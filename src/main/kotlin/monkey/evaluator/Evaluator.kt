package monkey.evaluator

import monkey.`object`.*
import monkey.ast.Node
import monkey.ast.Program
import monkey.ast.expressions.*
import monkey.ast.statements.*

/**
 * @author andrea
 * @since 7/29/17
 */
object Evaluator {

    private val TRUE = MonkeyBoolean(true)
    private val FALSE = MonkeyBoolean(false)
    private val NULL = MonkeyNull()

    fun eval(node: Node, environment: Environment): MonkeyObject = when (node::class) {
        Program::class -> evalProgram((node as Program), environment)
        ExpressionStatement::class -> eval((node as ExpressionStatement).expression, environment)
        IntegerLiteralExpression::class -> MonkeyInteger((node as IntegerLiteralExpression).value)
        StringLiteralExpression::class -> MonkeyString((node as StringLiteralExpression).value)
        BooleanExpression::class -> nativeBoolToBooleanObject((node as BooleanExpression).value)
        PrefixExpression::class -> run {
            val right = eval((node as PrefixExpression).right, environment)
            if (isError(right))
                return right

            evalPrefixExpression(node.operator, right)
        }
        InfixExpression::class -> run {
            val left = eval((node as InfixExpression).left, environment)
            if (isError(left))
                return left

            val right = eval(node.right, environment)
            if (isError(right))
                return right

            return evalInfixExpression(node.operator, left, right)
        }
        BlockStatement::class -> evalStatements((node as BlockStatement).statements, environment)
        IfExpression::class -> evalIfExpression(node as IfExpression, environment)
        ReturnStatement::class -> run {
            val returnValue = eval((node as ReturnStatement).returnValue, environment)
            if (isError(returnValue))
                return returnValue

            return ReturnValue(returnValue)
        }
        LetStatement::class -> run {
            val value = eval((node as LetStatement).value, environment)
            if (isError(value))
                return value

            return environment.set(node.name.value, value)
        }
        IdentifierExpression::class -> evalIdentifier(node as IdentifierExpression, environment)
        FunctionLiteralExpression::class -> {
            val parameters = (node as FunctionLiteralExpression).parameters
            val body = node.body
            MonkeyFunction(parameters, body, environment)
        }
        CallExpression::class -> run {
            val function = eval((node as CallExpression).function, environment)
            if (isError(function))
                return function

            val args = evalExpressions(node.arguments, environment)
            if (args.size == 1 && isError(args[0]))
                return args[0]

            return applyFunction(function, args)
        }
        ArrayLiteralExpression::class -> run {
            val elements = evalExpressions((node as ArrayLiteralExpression).elements, environment)
            if (elements.size == 1 && isError(elements[0]))
                return elements.first()

            return MonkeyArray(elements)
        }
        IndexExpression::class -> run {
            val left = eval((node as IndexExpression).left, environment)
            if (isError(left))
                return left

            val index = eval(node.index, environment)
            if (isError(index))
                return index

            return evalIndexExpression(left, index)
        }
        else -> throw RuntimeException("Unknown node implementation ${node.javaClass}")
    }

    private fun evalExpressions(arguments: List<Expression>, environment: Environment): List<MonkeyObject> =
            arguments.map {
                val evaluated = eval(it, environment)
                if (isError(evaluated))
                    return listOf(evaluated)
                evaluated
            }

    private fun evalProgram(program: Program, environment: Environment): MonkeyObject {
        var result: MonkeyObject = NULL
        program.statements.forEach {
            result = eval(it, environment)

            @Suppress("NON_EXHAUSTIVE_WHEN")
            when (result.type) {
                ObjectType.RETURN_VALUE -> return (result as ReturnValue).value
                ObjectType.ERROR -> return result
            }
        }

        return result
    }

    private fun evalIfExpression(expression: IfExpression, environment: Environment): MonkeyObject {
        val condition = eval(expression.condition, environment)
        if (isError(condition))
            return condition

        if (isTruthy(condition))
            return eval(expression.consequence, environment)
        else if (expression.alternative != null)
            return eval(expression.alternative, environment)
        return NULL
    }

    private fun evalStatements(statements: List<Statement>, environment: Environment): MonkeyObject {
        var result: MonkeyObject = NULL
        statements.forEach {
            result = eval(it, environment)
            if (result.type == ObjectType.RETURN_VALUE || result.type == ObjectType.ERROR)
                return result
        }

        return result
    }

    private fun evalIdentifier(identifier: IdentifierExpression, environment: Environment): MonkeyObject {
        val monkeyObject = environment.get(identifier.value)
        if (monkeyObject != null)
            return monkeyObject

        val builtin = BUILTINS[identifier.value]
        if (builtin != null)
            return builtin

        return MonkeyError("identifier not found: ${identifier.value}")
    }

    private fun evalInfixExpression(operator: String, left: MonkeyObject, right: MonkeyObject): MonkeyObject {
        if (left.type == ObjectType.INTEGER && right.type == ObjectType.INTEGER)
            return evalInfixIntegerExpression(operator, left as MonkeyInteger, right as MonkeyInteger)

        if (left.type == ObjectType.BOOLEAN && right.type == ObjectType.BOOLEAN)
            return evalInfixBooleanExpression(operator, left as MonkeyBoolean, right as MonkeyBoolean)

        if (left.type == ObjectType.STRING && right.type == ObjectType.STRING)
            return evalInfixStringExpression(operator, left as MonkeyString, right as MonkeyString)

        return MonkeyError("type mismatch: ${left.type} $operator ${right.type}")
    }

    private fun evalInfixStringExpression(operator: String, left: MonkeyString, right: MonkeyString) =
            when (operator) {
                "+" -> MonkeyString(left.value + right.value)
                else -> MonkeyError("unknown operator: ${left.type} $operator ${right.type}")
            }

    private fun evalInfixBooleanExpression(operator: String, left: MonkeyBoolean, right: MonkeyBoolean) =
            when (operator) {
                "==" -> nativeBoolToBooleanObject(left === right)
                "!=" -> nativeBoolToBooleanObject(left !== right)
                else -> MonkeyError("unknown operator: ${left.type} $operator ${right.type}")
            }

    private fun evalInfixIntegerExpression(operator: String, left: MonkeyInteger, right: MonkeyInteger) =
            when (operator) {
                "+" -> MonkeyInteger(left.value + right.value)
                "-" -> MonkeyInteger(left.value - right.value)
                "*" -> MonkeyInteger(left.value * right.value)
                "/" -> MonkeyInteger(left.value / right.value)
                ">" -> nativeBoolToBooleanObject(left.value > right.value)
                "<" -> nativeBoolToBooleanObject(left.value < right.value)
                "==" -> nativeBoolToBooleanObject(left.value == right.value)
                "!=" -> nativeBoolToBooleanObject(left.value != right.value)
                else -> MonkeyError("unknown operator: ${left.type} $operator ${right.type}")
            }

    private fun evalPrefixExpression(operator: String, right: MonkeyObject) = when (operator) {
        "!" -> evalBangOperatorExpression(right)
        "-" -> evalMinusPrefixOperatorExpression(right)
        else -> MonkeyError("unknown operator: $operator${right.type}")
    }

    private fun evalMinusPrefixOperatorExpression(right: MonkeyObject) = if (right !is MonkeyInteger)
        MonkeyError("unknown operator: -${right.type}")
    else MonkeyInteger(-right.value)

    private fun evalBangOperatorExpression(right: MonkeyObject) = when (right) {
        TRUE -> FALSE
        FALSE -> TRUE
        NULL -> TRUE
        else -> FALSE
    }

    private fun evalIndexExpression(left: MonkeyObject, index: MonkeyObject): MonkeyObject {
        if (left.type == ObjectType.ARRAY && index.type == ObjectType.INTEGER)
            return evalArrayIndexExpression(left as MonkeyArray, index as MonkeyInteger)

        return MonkeyError("index operator not supported: ${left.type}")
    }

    private fun evalArrayIndexExpression(left: MonkeyArray, index: MonkeyInteger): MonkeyObject {
        if (index.value < 0 || index.value > left.elements.size - 1)
            return NULL
        return left.elements[index.value]
    }

    private fun applyFunction(function: MonkeyObject, args: List<MonkeyObject>) = when (function::class) {
        MonkeyFunction::class -> {
            val env = extendFunctionEnv(function as MonkeyFunction, args)
            val evaluated = eval(function.body, env)
            unwrapReturnValue(evaluated)
        }
        MonkeyBuiltin::class -> (function as MonkeyBuiltin).function(args)
        else -> MonkeyError("not a function: $function")
    }

    private fun unwrapReturnValue(evaluated: MonkeyObject) = (evaluated as? ReturnValue)?.value ?: evaluated

    private fun extendFunctionEnv(function: MonkeyFunction, arguments: List<MonkeyObject>): Environment {
        val env = function.env.newEnclosedEnvironment()
        function.parameters.forEachIndexed { index, param -> env.set(param.value, arguments[index]) }
        return env
    }

    private fun nativeBoolToBooleanObject(boolean: kotlin.Boolean) = if (boolean) TRUE else FALSE

    private fun isTruthy(obj: MonkeyObject) = when (obj) {
        NULL -> false
        TRUE -> true
        FALSE -> false
        else -> true
    }

    private fun isError(obj: MonkeyObject) = obj.type == ObjectType.ERROR
}