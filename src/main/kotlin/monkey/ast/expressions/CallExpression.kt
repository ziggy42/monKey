package monkey.ast.expressions

import monkey.token.Token

/**
 * @author andrea
 * @since 7/25/17
 */
class CallExpression(val token: Token, val function: Expression, val arguments: List<Expression>) : Expression {

    override fun getTokenLiteral() = token.literal

    override fun toString() = "$function (${arguments.joinToString(", ")})"
}