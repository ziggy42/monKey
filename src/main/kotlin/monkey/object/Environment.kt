package monkey.`object`

/**
 * @author andrea
 * @since 7/29/17
 */
class Environment(val outer: Environment? = null) {
    private val store = mutableMapOf<String, MonkeyObject>()

    fun get(name: String): MonkeyObject? = store[name] ?: outer?.get(name)

    fun set(name: String, value: MonkeyObject): MonkeyObject {
        store[name] = value
        return value
    }

    fun newEnclosedEnvironment() = Environment(this)
}