package monkey.evaluator

import com.winterbe.expekt.should
import monkey.`object`.Integer
import monkey.`object`.Null
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
            mapOf("5" to 5,
                    "10" to 10,
                    "-5" to -5,
                    "-10" to -10,
                    "5 + 5 + 5 + 5 - 10" to 10,
                    "2 * 2 * 2 * 2 * 2" to 32,
                    "-50 + 100 + -50" to 0,
                    "5 * 2 + 10" to 20,
                    "5 + 2 * 10" to 25,
                    "20 + 2 * -10" to 0,
                    "50 / 2 * 2 + 10" to 60,
                    "2 * (5 + 10)" to 30,
                    "3 * 3 * 3 + 10" to 37,
                    "3 * (3 * 3) + 10" to 37,
                    "(5 + 10 * 2 + 15 / 3) * 2 + -10" to 50)
                    .forEach { testIntegerObject(testEval(it.key), it.value) }
        }

        it("Test evaluation of boolean expressions") {
            mapOf("true" to true,
                    "false" to false,
                    "1 < 2" to true,
                    "1 > 2" to false,
                    "1 < 1" to false,
                    "1 > 1" to false,
                    "1 == 1" to true,
                    "1 != 1" to false,
                    "1 == 2" to false,
                    "1 != 2" to true,
                    "true == true" to true,
                    "false == false" to true,
                    "true == false" to false,
                    "true != false" to true,
                    "false != true" to true,
                    "(1 < 2) == true" to true,
                    "(1 < 2) == false" to false,
                    "(1 > 2) == true" to false,
                    "(1 > 2) == false" to true)
                    .forEach { testBooleanObject(testEval(it.key), it.value) }
        }

        it("Test evaluation of the ! operator") {
            mapOf("!true" to false,
                    "!false" to true,
                    "!5" to false,
                    "!!true" to true,
                    "!!false" to false,
                    "!!5" to true)
                    .forEach { testBooleanObject(testEval(it.key), it.value) }
        }

        it("Test conditionals") {
            mapOf("if (true) { 10 }" to 10,
                    "if (false) { 10 }" to Null(),
                    "if (1) { 10 }" to 10,
                    "if (1 < 2) { 10 }" to 10,
                    "if (1 > 2) { 10 }" to Null(),
                    "if (1 > 2) { 10 } else { 20 }" to 20,
                    "if (1 < 2) { 10 } else { 20 }" to 10)
                    .forEach {
                        val evaluated = testEval(it.key)
                        if (it.value is Null)
                            testNullObject(evaluated)
                        else
                            testIntegerObject(evaluated, it.value as Int)
                    }
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

fun testNullObject(obj: Object) {
    obj.should.`is`.instanceof(Null::class.java)
}