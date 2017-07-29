package monkey.`object`

/**
 * @author andrea
 * @since 7/29/17
 */
class Environment(val outer: Environment? = null) {
    private val store = mutableMapOf<String, Object>()

    fun get(name: String): Object? = store[name] ?: outer?.get(name)

    fun set(name: String, value: Object): Object {
        store[name] = value
        return value
    }

    fun newEnclosedEnvironment() = Environment(this)
}