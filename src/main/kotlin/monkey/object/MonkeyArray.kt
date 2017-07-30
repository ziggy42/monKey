package monkey.`object`

/**
 * @author andrea
 * @since 7/30/17
 */
data class MonkeyArray(val elements: List<MonkeyObject>) : MonkeyObject(ObjectType.ARRAY) {

    override fun inspect() = "[${elements.map { it.inspect() }.joinToString(", ")}]"
}