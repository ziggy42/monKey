package monkey.`object`

/**
 * @author andrea
 * @since 7/29/17
 */
data class MonkeyBoolean(val value: kotlin.Boolean) : MonkeyObject(ObjectType.BOOLEAN), Hashable {

    override fun inspect() = value.toString()
}