package monkey.ast.statements

import monkey.ast.expressions.Expression
import monkey.ast.expressions.IdentifierExpression
import monkey.token.Token

/**
 * @author andrea
 * @since 7/18/17
 */
data class LetStatement(private val token: Token, val name: IdentifierExpression, val value: Expression) : Statement {

    override fun getTokenLiteral() = token.literal

    override fun toString() = "${token.literal} $name = $value;"

}