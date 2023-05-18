package me.geek.mail.settings.market

import org.bukkit.entity.Player

/**
 * 作者: 老廖
 * 时间: 2022/10/25
 *
 **/
data class MarketData(
    val use: Boolean = false,
    val player_buy_sendPack: String = "市场发货模板",
    val player_sell_sendPack: String = "市场拍卖模板",
    val player_quit_sendPack: String = "市场下架模板",
    val defaultSize: Int = 3,
    val permGroup: MutableList<PermGroupData> = mutableListOf()
) {
    fun getPlayerPutSize(player: Player): Int {
        for (a in permGroup) {
            if (player.hasPermission(a.perm)) {
                return a.size
            }
        }
        return defaultSize
    }
}
