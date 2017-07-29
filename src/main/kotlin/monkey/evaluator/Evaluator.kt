package monkey.evaluator

import monkey.`object`.*
import monkey.`object`.Boolean
import monkey.`object`.Function
import monkey.ast.Node
import monkey.ast.Program
import monkey.ast.expressions.*
import monkey.ast.statements.*

/**
 * @author andrea
 * @since 7/29/17
 */
object Evaluator {
    private val TRUE = Boolean(true)
    private val FALSE = Boolean(false)
    private val NULL = Null()

    fun eval(node: Node, environment: Environment): Object = when (node::class) {
        Program::class -> evalProgram((node as Program), environment)
        ExpressionStatement::class -> eval((node as ExpressionStatement).expression, environment)
        IntegerLiteralExpression::class -> Integer((node as IntegerLiteralExpression).value)
        BooleanExpression::class -> nativeBoolToBooleanObject((node as BooleanExpression).value)
        PrefixExpression::class -> {
            val prefixExpression = node as PrefixExpression
            val right = eval(prefixExpression.right, environment)
            if (isError(right))
                right
            else
                evalPrefixExpression(prefixExpression.operator, right)
        }
        InfixExpression::class -> {
            val infixExpression = node as InfixExpression
            val left = eval(infixExpression.left, environment)
            if (isError(left))
                left
            else {
                val right = eval(infixExpression.right, environment)
                if (isError(right))
                    right
                else
                    evalInfixExpression(infixExpression.operator, left, right)
            }
        }
        BlockStatement::class -> evalStatements((node as BlockStatement).statements, environment)
        IfExpression::class -> evalIfExpression(node as IfExpression, environment)
        ReturnStatement::class -> {
            val returnValue = eval((node as ReturnStatement).returnValue, environment)
            if (isError(returnValue))
                returnValue
            else
                ReturnValue(returnValue)
        }
        LetStatement::class -> {
            val letStatement = node as LetStatement
            val value = eval(letStatement.value, environment)
            if (isError(value))
                value
            else {
                environment.set(letStatement.name.value, value)
            }
        }
        IdentifierExpression::class -> evalIdentifier(node as IdentifierExpression, environment)
        FunctionLiteralExpression::class -> {
            val parameters = (node as FunctionLiteralExpression).parameters
            val body = node.body
            Function(parameters, body, environment)
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
        else -> throw RuntimeException("Unknown node implementation ${node.javaClass}")
    }

    private fun evalExpressions(arguments: List<Expression>, environment: Environment): List<Object> =
            arguments.map {
                val evaluated = eval(it, environment)
                if (isError(evaluated))
                    return listOf(evaluated)
                evaluated
            }

    private fun evalProgram(program: Program, environment: Environment): Object {
        var result: Object = NULL
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

    private fun evalIfExpression(expression: IfExpression, environment: Environment): Object {
        val condition = eval(expression.condition, environment)
        if (isError(condition))
            return condition

        if (isTruthy(condition))
            return eval(expression.consequence, environment)
        else if (expression.alternative != null)
            return eval(expression.alternative, environment)
        return NULL
    }

    private fun evalStatements(statements: List<Statement>, environment: Environment): Object {
        var result: Object = NULL
        statements.forEach {
            result = eval(it, environment)
            if (result.type == ObjectType.RETURN_VALUE || result.type == ObjectType.ERROR)
                return result
        }

        return result
    }

    private fun evalIdentifier(identifier: IdentifierExpression, environment: Environment) =
            environment.get(identifier.value) ?: Error("identifier not found: ${identifier.value}")

    private fun evalInfixExpression(operator: String, left: Object, right: Object): Object {
        if (left.type == ObjectType.INTEGER && right.type == ObjectType.INTEGER)
            return evalInfixIntegerExpression(operator, left as Integer, right as Integer)

        if (left.type == ObjectType.BOOLEAN && right.type == ObjectType.BOOLEAN)
            return evalInfixBooleanExpression(operator, left as Boolean, right as Boolean)

        return Error("type mismatch: ${left.type} $operator ${right.type}")
    }

    private fun evalInfixBooleanExpression(operator: String, left: Boolean, right: Boolean) = when (operator) {
        "==" -> nativeBoolToBooleanObject(left === right)
        "!=" -> nativeBoolToBooleanObject(left !== right)
        else -> Error("unknown operator: ${left.type} $operator ${right.type}")
    }

    private fun evalInfixIntegerExpression(operator: String, left: Integer, right: Integer) = when (operator) {
        "+" -> Integer(left.value + right.value)
        "-" -> Integer(left.value - right.value)
        "*" -> Integer(left.value * right.value)
        "/" -> Integer(left.value / right.value)
        ">" -> nativeBoolToBooleanObject(left.value > right.value)
        "<" -> nativeBoolToBooleanObject(left.value < right.value)
        "==" -> nativeBoolToBooleanObject(left.value == right.value)
        "!=" -> nativeBoolToBooleanObject(left.value != right.value)
        else -> Error("unknown operator: ${left.type} $operator ${right.type}")
    }

    private fun evalPrefixExpression(operator: String, right: Object) = when (operator) {
        "!" -> evalBangOperatorExpression(right)
        "-" -> evalMinusPrefixOperatorExpression(right)
        else -> Error("unknown operator: $operator${right.type}")
    }

    private fun evalMinusPrefixOperatorExpression(right: Object): Object =
            if (right !is Integer)
                Error("unknown operator: -${right.type}")
            else Integer(-right.value)

    private fun evalBangOperatorExpression(right: Object) = when (right) {
        TRUE -> FALSE
        FALSE -> TRUE
        NULL -> TRUE
        else -> FALSE
    }

    private fun applyFunction(function: Object, args: List<Object>): Object {
        if (function !is Function)
            return Error("not a function: $function")

        val env = extendFunctionEnv(function, args)
        val evaluated = eval(function.body, env)
        return unwrapReturnValue(evaluated)
    }

    private fun unwrapReturnValue(evaluated: Object): Object = (evaluated as? ReturnValue)?.value ?: evaluated

    private fun extendFunctionEnv(function: Function, arguments: List<Object>): Environment {
        val env = function.env.newEnclosedEnvironment()
        function.parameters.forEachIndexed { index, param -> env.set(param.value, arguments[index]) }
        return env
    }

    private fun nativeBoolToBooleanObject(boolean: kotlin.Boolean) = if (boolean) TRUE else FALSE

    private fun isTruthy(obj: Object) = when (obj) {
        NULL -> false
        TRUE -> true
        FALSE -> false
        else -> true
    }

    private fun isError(obj: Object) = obj.type == ObjectType.ERROR
}