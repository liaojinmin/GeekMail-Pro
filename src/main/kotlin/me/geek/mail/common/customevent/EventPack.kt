package me.geek.mail.common.customevent

/**
 * 作者: 老廖
 * 时间: 2022/10/3
 *
 **/
data class EventPack(
    val id: String = "",
    val event: String = "",
    val condition: String = "",
    val action: MutableList<String> = mutableListOf()
)