package monkey.`object`

import monkey.ast.expressions.IdentifierExpression
import monkey.ast.statements.BlockStatement

/**
 * @author andrea
 * @since 7/29/17
 */
class Function(
        val parameters: List<IdentifierExpression>,
        val body: BlockStatement,
        val env: Environment) : Object(ObjectType.FUNCTION) {

    override fun inspect() = "fn (${parameters.map { it.value }.joinToString(", ")}) {\n$body\n}"
}