package monkey.`object`

/**
 * @author andrea
 * @since 7/29/17
 */
data class MonkeyError(val message: String) : MonkeyObject(ObjectType.ERROR) {

    override fun inspect() = "ERROR: $message"
}