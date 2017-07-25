package monkey.repl

import com.andreapivetta.kolor.blue
import com.andreapivetta.kolor.red
import monkey.ast.Parser
import monkey.lexer.StringLexer


/**
 * @author andrea
 * @since 7/17/17
 */
object REPL {
    private val PROMPT = ">> ".blue()
    private val MONKEY_FACE = """
            __,__
   .--.  .-"     "-.  .--.
  / .. \/  .-. .-.  \/ .. \
 | |  '|  /   Y   \  |'  | |
 | \   \  \ 0 | 0 /  /   / |
  \ '- ,\.-'''''''-./, -' /
   ''-' /_   ^ ^   _\ '-''
       |  \._   _./  |
       \   \ '~' /   /
        '._ '-=-' _.'
           '-----'
	""".red()

    fun start() {
        while (true) {
            print(PROMPT)
            try {
                val parser = Parser(StringLexer(readLine()!!))
                println(parser.parseProgram())
            } catch (error: Exception) {
                println(MONKEY_FACE)
                println(error.message?.red())
            }
        }
    }
}
