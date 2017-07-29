package monkey.evaluator

import com.winterbe.expekt.should
import monkey.`object`.*
import monkey.`object`.Function
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

        it("Test evaluation of string expressions") {
            mapOf(""" "foo" """ to "foo",
                    """ "bar" """ to "bar",
                    """ "foo" + "bar" """ to "foobar")
                    .forEach { testStringObject(testEval(it.key), it.value) }
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
                    "if (false) { 10 }" to MonkeyNull(),
                    "if (1) { 10 }" to 10,
                    "if (1 < 2) { 10 }" to 10,
                    "if (1 > 2) { 10 }" to MonkeyNull(),
                    "if (1 > 2) { 10 } else { 20 }" to 20,
                    "if (1 < 2) { 10 } else { 20 }" to 10)
                    .forEach {
                        val evaluated = testEval(it.key)
                        if (it.value is MonkeyNull)
                            testNullObject(evaluated)
                        else
                            testIntegerObject(evaluated, it.value as Int)
                    }
        }

        it("Test return statements") {
            mapOf("return 10;" to 10,
                    "return 10; 9;" to 10,
                    "return 2 * 5; 9;" to 10,
                    "9; return 2 * 5; 9;" to 10,
                    "if (10 > 1) { if (10 > 1) { return 10; } return 1; }" to 10)
                    .forEach { testIntegerObject(testEval(it.key), it.value) }
        }

        it("Test error handling") {
            mapOf("5 + true;" to "type mismatch: INTEGER + BOOLEAN",
                    "5 + true; 5;" to "type mismatch: INTEGER + BOOLEAN",
                    "-true" to "unknown operator: -BOOLEAN",
                    "true + false;" to "unknown operator: BOOLEAN + BOOLEAN",
                    "5; true + false; 5" to "unknown operator: BOOLEAN + BOOLEAN",
                    "if (10 > 1) { true + false; }" to "unknown operator: BOOLEAN + BOOLEAN",
                    "if (10 > 1) { if (10 > 1) { return true + false; } return 1; }" to
                            "unknown operator: BOOLEAN + BOOLEAN",
                    "foobar" to "identifier not found: foobar",
                    """ "Hello" - "World" """ to "unknown operator: STRING - STRING")
                    .forEach { testError(testEval(it.key), it.value) }
        }

        it("Test let statements") {
            mapOf("let a = 5; a;" to 5,
                    "let a = 5 * 5; a;" to 25,
                    "let a = 5; let b = a; b;" to 5,
                    "let a = 5; let b = a; let c = a + b + 5; c;" to 15)
                    .forEach { testIntegerObject(testEval(it.key), it.value) }
        }

        it("Test function application") {
            mapOf(
                    "let identity = fn(x) { x; }; identity(5);" to 5,
                    "let identity = fn(x) { return x; }; identity(5);" to 5,
                    "let double = fn(x) { x * 2; }; double(5);" to 10,
                    "let add = fn(x, y) { x + y; }; add(5, 5);" to 10,
                    "let add = fn(x, y) { x + y; }; add(5 + 5, add(5, 5));" to 20,
                    "fn(x) { x; }(5)" to 5)
                    .forEach { testIntegerObject(testEval(it.key), it.value) }
        }
    }
})

fun testEval(input: String): MonkeyObject {
    val environment = Environment()
    val program = Parser(StringLexer(input)).parseProgram()
    return Evaluator.eval(program, environment)
}

fun testIntegerObject(obj: MonkeyObject, expected: Int) {
    obj.should.be.instanceof(MonkeyInteger::class.java)
    (obj as MonkeyInteger).value.should.be.equal(expected)
}

fun testStringObject(obj: MonkeyObject, expected: String) {
    obj.should.be.instanceof(MonkeyString::class.java)
    (obj as MonkeyString).value.should.be.equal(expected)
}

fun testBooleanObject(obj: MonkeyObject, expected: Boolean) {
    obj.should.be.instanceof(monkey.`object`.MonkeyBoolean::class.java)
    (obj as monkey.`object`.MonkeyBoolean).value.should.be.equal(expected)
}

fun testNullObject(obj: MonkeyObject) {
    obj.should.`is`.instanceof(MonkeyNull::class.java)
}

fun testError(obj: MonkeyObject, expected: String) {
    obj.should.be.instanceof(monkey.`object`.Error::class.java)
    (obj as monkey.`object`.Error).message.should.be.equal(expected)
}

fun testFunctionObject() {
    val input = "fn(x) { x + 2; };"

    val evaluated = testEval(input)
    evaluated.should.instanceof(Function::class.java)
    val function = evaluated as Function

    function.parameters.size.should.equal(1)
    function.parameters[0].toString().should.equal("x")
    function.body.toString().should.equal("(x + 2)")
}