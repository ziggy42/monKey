package monkey.ast.expressions

import monkey.token.Token

/**
 * @author andrea
 * @since 7/30/17
 */
class IndexExpression(val token: Token, val left: Expression, val index: Expression) : Expression {

    override fun getTokenLiteral() = token.literal

    override fun toString(): String = "($left[$index])"
}