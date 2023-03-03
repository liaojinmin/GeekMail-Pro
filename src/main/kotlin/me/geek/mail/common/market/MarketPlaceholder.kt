package me.geek.mail.common.market

import com.google.gson.annotations.Expose
import me.geek.mail.settings.SetTings
import org.bukkit.Bukkit
import org.jetbrains.annotations.NotNull
import taboolib.expansion.geek.Expiry
import taboolib.module.nms.i18n.I18n
import java.text.SimpleDateFormat

/**
 * 作者: 老廖
 * 时间: 2022/10/25
 *
 **/
abstract class MarketPlaceholder: MarketPack {


    @Expose
    val format = SimpleDateFormat("yyyy年 MM月 dd日 HH:mm:ss")

    @Expose
    val itemName = Regex("(\\{|\\[)(item_name|商品名称)(}|])")

    @Expose
    val seller = Regex("(\\{|\\[)(seller|卖家)(}|])")

    @Expose
    val moneyPrice = Regex("(\\{|\\[)(moneyPrice|金币价格)(}|])")
    @Expose
    val pointsPrice = Regex("(\\{|\\[)(pointsPrice|点券价格)(}|])")

    @Expose
    val times = Regex("(\\{|\\[)(time|上架时间)(}|])")

    @Expose
    val expires = Regex("(\\{|\\[)(expire|到期时间)(}|])")

    override fun parseItemInfo(@NotNull name: String, @NotNull iconLore: List<String>): MutableList<String> {
        val list = mutableListOf<String>()
        iconLore.forEach {
            when {
                it.contains(itemName) -> {
                    val meta = this.item.itemMeta
                    if (meta != null) {
                        val display = if (meta.hasDisplayName()) { meta.displayName } else I18n.instance.getName(this.item)
                        list.add(it.replace(itemName, display))
                    }
                }
                it.contains(seller) -> list.add(it.replace(seller, Bukkit.getOfflinePlayer(user).name ?: "未知卖家"))
                it.contains(moneyPrice) -> list.add(it.replace(moneyPrice, this.money.toString()))
                it.contains(pointsPrice) -> list.add(it.replace(pointsPrice, this.points.toString()))
                it.contains(times) -> list.add(it.replace(times, format.format(this.time.toLong())))
                it.contains(expires) -> list.add(it.replace(expires, Expiry.getExpiryDate(this.expire.toLong() + SetTings.ExpiryTime, false)))
                else -> list.add(it)
            }
        }
        return list
    }
}