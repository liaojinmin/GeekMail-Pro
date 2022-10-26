package me.geek.mail.common.market

import org.bukkit.inventory.ItemStack
import org.jetbrains.annotations.NotNull
import java.util.*

/**
 * 作者: 老廖
 * 时间: 2022/10/25
 *
 **/
interface MarketPack {
    val packUid: UUID
    val user: UUID
    val time: String
    val expire: String
    val points: Int
    val money: Double
    val item: ItemStack

    fun addToMarket()

    fun parseItemInfo(@NotNull name: String, @NotNull iconLore: List<String>): List<String>
}