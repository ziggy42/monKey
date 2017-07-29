package monkey.evaluator

import monkey.`object`.*
import monkey.`object`.Boolean
import monkey.ast.Node
import monkey.ast.Program
import monkey.ast.expressions.BooleanExpression
import monkey.ast.expressions.InfixExpression
import monkey.ast.expressions.IntegerLiteralExpression
import monkey.ast.expressions.PrefixExpression
import monkey.ast.statements.ExpressionStatement
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
        Program::class -> evalStatements((node as Program).statements)
        ExpressionStatement::class -> eval((node as ExpressionStatement).expression)
        IntegerLiteralExpression::class -> Integer((node as IntegerLiteralExpression).value)
        BooleanExpression::class -> if ((node as BooleanExpression).value) TRUE else FALSE
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
        else -> throw RuntimeException("Unknown node implementation ${node.javaClass}")
    }

    private fun evalStatements(statements: List<Statement>): Object {
        var result: Object = NULL
        // TODO why
        statements.forEach { result = eval(it) }
        return result
    }

    private fun evalInfixExpression(operator: String, left: Object, right: Object): Object {
        if (left.type == ObjectType.INTEGER && right.type == ObjectType.INTEGER)
            return evalInfixIntegerExpression(operator, left as Integer, right as Integer)

        throw RuntimeException("Unsupported operator '$operator' between $left and $right")
    }

    private fun evalInfixIntegerExpression(operator: String, left: Integer, right: Integer) = when (operator) {
        "+" -> Integer(left.value + right.value)
        "-" -> Integer(left.value - right.value)
        "*" -> Integer(left.value * right.value)
        "/" -> Integer(left.value / right.value)
        else -> throw RuntimeException("Unsupported operator $operator")
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
}