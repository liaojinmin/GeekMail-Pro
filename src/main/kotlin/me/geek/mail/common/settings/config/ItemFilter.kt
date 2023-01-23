package me.geek.mail.common.settings.config

/**
 * 作者: 老廖
 * 时间: 2022/10/24
 *
 **/
data class ItemFilter(
    val use: Boolean = false,
    val type: String = "黑名单",
    val contains_name: List<String> = listOf(),
    val contains_lore: List<String> = listOf()
)