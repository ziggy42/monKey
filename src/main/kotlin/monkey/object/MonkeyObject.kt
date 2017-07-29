package monkey.`object`

/**
 * @author andrea
 * @since 7/29/17
 */
abstract class MonkeyObject(val type: ObjectType) {

    abstract fun inspect(): kotlin.String
}