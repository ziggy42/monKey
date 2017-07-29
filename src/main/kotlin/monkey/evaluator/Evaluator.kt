package monkey.evaluator

import monkey.`object`.Boolean
import monkey.`object`.Integer
import monkey.`object`.Null
import monkey.`object`.Object
import monkey.ast.Node
import monkey.ast.Program
import monkey.ast.expressions.BooleanExpression
import monkey.ast.expressions.IntegerLiteralExpression
import monkey.ast.statements.ExpressionStatement
import monkey.ast.statements.Statement

/**
 * @author andrea
 * @since 7/29/17
 */
object Evaluator {

    fun eval(node: Node): Object = when (node::class) {
        Program::class -> evalStatements((node as Program).statements)
        ExpressionStatement::class -> eval((node as ExpressionStatement).expression)
        IntegerLiteralExpression::class -> Integer((node as IntegerLiteralExpression).value)
        BooleanExpression::class -> Boolean((node as BooleanExpression).value)
        else -> throw RuntimeException("Unknown node implementation ${node.javaClass}")
    }

    private fun evalStatements(statements: List<Statement>): Object {
        var result: Object = Null()
        // TODO why
        statements.forEach { result = eval(it) }
        return result
    }
}