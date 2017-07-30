package monkey.`object`


typealias BuiltinFunction = (List<MonkeyObject>) -> MonkeyObject

/**
 * @author andrea
 * @since 7/29/17
 */
data class MonkeyBuiltin(val function: BuiltinFunction) : MonkeyObject(ObjectType.BUILTIN) {

    override fun inspect() = "builtin function"
}