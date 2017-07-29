package monkey.`object`

/**
 * @author andrea
 * @since 7/29/17
 */
class Error(val message: String) : Object(ObjectType.ERROR) {

    override fun inspect() = "ERROR: $message"
}