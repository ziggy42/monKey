package monkey.`object`

/**
 * @author andrea
 * @since 7/29/17
 */
class MonkeyInteger(val value: Int) : MonkeyObject(ObjectType.INTEGER) {

    override fun inspect() = value.toString()
}