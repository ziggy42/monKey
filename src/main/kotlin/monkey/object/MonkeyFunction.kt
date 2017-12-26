package monkey.`object`

import monkey.ast.expressions.IdentifierExpression
import monkey.ast.statements.BlockStatement

/**
 * @author andrea
 * @since 7/29/17
 */
data class MonkeyFunction(
        val parameters: List<IdentifierExpression>,
        val body: BlockStatement,
        val env: Environment) : MonkeyObject(ObjectType.FUNCTION) {

    override fun inspect() = "fn (${parameters.joinToString(", ") { it.value }}) {\n\t$body\n}"
}