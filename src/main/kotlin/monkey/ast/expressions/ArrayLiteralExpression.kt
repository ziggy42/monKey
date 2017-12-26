package monkey.ast.expressions

import monkey.token.Token

/**
 * @author andrea
 * @since 7/30/17
 */
class ArrayLiteralExpression(private val token: Token, val elements: List<Expression>) : Expression {

    override fun getTokenLiteral() = token.literal

    override fun toString() = "[${elements.joinToString(", ") { it.toString() }}]"
}