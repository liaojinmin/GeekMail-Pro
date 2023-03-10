package me.geek.mail.utils

import java.util.function.Predicate

/**
 * 作者: 老廖
 * 时间: 2023/1/24
 *
 **/
fun <T> MutableList<T>.removeE(filter: Predicate<in T>): Int {
    var amt = 0
    val each = iterator()
    while (each.hasNext()) {
        val a = each.next()
        if (a != null) {
            if (filter.test(a)) {
                each.remove()
                amt++
            }
        }
    }
    return amt
}
