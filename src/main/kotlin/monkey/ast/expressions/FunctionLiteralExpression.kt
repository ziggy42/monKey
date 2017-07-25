package monkey.ast.expressions

import monkey.ast.statements.BlockStatement
import monkey.token.Token

/**
 * @author andrea
 * @since 7/25/17
 */
class FunctionLiteralExpression(
        val token: Token,
        val parameters: List<IdentifierExpression>,
        val body: BlockStatement) : Expression {

    override fun getTokenLiteral() = token.literal

    override fun toString() = "${token.literal} (${parameters.joinToString(", ")})$body)"
}