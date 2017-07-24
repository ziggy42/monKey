package monkey.lexer

import monkey.token.Token

/**
 * @author andrea
 * @since 7/18/17
 */
interface Lexer {
    fun nextToken(): Token
}