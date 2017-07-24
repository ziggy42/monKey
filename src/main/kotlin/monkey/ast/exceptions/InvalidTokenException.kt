package monkey.ast.exceptions

/**
 * @author andrea
 * @since 7/18/17
 */
class InvalidTokenException(token: String) : RuntimeException("Invalid token $token")