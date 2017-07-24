package monkey.ast.expressions

import monkey.token.Token

/**
 * @author andrea
 * @since 7/23/17
 */
class BooleanExpression(private val token: Token, val value: Boolean) : Expression {

    override fun getTokenLiteral() = token.literal

    override fun toString() = token.literal
}