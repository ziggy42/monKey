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

val first = fun(args: List<MonkeyObject>): MonkeyObject {
    if (args.size != 1)
        return MonkeyError("wrong number of arguments. got=${args.size}, want=1")

    if (args[0].type != ObjectType.ARRAY)
        return MonkeyError("argument to `first` must be ARRAY, got ${args[0].type}")

    val array = args[0] as MonkeyArray
    return if (array.elements.isNotEmpty()) array.elements[0] else MonkeyNull()
}

val last = fun(args: List<MonkeyObject>): MonkeyObject {
    if (args.size != 1)
        return MonkeyError("wrong number of arguments. got=${args.size}, want=1")

    if (args[0].type != ObjectType.ARRAY)
        return MonkeyError("argument to `last` must be ARRAY, got ${args[0].type}")

    val array = args[0] as MonkeyArray
    return if (array.elements.isNotEmpty()) array.elements[array.elements.size - 1] else MonkeyNull()
}

val rest = fun(args: List<MonkeyObject>): MonkeyObject {
    if (args.size != 1)
        return MonkeyError("wrong number of arguments. got=${args.size}, want=1")

    if (args[0].type != ObjectType.ARRAY)
        return MonkeyError("argument to `rest` must be ARRAY, got ${args[0].type}")

    val list = (args[0] as MonkeyArray).elements
    return if (list.isNotEmpty()) MonkeyArray(list.subList(1, list.size)) else MonkeyNull()
}

val push = fun(args: List<MonkeyObject>): MonkeyObject {
    if (args.size != 2)
        return MonkeyError("wrong number of arguments. got=${args.size}, want=2")

    if (args[0].type != ObjectType.ARRAY)
        return MonkeyError("argument to `push` must be ARRAY, got ${args[0].type}")

    val list = (args[0] as MonkeyArray).elements.toMutableList()
    list.add(args[1])
    return if (list.isNotEmpty()) MonkeyArray(list) else MonkeyNull()
}

val puts = fun(args: List<MonkeyObject>): MonkeyNull {
    println(args[0].inspect())
    return MonkeyNull()
}

val BUILTINS = mapOf(
        "len" to MonkeyBuiltin(len),
        "first" to MonkeyBuiltin(first),
        "last" to MonkeyBuiltin(last),
        "rest" to MonkeyBuiltin(rest),
        "push" to MonkeyBuiltin(push),
        "puts" to MonkeyBuiltin(puts))