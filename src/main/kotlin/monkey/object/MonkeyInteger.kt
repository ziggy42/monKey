package monkey.`object`

/**
 * @author andrea
 * @since 7/29/17
 */
data class MonkeyInteger(val value: Int) : MonkeyObject(ObjectType.INTEGER), Hashable {

    override fun inspect() = value.toString()
}