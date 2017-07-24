package monkey.ast.statements

import monkey.ast.expressions.Expression
import monkey.token.Token

/**
 * @author andrea
 * @since 7/18/17
 */
data class ReturnStatement(private val token: Token, val returnValue: Expression) : Statement {

    override fun getTokenLiteral(): String = token.literal

    override fun toString() = "${token.literal} $returnValue;"
}