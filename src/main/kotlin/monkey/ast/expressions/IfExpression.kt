package monkey.ast.expressions

import monkey.ast.statements.BlockStatement
import monkey.token.Token

/**
 * @author andrea
 * @since 7/23/17
 */
class IfExpression(
        val token: Token,
        val condition: Expression,
        val consequence: BlockStatement,
        val alternative: BlockStatement? = null) : Expression {

    override fun getTokenLiteral() = token.literal

    override fun toString(): String {
        val base = "if ($condition) $consequence"
        return if (alternative != null) "$base else $alternative" else base
    }
}