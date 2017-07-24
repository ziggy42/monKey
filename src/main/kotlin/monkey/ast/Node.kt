package monkey.ast

/**
 * An element of the AST
 * @author andrea
 * @since 7/18/17
 */
interface Node {
    fun getTokenLiteral(): String
}