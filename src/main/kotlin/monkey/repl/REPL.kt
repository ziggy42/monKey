package monkey.repl

import com.andreapivetta.kolor.blue
import com.andreapivetta.kolor.red
import monkey.`object`.Environment
import monkey.ast.Parser
import monkey.evaluator.Evaluator
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
        val environment = Environment()
        while (true) {
            print(PROMPT)
            try {
                val line = readLine()
                if (line?.isNotEmpty() == true) {
                    val program = Parser(StringLexer(line)).parseProgram()
                    println(Evaluator.eval(program, environment).inspect())
                }
            } catch (error: Exception) {
                println(MONKEY_FACE)
                println(error.message?.red())
            }
        }
    }
}
