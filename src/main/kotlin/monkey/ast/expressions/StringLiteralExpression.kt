package monkey.ast.expressions

import monkey.token.Token

/**
 * @author andrea
 * @since 7/29/17
 */
class StringLiteralExpression(private val token: Token, val value: String) : Expression {

    override fun getTokenLiteral() = token.literal

    override fun toString() = this.token.literal
}