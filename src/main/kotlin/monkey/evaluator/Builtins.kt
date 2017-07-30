package monkey.evaluator

import monkey.`object`.*

/**
 * @author andrea
 * @since 7/30/17
 */
val len = fun(args: List<MonkeyObject>): MonkeyObject {
    if (args.size != 1)
        return MonkeyError("wrong number of arguments. got=${args.size}, want=1")

    val argument = args[0]
    when (argument.type) {
        ObjectType.STRING -> return MonkeyInteger((argument as MonkeyString).value.length)
        ObjectType.ARRAY -> return MonkeyInteger((argument as MonkeyArray).elements.size)
        else -> return MonkeyError("argument to `len` not supported, got ${argument.type}")
    }
}

val puts = fun(args: List<MonkeyObject>): MonkeyNull {
    println(args[0].inspect())
    return MonkeyNull()
}

val BUILTINS = mapOf("len" to MonkeyBuiltin(len), "puts" to MonkeyBuiltin(puts))