package monkey.`object`

/**
 * @author andrea
 * @since 7/29/17
 */
class Integer(val value: Int) : Object(ObjectType.INTEGER) {

    override fun inspect() = value.toString()
}