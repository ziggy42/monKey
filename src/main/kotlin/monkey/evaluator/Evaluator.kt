package monkey.evaluator

import monkey.`object`.*
import monkey.`object`.Boolean
import monkey.ast.Node
import monkey.ast.Program
import monkey.ast.expressions.*
import monkey.ast.statements.BlockStatement
import monkey.ast.statements.ExpressionStatement
import monkey.ast.statements.ReturnStatement
import monkey.ast.statements.Statement

/**
 * @author andrea
 * @since 7/29/17
 */
object Evaluator {
    private val TRUE = Boolean(true)
    private val FALSE = Boolean(false)
    private val NULL = Null()

    fun eval(node: Node): Object = when (node::class) {
        Program::class -> evalProgram((node as Program))
        ExpressionStatement::class -> eval((node as ExpressionStatement).expression)
        IntegerLiteralExpression::class -> Integer((node as IntegerLiteralExpression).value)
        BooleanExpression::class -> nativeBoolToBooleanObject((node as BooleanExpression).value)
        PrefixExpression::class -> {
            val prefixExpression = node as PrefixExpression
            val right = eval(prefixExpression.right)
            if (isError(right))
                right
            else
                evalPrefixExpression(prefixExpression.operator, right)
        }
        InfixExpression::class -> {
            val infixExpression = node as InfixExpression
            val left = eval(infixExpression.left)
            if (isError(left))
                left
            else {
                val right = eval(infixExpression.right)
                if (isError(right))
                    right
                else
                    evalInfixExpression(infixExpression.operator, left, right)
            }
        }
        BlockStatement::class -> evalStatements((node as BlockStatement).statements)
        IfExpression::class -> evalIfExpression(node as IfExpression)
        ReturnStatement::class -> {
            val returnValue = eval((node as ReturnStatement).returnValue)
            if (isError(returnValue))
                returnValue
            else
                ReturnValue(returnValue)
        }
        else -> throw RuntimeException("Unknown node implementation ${node.javaClass}")
    }

    private fun evalProgram(program: Program): Object {
        var result: Object = NULL
        program.statements.forEach {
            result = eval(it)

            @Suppress("NON_EXHAUSTIVE_WHEN")
            when (result.type) {
                ObjectType.RETURN_VALUE -> return (result as ReturnValue).value
                ObjectType.ERROR -> return result
            }
        }

        return result
    }

    private fun evalIfExpression(expression: IfExpression): Object {
        val condition = eval(expression.condition)
        if (isError(condition))
            return condition

        if (isTruthy(condition))
            return eval(expression.consequence)
        else if (expression.alternative != null)
            return eval(expression.alternative)
        return NULL
    }

    private fun evalStatements(statements: List<Statement>): Object {
        var result: Object = NULL
        statements.forEach {
            result = eval(it)
            if (result.type == ObjectType.RETURN_VALUE || result.type == ObjectType.ERROR)
                return result
        }

        return result
    }

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

    private fun nativeBoolToBooleanObject(boolean: kotlin.Boolean) = if (boolean) TRUE else FALSE

    private fun isTruthy(obj: Object) = when (obj) {
        NULL -> false
        TRUE -> true
        FALSE -> false
        else -> true
    }

    private fun isError(obj: Object) = obj.type == ObjectType.ERROR
}