package monkey.`object`

/**
 * @author andrea
 * @since 7/29/17
 */
abstract class Object(val type: ObjectType) {

    abstract fun inspect(): String
}