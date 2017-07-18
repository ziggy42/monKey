package monkey.lexer

import monkey.token.Token
import monkey.token.TokenType

private val KEYWORDS = mapOf(
        "let" to TokenType.LET,
        "fn" to TokenType.FUNCTION,
        "true" to TokenType.TRUE,
        "false" to TokenType.FALSE,
        "if" to TokenType.IF,
        "else" to TokenType.ELSE,
        "return" to TokenType.RETURN)

/**
 * A lexer that will take source code as input and output the tokens that represent the source code.
 * @author andrea
 * @since 7/17/17
 */
class Lexer(private val input: String) {

    private var position = 0
    private var readPosition = 1
    private var char = input[position]

    fun nextToken(): Token {
        skipSeparators()

        val token = when (this.char) {
            '=' ->
                if (peekChar() == '=')
                    Token(TokenType.EQ, readTwoCharacterTokenLiteral())
                else
                    Token(TokenType.ASSIGN, this.char.toString())
            ';' -> Token(TokenType.SEMICOLON, this.char.toString())
            '(' -> Token(TokenType.LPAREN, this.char.toString())
            ')' -> Token(TokenType.RPAREN, this.char.toString())
            ',' -> Token(TokenType.COMMA, this.char.toString())
            '+' -> Token(TokenType.PLUS, this.char.toString())
            '-' -> Token(TokenType.MINUS, this.char.toString())
            '!' ->
                if (peekChar() == '=')
                    Token(TokenType.NOT_EQ, readTwoCharacterTokenLiteral())
                else
                    Token(TokenType.BANG, this.char.toString())
            '*' -> Token(TokenType.ASTERISK, this.char.toString())
            '/' -> Token(TokenType.SLASH, this.char.toString())
            '<' -> Token(TokenType.LT, this.char.toString())
            '>' -> Token(TokenType.GT, this.char.toString())
            '{' -> Token(TokenType.LBRACE, this.char.toString())
            '}' -> Token(TokenType.RBRACE, this.char.toString())
            0.toChar() -> Token(TokenType.EOF, this.char.toString())
            else -> {
                return if (isIdentifier(char)) {
                    val identifier = read { isIdentifier(char) }
                    Token(getTokenType(identifier), identifier)
                } else if (char.isDigit()) {
                    Token(TokenType.INT, read { it.isDigit() })
                } else {
                    Token(TokenType.ILLEGAL, this.char.toString())
                }
            }
        }

        readChar()
        return token
    }

    private fun skipSeparators() {
        while (arrayOf(' ', '\t', '\n', '\r').contains(this.char))
            readChar()
    }

    private fun getTokenType(identifier: String) = if (KEYWORDS.containsKey(identifier))
        KEYWORDS[identifier]!!
    else
        TokenType.IDENT

    private fun isIdentifier(char: Char) = char.isLetter() || char == '_'

    private fun readTwoCharacterTokenLiteral(): String {
        val ch = this.char
        readChar()
        return "$ch${this.char}"
    }

    private fun read(condition: (Char) -> Boolean): String {
        val start = this.position
        while (condition(this.char))
            readChar()

        return input.substring(start, this.position)
    }

    private fun peekChar(): Char = if (readPosition >= input.length) 0.toChar() else input[readPosition]

    private fun readChar() {
        this.char = peekChar()
        this.position = this.readPosition
        this.readPosition++
    }
}