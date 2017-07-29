package monkey.`object`

/**
 * @author andrea
 * @since 7/29/17
 */
class ReturnValue(val value: Object) : Object(ObjectType.RETURN_VALUE) {

    override fun inspect() = value.inspect()
}