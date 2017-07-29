package monkey.parser

import com.winterbe.expekt.should
import monkey.ast.Parser
import monkey.ast.Program
import monkey.ast.exceptions.UnexpectedTokenException
import monkey.ast.expressions.*
import monkey.ast.statements.ExpressionStatement
import monkey.ast.statements.LetStatement
import monkey.ast.statements.ReturnStatement
import monkey.ast.statements.Statement
import monkey.lexer.StringLexer
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

/**
 * @author andrea
 * @since 7/18/17
 */
object ParserTest : Spek({
    describe("parser") {
        it("Parse let statements") {
            val code = """
let x = 5;
let y = true;
let foobar = y;
"""
            val program = Parser(StringLexer(code)).parseProgram()

            testProgram(program, 3)

            val identifiers = arrayOf("x", "y", "foobar")

            program.statements.forEachIndexed { index, statement -> testLetStatement(statement, identifiers[index]) }
        }

        it("Parse return statements") {
            val program = Parser(StringLexer("return 5; return true; return 1234;")).parseProgram()

            testProgram(program, 3)

            program.statements.forEach { testReturnStatement(it) }
        }

        it("Parse identifier expressions") {
            val program = Parser(StringLexer("foo;")).parseProgram()

            testProgram(program, 1)

            program.statements.forEach {
                it.should.instanceof(ExpressionStatement::class.java)
                val expressionStatement = it as ExpressionStatement
                testIdentifierExpression(expressionStatement.expression, "foo")
            }
        }

        it("Parse integer literal expressions") {
            val program = Parser(StringLexer("5;")).parseProgram()

            testProgram(program, 1)

            program.statements.forEach {
                it.should.instanceof(ExpressionStatement::class.java)
                val expressionStatement = it as ExpressionStatement
                testIntegerLiteralExpression(expressionStatement.expression, 5)
            }
        }

        it("Parse string literal expressions") {
            val program = Parser(StringLexer(""""foo";""")).parseProgram()

            testProgram(program, 1)

            program.statements.forEach {
                it.should.instanceof(ExpressionStatement::class.java)
                val expressionStatement = it as ExpressionStatement
                testStringLiteralExpression(expressionStatement.expression, "foo")
            }
        }

        it("Parse prefix expressions") {
            val operators = arrayOf("-", "!")

            val program = Parser(StringLexer("-5;!5")).parseProgram()

            testProgram(program, 2)

            program.statements.forEachIndexed { index, statement ->
                statement.should.instanceof(ExpressionStatement::class.java)
                val expressionStatement = statement as ExpressionStatement

                testPrefixExpression(
                        expressionStatement.expression,
                        operators[index],
                        IntegerLiteralExpression::class.java,
                        5)
            }
        }

        it("Parse infix expressions") {
            val operators = arrayOf("+", "==")

            val program = Parser(StringLexer("5 + 5; 5 == 5;")).parseProgram()

            testProgram(program, 2)

            program.statements.forEachIndexed { index, statement ->
                statement.should.instanceof(ExpressionStatement::class.java)
                val expressionStatement = statement as ExpressionStatement

                testInfixExpression(
                        expressionStatement.expression,
                        IntegerLiteralExpression::class.java,
                        5,
                        operators[index],
                        IntegerLiteralExpression::class.java,
                        5)
            }
        }

        it("Test precedence") {
            listOf(Pair("5 + 5", "(5 + 5)"),
                    Pair("-a * b", "((-a) * b)"),
                    Pair("!-a", "(!(-a))"),
                    Pair("a + b + c", "((a + b) + c)"),
                    Pair("a + b - c", "((a + b) - c)"),
                    Pair("a * b * c", "((a * b) * c)"),
                    Pair("a * b / c", "((a * b) / c)"),
                    Pair("a + b * c + d / e - f", "(((a + (b * c)) + (d / e)) - f)"),
                    Pair("5 > 4 == 3 < 4", "((5 > 4) == (3 < 4))"),
                    Pair("5 < 4 != 3 > 4", "((5 < 4) != (3 > 4))"),
                    Pair("3 + 4 * 5 == 3 * 1 + 4 * 5", "((3 + (4 * 5)) == ((3 * 1) + (4 * 5)))"),
                    Pair("1 + (2 + 3) + 4", "((1 + (2 + 3)) + 4)"),
                    Pair("(5 + 5) * 2", "((5 + 5) * 2)"),
                    Pair("2 / (5 + 5)", "(2 / (5 + 5))"),
                    Pair("-(5 + 5)", "(-(5 + 5))"),
                    Pair("!(true == true)", "(!(true == true))"))
                    .map { Pair(Parser(StringLexer(it.first)).parseProgram(), it.second) }
                    .forEach { it.first.toString().should.be.equal(it.second) }
        }

        it("Test if expressions") {
            val program = Parser(StringLexer("if (x < y) { x }")).parseProgram()

            testProgram(program, 1)

            val statement = program.statements[0]
            statement.should.instanceof(ExpressionStatement::class.java)

            val expression = (statement as ExpressionStatement).expression
            expression.should.instanceof(IfExpression::class.java)

            val ifExpression = expression as IfExpression
            ifExpression.alternative.should.be.`null`

            testInfixExpression(
                    ifExpression.condition,
                    IdentifierExpression::class.java,
                    "x",
                    "<",
                    IdentifierExpression::class.java,
                    "y")

            val expressionStatement = ifExpression.consequence.statements[0] as ExpressionStatement
            testIdentifierExpression(expressionStatement.expression, "x")
        }

        it("Test if expression with else") {
            val program = Parser(StringLexer("if (x < y) { x } else { y }")).parseProgram()

            testProgram(program, 1)

            val statement = program.statements[0]
            statement.should.instanceof(ExpressionStatement::class.java)

            val expression = (statement as ExpressionStatement).expression
            expression.should.instanceof(IfExpression::class.java)

            val ifExpression = expression as IfExpression

            testInfixExpression(
                    ifExpression.condition,
                    IdentifierExpression::class.java,
                    "x",
                    "<",
                    IdentifierExpression::class.java,
                    "y")

            val consequenceStatement = ifExpression.consequence.statements[0] as ExpressionStatement
            testIdentifierExpression(consequenceStatement.expression, "x")

            ifExpression.alternative.should.not.be.`null`
            val alternativeExpression = ifExpression.alternative!!.statements[0] as ExpressionStatement
            testIdentifierExpression(alternativeExpression.expression, "y")
        }

        it("Test function literal expressions") {
            val program = Parser(StringLexer("fn(x, y) { x + y; }")).parseProgram()

            testProgram(program, 1)

            val statement = program.statements[0]
            statement.should.instanceof(ExpressionStatement::class.java)

            val expression = (statement as ExpressionStatement).expression
            expression.should.instanceof(FunctionLiteralExpression::class.java)

            val functionLiteralExpression = expression as FunctionLiteralExpression
            functionLiteralExpression.parameters.size.should.be.equal(2)

            testIdentifierExpression(functionLiteralExpression.parameters[0], "x")
            testIdentifierExpression(functionLiteralExpression.parameters[1], "y")

            functionLiteralExpression.body.statements.size.should.be.equal(1)
            functionLiteralExpression.body.statements[0].should.be.instanceof(ExpressionStatement::class.java)

            val expressionStatement = functionLiteralExpression.body.statements[0] as ExpressionStatement
            testInfixExpression(
                    expressionStatement.expression,
                    IdentifierExpression::class.java,
                    "x", "+",
                    IdentifierExpression::class.java,
                    "y")
        }

        it("Test call expression") {
            val program = Parser(StringLexer("add(1, 2 * 3, 4 + 5);")).parseProgram()

            testProgram(program, 1)

            val statement = program.statements[0]
            statement.should.instanceof(ExpressionStatement::class.java)

            val expression = (statement as ExpressionStatement).expression
            expression.should.instanceof(CallExpression::class.java)

            val callExpression = expression as CallExpression
            testIdentifierExpression(callExpression.function, "add")

            callExpression.arguments.size.should.be.equal(3)

            testIntegerLiteralExpression(callExpression.arguments[0], 1)
            testInfixExpression(
                    callExpression.arguments[1],
                    IntegerLiteralExpression::class.java,
                    2,
                    "*",
                    IntegerLiteralExpression::class.java,
                    3)
            testInfixExpression(
                    callExpression.arguments[2],
                    IntegerLiteralExpression::class.java,
                    4,
                    "+",
                    IntegerLiteralExpression::class.java,
                    5)
        }

        it("Throw exceptions if code has unexpected tokens") {
            val parser = Parser(StringLexer("let = 4;"))
            try {
                parser.parseProgram()
                throw RuntimeException("You shall not pass!")
            } catch (exception: RuntimeException) {
                exception.should.instanceof(UnexpectedTokenException::class.java)
            }
        }
    }
})

private fun testProgram(program: Program, nStatement: Int) {
    program.should.not.be.`null`
    program.statements.size.should.be.equal(nStatement)
}

private fun testLetStatement(statement: Statement, identifier: String) {
    statement.should.instanceof(LetStatement::class.java)
    val letStatement = statement as LetStatement
    letStatement.getTokenLiteral().should.be.equal("let")
    letStatement.name.getTokenLiteral().should.equal(identifier)
    letStatement.name.value.should.equal(identifier)
}

private fun testReturnStatement(statement: Statement) {
    statement.should.instanceof(ReturnStatement::class.java)
    val returnStatement = statement as ReturnStatement
    returnStatement.getTokenLiteral().should.be.equal("return")
}

private fun testIdentifierExpression(expression: Expression, value: String) {
    expression.should.instanceof(IdentifierExpression::class.java)
    (expression as IdentifierExpression).value.should.be.equal(value)
}

private fun testIntegerLiteralExpression(expression: Expression, value: Int) {
    expression.should.instanceof(IntegerLiteralExpression::class.java)
    (expression as IntegerLiteralExpression).value.should.equal(value)
}

private fun testStringLiteralExpression(expression: Expression, value: String) {
    expression.should.instanceof(StringLiteralExpression::class.java)
    (expression as StringLiteralExpression).value.should.equal(value)
}

private fun testPrefixExpression(
        expression: Expression,
        operator: String,
        rightClass: Class<out Expression>,
        rightValue: Any) {
    expression.should.instanceof(PrefixExpression::class.java)
    val prefixExpression = expression as PrefixExpression
    prefixExpression.operator.should.equal(operator)

    testGenericExpression(prefixExpression.right, rightClass, rightValue)
}

private fun testInfixExpression(
        expression: Expression,
        leftClass: Class<out Expression>,
        leftValue: Any,
        operator: String,
        rightClass: Class<out Expression>,
        rightValue: Any) {
    expression.should.instanceof(InfixExpression::class.java)
    val infixExpression = expression as InfixExpression
    infixExpression.operator.should.equal(operator)

    testGenericExpression(infixExpression.left, leftClass, leftValue)
    testGenericExpression(infixExpression.right, rightClass, rightValue)
}

private fun testGenericExpression(expression: Expression, aClass: Class<out Expression>, value: Any) {
    when (aClass) {
        IdentifierExpression::class.java -> testIdentifierExpression(expression, value as String)
        IntegerLiteralExpression::class.java -> testIntegerLiteralExpression(expression, value as Int)
        else -> throw RuntimeException("Unknown right expression ${aClass.name}")
    }
}