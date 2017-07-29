package monkey.`object`

/**
 * @author andrea
 * @since 7/29/17
 */
class Boolean(val value: kotlin.Boolean) : Object(ObjectType.BOOLEAN) {

    override fun inspect() = value.toString()
}