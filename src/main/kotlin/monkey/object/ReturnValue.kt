package monkey.`object`

/**
 * @author andrea
 * @since 7/29/17
 */
class ReturnValue(val value: MonkeyObject) : MonkeyObject(ObjectType.RETURN_VALUE) {

    override fun inspect() = value.inspect()
}