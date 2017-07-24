package monkey.ast.expressions

import monkey.token.Token

/**
 * @author andrea
 * @since 7/18/17
 */
class IntegerLiteralExpression(private val token: Token, val value: Int) : Expression {

    override fun getTokenLiteral() = token.literal

    override fun toString() = this.token.literal
}