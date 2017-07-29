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
            evalPrefixExpression(prefixExpression.operator, right)
        }
        InfixExpression::class -> {
            val infixExpression = node as InfixExpression
            val left = eval(infixExpression.left)
            val right = eval(infixExpression.right)
            evalInfixExpression(infixExpression.operator, left, right)
        }
        BlockStatement::class -> evalStatements((node as BlockStatement).statements)
        IfExpression::class -> evalIfExpression(node as IfExpression)
        ReturnStatement::class -> {
            val returnValue = eval((node as ReturnStatement).returnValue)
            ReturnValue(returnValue)
        }
        else -> throw RuntimeException("Unknown node implementation ${node.javaClass}")
    }

    private fun evalProgram(program: Program): Object {
        var result: Object = NULL
        program.statements.forEach {
            result = eval(it)
            if (result.type == ObjectType.RETURN_VALUE)
                return (result as ReturnValue).value
        }

        return result
    }

    private fun evalIfExpression(expression: IfExpression): Object {
        val condition = eval(expression.condition)
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
            if (result.type == ObjectType.RETURN_VALUE)
                return result
        }

        return result
    }

    private fun evalInfixExpression(operator: String, left: Object, right: Object): Object {
        if (left.type == ObjectType.INTEGER && right.type == ObjectType.INTEGER)
            return evalInfixIntegerExpression(operator, left as Integer, right as Integer)

        if (left.type == ObjectType.BOOLEAN && right.type == ObjectType.BOOLEAN)
            return evalInfixBooleanExpression(operator, left as Boolean, right as Boolean)

        throw RuntimeException("Unsupported operator '$operator' between $left and $right")
    }

    private fun evalInfixBooleanExpression(operator: String, left: Boolean, right: Boolean) = when (operator) {
        "==" -> nativeBoolToBooleanObject(left === right)
        "!=" -> nativeBoolToBooleanObject(left !== right)
        else -> throw RuntimeException("Unsupported operator $operator between booleans")
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
        else -> throw RuntimeException("Unsupported operator $operator between integers")
    }

    private fun evalPrefixExpression(operator: String, right: Object) = when (operator) {
        "!" -> evalBangOperatorExpression(right)
        "-" -> evalMinusPrefixOperatorExpression(right)
        else -> throw RuntimeException("Unsupported operator $operator")
    }

    private fun evalMinusPrefixOperatorExpression(right: Object): Object {
        if (right !is Integer)
            throw RuntimeException("Expecting an Integer but got $right")
        return Integer(-right.value)
    }

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
}