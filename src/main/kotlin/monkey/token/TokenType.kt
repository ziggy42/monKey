package monkey.token

val KEYWORDS = mapOf(
        "let" to TokenType.LET,
        "fn" to TokenType.FUNCTION,
        "true" to TokenType.TRUE,
        "false" to TokenType.FALSE,
        "if" to TokenType.IF,
        "else" to TokenType.ELSE,
        "return" to TokenType.RETURN)

/**
 * The type of a Token
 * @author andrea
 * @since 7/17/17
 */
enum class TokenType {
    ILLEGAL,
    EOF,
    IDENT,
    INT,
    STRING,
    ASSIGN,
    PLUS,
    MINUS,
    BANG,
    ASTERISK,
    SLASH,
    LT,
    GT,
    COMMA,
    SEMICOLON,
    COLON,
    LPAREN,
    RPAREN,
    LBRACE,
    RBRACE,
    LBRACKET,
    RBRACKET,
    FUNCTION,
    LET,
    TRUE,
    FALSE,
    IF,
    ELSE,
    RETURN,
    EQ,
    NOT_EQ
}