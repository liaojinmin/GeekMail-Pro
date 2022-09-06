package me.geek.mail.common.menu.sub

import org.bukkit.inventory.ItemStack

/**
 * 作者: 老廖
 * 时间: 2022/7/5
 */
class Session(
    val session: String,
    val title: String,
    val stringLayout: String,
    val size: Int,
    val bindings: String,
    var micon: List<Micon>,
    val type: String,
    var itemStacks: Array<ItemStack>
)