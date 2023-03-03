package me.geek.mail.settings.market

/**
 * 作者: 老廖
 * 时间: 2022/10/25
 *
 **/
data class MarketData(
    val use: Boolean = false,
    val player_buy_sendPack: String = "市场发货模板",
    val player_sell_sendPack: String = "市场拍卖模板"
)
