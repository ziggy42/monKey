package monkey.`object`

/**
 * @author andrea
 * @since 7/29/17
 */
class Environment {
    private val store = mutableMapOf<String, Object>()

    fun get(name: String) = store[name]

    fun set(name: String, value: Object): Object {
        store[name] = value
        return value
    }
}