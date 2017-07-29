package monkey.`object`

/**
 * @author andrea
 * @since 7/29/17
 */
class MonkeyBoolean(val value: kotlin.Boolean) : MonkeyObject(ObjectType.BOOLEAN) {

    override fun inspect() = value.toString()
}