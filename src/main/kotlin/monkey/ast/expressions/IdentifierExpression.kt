package monkey.ast.expressions

import monkey.token.Token

/**
 * @author andrea
 * @since 7/18/17
 */
data class IdentifierExpression(private val token: Token, val value: String) : Expression {

    override fun getTokenLiteral() = token.literal

    override fun toString() = this.value
}