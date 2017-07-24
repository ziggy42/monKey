package monkey.ast.expressions

import monkey.token.Token

/**
 * @author andrea
 * @since 7/18/17
 */
data class InfixExpression(private val token: Token, val operator: String, val left: Expression, val right: Expression)
    : Expression {

    override fun getTokenLiteral() = token.literal

    override fun toString() = "($left $operator $right)"
}