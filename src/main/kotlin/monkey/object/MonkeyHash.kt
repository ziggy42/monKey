package monkey.`object`

/**
 * @author andrea
 * @since 7/30/17
 */
class MonkeyHash(val pairs: MutableMap<MonkeyObject, MonkeyObject>) : MonkeyObject(ObjectType.HASH) {

    override fun inspect() =
            "{${pairs.map { "${it.key.inspect()}: ${it.value.inspect()}" }.joinToString(", ")}}"
}