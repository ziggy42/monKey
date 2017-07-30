package monkey

import com.andreapivetta.kolor.red
import monkey.`object`.Environment
import monkey.`object`.MonkeyError
import monkey.ast.Parser
import monkey.evaluator.Evaluator
import monkey.lexer.StringLexer
import monkey.repl.REPL
import java.io.File

/**
 * @author andrea
 * @since 7/18/17
 */
fun main(args: Array<String>) {
    if (args.size == 1) {
        val input = File(args[0])
        if (input.exists()) {
            val code = input.inputStream().bufferedReader().use { it.readText() }
            val evaluated = Evaluator.eval(Parser(StringLexer(code)).parseProgram(), Environment())
            if (evaluated is MonkeyError)
                println(evaluated.inspect())
        } else println("File not found: ${args[0]}".red())
    } else {
        REPL.start()
    }
}