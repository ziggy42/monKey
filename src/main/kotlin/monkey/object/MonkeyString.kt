package monkey.`object`

/**
 * @author andrea
 * @since 7/29/17
 */
data class MonkeyString(val value: kotlin.String) : MonkeyObject(ObjectType.STRING), Hashable {

    override fun inspect() = value
}