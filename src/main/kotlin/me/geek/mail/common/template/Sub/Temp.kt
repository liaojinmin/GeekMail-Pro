package me.geek.mail.common.template.sub



/**
 * 作者: 老廖
 * 时间: 2022/8/8
 */
interface Temp {
    val packID: String
    val condition: String
    val action: String
    val deny: String
    val title: String
    val text: String
    val type: String
    val additional: String?
        get() = "0"
    val itemStacks: String?
        get() = ""
    val command: List<String>?
}