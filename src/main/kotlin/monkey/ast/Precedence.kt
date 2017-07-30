package monkey.ast

/**
 * @author andrea
 * @since 7/18/17
 */
enum class Precedence {
    LOWEST,
    EQUALS,
    LESSGREATER,
    SUM,
    PRODUCT,
    PREFIX,
    CALL,
    INDEX
}