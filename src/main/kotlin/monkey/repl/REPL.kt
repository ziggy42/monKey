package monkey.repl

import com.andreapivetta.kolor.blue
import monkey.lexer.StringLexer
import monkey.token.TokenType


/**
 * @author andrea
 * @since 7/17/17
 */
object REPL {
    private val PROMPT = ">> ".blue()

    fun start() {
        while (true) {
            print(PROMPT)
            val lexer = StringLexer(readLine()!!)
            var token = lexer.nextToken()
            while (token.tokenType !== TokenType.EOF) {
                println(token)
                token = lexer.nextToken()
            }
        }
    }
}