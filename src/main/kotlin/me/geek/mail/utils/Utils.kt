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
        if (filter.test(each.next())) {
            each.remove()
            amt++
        }
    }
    return 0
}
