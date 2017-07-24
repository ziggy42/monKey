package monkey.ast.statements

import monkey.ast.expressions.Expression
import monkey.token.Token

/**
 * Example: 1 + 2
 * Token is just the first token of the expression
 * @author andrea
 * @since 7/18/17
 */
data class ExpressionStatement(private val token: Token, val expression: Expression) : Statement {

    override fun getTokenLiteral() = token.literal

    override fun toString() = expression.toString()
}