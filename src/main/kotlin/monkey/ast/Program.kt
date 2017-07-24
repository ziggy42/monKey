package monkey.ast

import monkey.ast.statements.Statement

/**
 * The root node of an AST
 * @author andrea
 * @since 7/18/17
 */
data class Program(val statements: List<Statement>) : Node {

    override fun getTokenLiteral() = if (statements.isNotEmpty()) statements[0].getTokenLiteral() else ""

    override fun toString() = statements.map { it.toString() }.joinToString("\n")
}