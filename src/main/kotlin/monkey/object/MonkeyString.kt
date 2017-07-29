package monkey.`object`

/**
 * @author andrea
 * @since 7/29/17
 */
class MonkeyString(val value: kotlin.String) : MonkeyObject(ObjectType.STRING) {

    override fun inspect() = value
}