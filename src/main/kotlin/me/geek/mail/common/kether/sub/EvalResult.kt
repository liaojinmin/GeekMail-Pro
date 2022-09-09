package me.geek.mail.common.kether.sub


/**
 * 作者: 老廖
 * 时间: 2022/8/8
 *
 **/
@JvmInline
value class EvalResult(val any: Any? = null) {

    fun asBoolean(def: Boolean = false): Boolean {
        return when (any) {
            is Boolean -> any
            else -> def //|| KetherAPI.toBoolean(any)
        }
    }

    fun asString(): String {
        return any.toString()
    }

    companion object {

        val TRUE = EvalResult(true)

        val FALSE = EvalResult(false)

    }
}