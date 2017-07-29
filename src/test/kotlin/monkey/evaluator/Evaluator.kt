package monkey.evaluator

import com.winterbe.expekt.should
import monkey.`object`.Integer
import monkey.`object`.Object
import monkey.ast.Parser
import monkey.lexer.StringLexer
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

/**
 * @author andrea
 * @since 7/29/17
 */
object EvaluatorTest : Spek({
    describe("evaluator") {
        it("Test evaluation of integer expressions") {
            mapOf("5" to 5, "10" to 10).forEach { testIntegerObject(testEval(it.key), it.value) }
        }

        it("Test evaluation of boolean expressions") {
            mapOf("true" to true, "false" to false).forEach { testBooleanObject(testEval(it.key), it.value) }
        }
    }
})

fun testEval(input: String): Object {
    val program = Parser(StringLexer(input)).parseProgram()
    return Evaluator.eval(program)
}

fun testIntegerObject(obj: Object, expected: Int) {
    obj.should.be.instanceof(Integer::class.java)
    (obj as Integer).value.should.be.equal(expected)
}

fun testBooleanObject(obj: Object, expected: Boolean) {
    obj.should.be.instanceof(monkey.`object`.Boolean::class.java)
    (obj as monkey.`object`.Boolean).value.should.be.equal(expected)
}