package monkey.ast.statements

import monkey.token.Token

/**
 * @author andrea
 * @since 7/23/17
 */
class BlockStatement(private val token: Token, val statements: List<Statement>) : Statement {

    override fun getTokenLiteral() = token.literal

    override fun toString() = statements.map { it.toString() }.joinToString("\n")
}