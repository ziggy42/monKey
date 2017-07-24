package monkey.ast.exceptions

import monkey.token.TokenType

/**
 * @author andrea
 * @since 7/18/17
 */
class UnexpectedTokenException(expectedTokenType: TokenType, actualTokenType: TokenType)
    : RuntimeException("Invalid token: expected $expectedTokenType but got $actualTokenType instead")