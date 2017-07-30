package monkey.ast.expressions

import monkey.token.Token

/**
 * @author andrea
 * @since 7/30/17
 */
class HashLiteralExpression(val token: Token, val pairs: Map<Expression, Expression>) : Expression {

    override fun getTokenLiteral() = token.literal

    override fun toString() = "{${pairs.map { "${it.key}:${it.value}" }.joinToString(", ")}}"
}