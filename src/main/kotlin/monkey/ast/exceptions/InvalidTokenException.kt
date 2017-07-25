package monkey.ast.exceptions

import monkey.token.Token

/**
 * @author andrea
 * @since 7/18/17
 */
class InvalidTokenException(token: Token) : RuntimeException("Invalid token $token")