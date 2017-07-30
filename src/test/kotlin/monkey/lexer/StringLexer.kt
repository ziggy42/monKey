package monkey.lexer

import com.winterbe.expekt.should
import monkey.token.Token
import monkey.token.TokenType
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

/**
 * @author andrea
 * @since 7/17/17
 */
object LexerTest : Spek({
    describe("lexer") {
        it("extract basic tokens") {
            val simpleTokens = "=+(){},;"
            val lexer = StringLexer(simpleTokens)

            arrayOf(
                    Token(TokenType.ASSIGN, "="),
                    Token(TokenType.PLUS, "+"),
                    Token(TokenType.LPAREN, "("),
                    Token(TokenType.RPAREN, ")"),
                    Token(TokenType.LBRACE, "{"),
                    Token(TokenType.RBRACE, "}"),
                    Token(TokenType.COMMA, ","),
                    Token(TokenType.SEMICOLON, ";"))
                    .forEach { lexer.nextToken().should.equal(it) }
        }

        it("extract tokens from simple math expressions") {
            val mathExpression = """
5+3/2*1!;
5 < 10 > 5;
==
!=
"""
            val lexer = StringLexer(mathExpression)

            arrayOf(
                    Token(TokenType.INT, "5"),
                    Token(TokenType.PLUS, "+"),
                    Token(TokenType.INT, "3"),
                    Token(TokenType.SLASH, "/"),
                    Token(TokenType.INT, "2"),
                    Token(TokenType.ASTERISK, "*"),
                    Token(TokenType.INT, "1"),
                    Token(TokenType.BANG, "!"),
                    Token(TokenType.SEMICOLON, ";"),
                    Token(TokenType.INT, "5"),
                    Token(TokenType.LT, "<"),
                    Token(TokenType.INT, "10"),
                    Token(TokenType.GT, ">"),
                    Token(TokenType.INT, "5"),
                    Token(TokenType.SEMICOLON, ";"),
                    Token(TokenType.EQ, "=="),
                    Token(TokenType.NOT_EQ, "!="))
                    .forEach { lexer.nextToken().should.equal(it) }
        }

        it("extract tokens from simple code snippet") {
            val codeSnippet = """let five = 5;
let ten = 10;

let add = fn(x, y) {
    x + y;
};

let result = add(five, ten);
"""
            val lexer = StringLexer(codeSnippet)

            arrayOf(
                    Token(TokenType.LET, "let"),
                    Token(TokenType.IDENT, "five"),
                    Token(TokenType.ASSIGN, "="),
                    Token(TokenType.INT, "5"),
                    Token(TokenType.SEMICOLON, ";"),
                    Token(TokenType.LET, "let"),
                    Token(TokenType.IDENT, "ten"),
                    Token(TokenType.ASSIGN, "="),
                    Token(TokenType.INT, "10"),
                    Token(TokenType.SEMICOLON, ";"),
                    Token(TokenType.LET, "let"),
                    Token(TokenType.IDENT, "add"),
                    Token(TokenType.ASSIGN, "="),
                    Token(TokenType.FUNCTION, "fn"),
                    Token(TokenType.LPAREN, "("),
                    Token(TokenType.IDENT, "x"),
                    Token(TokenType.COMMA, ","),
                    Token(TokenType.IDENT, "y"),
                    Token(TokenType.RPAREN, ")"),
                    Token(TokenType.LBRACE, "{"),
                    Token(TokenType.IDENT, "x"),
                    Token(TokenType.PLUS, "+"),
                    Token(TokenType.IDENT, "y"),
                    Token(TokenType.SEMICOLON, ";"),
                    Token(TokenType.RBRACE, "}"),
                    Token(TokenType.SEMICOLON, ";"),
                    Token(TokenType.LET, "let"),
                    Token(TokenType.IDENT, "result"),
                    Token(TokenType.ASSIGN, "="),
                    Token(TokenType.IDENT, "add"),
                    Token(TokenType.LPAREN, "("),
                    Token(TokenType.IDENT, "five"),
                    Token(TokenType.COMMA, ","),
                    Token(TokenType.IDENT, "ten"),
                    Token(TokenType.RPAREN, ")"),
                    Token(TokenType.SEMICOLON, ";"))
                    .forEach { lexer.nextToken().should.equal(it) }
        }

        it("extract tokens from code snippet with multi character KEYWORDS") {
            val codeSnippet = """
if (5 < 10) {
    return true;
} else {
    return false;
}
"""
            val lexer = StringLexer(codeSnippet)

            arrayOf(
                    Token(TokenType.IF, "if"),
                    Token(TokenType.LPAREN, "("),
                    Token(TokenType.INT, "5"),
                    Token(TokenType.LT, "<"),
                    Token(TokenType.INT, "10"),
                    Token(TokenType.RPAREN, ")"),
                    Token(TokenType.LBRACE, "{"),
                    Token(TokenType.RETURN, "return"),
                    Token(TokenType.TRUE, "true"),
                    Token(TokenType.SEMICOLON, ";"),
                    Token(TokenType.RBRACE, "}"),
                    Token(TokenType.ELSE, "else"),
                    Token(TokenType.LBRACE, "{"),
                    Token(TokenType.RETURN, "return"),
                    Token(TokenType.FALSE, "false"),
                    Token(TokenType.SEMICOLON, ";"),
                    Token(TokenType.RBRACE, "}"))
                    .forEach { lexer.nextToken().should.equal(it) }
        }

        it("extract tokens from code snippet with strings") {
            val codeSnippet = """
"foobar"
"foo bar"
"""

            val lexer = StringLexer(codeSnippet)
            arrayOf(
                    Token(TokenType.STRING, "foobar"),
                    Token(TokenType.STRING, "foo bar"))
                    .forEach { lexer.nextToken().should.equal(it) }

        }

        it("extract tokens from code snippet with arrays") {
            val lexer = StringLexer("[1, 2];")

            arrayOf(Token(TokenType.LBRACKET, "["),
                    Token(TokenType.INT, "1"),
                    Token(TokenType.COMMA, ","),
                    Token(TokenType.INT, "2"),
                    Token(TokenType.RBRACKET, "]"),
                    Token(TokenType.SEMICOLON, ";"))
                    .forEach { lexer.nextToken().should.equal(it) }
        }

        it("extract tokens from code snippet with arrays") {
            val lexer = StringLexer("""{"foo": "bar"}""")

            arrayOf(Token(TokenType.LBRACE, "{"),
                    Token(TokenType.STRING, "foo"),
                    Token(TokenType.COLON, ":"),
                    Token(TokenType.STRING, "bar"),
                    Token(TokenType.RBRACE, "}"))
                    .forEach { lexer.nextToken().should.equal(it) }
        }
    }
})