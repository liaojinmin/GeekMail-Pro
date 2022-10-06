package me.geek.mail.common.customevent.sub

/**
 * 作者: 老廖
 * 时间: 2022/10/3
 *
 **/
class EventPack(
    val id: String = "",
    val condition: String = "",
    val action: MutableList<String> = mutableListOf()
)