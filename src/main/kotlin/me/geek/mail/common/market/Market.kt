package me.geek.mail.common.market

import me.geek.mail.common.data.SqlManage
import me.geek.mail.scheduler.sql.actions
import me.geek.mail.scheduler.sql.use
import java.util.*
import java.util.concurrent.ConcurrentHashMap


/**
 * 作者: 老廖
 * 时间: 2022/10/24
 *
 *
 **/
object Market {
    // 上架的商品ID 、 商品
    private val MarketCache: MutableMap<UUID, Item> = ConcurrentHashMap()

    @JvmStatic
    fun getMarketCache(): MutableMap<UUID, Item> {
        return MarketCache
    }
    @JvmStatic
    fun getMarketListCache(): List<Item> {
        return MarketCache.map { it.value }
    }

    @JvmStatic
    fun getMarketItem(user: UUID): Item? {
        return MarketCache[user]
    }

    @JvmStatic
    fun remMarketItem(user: UUID) {
        MarketCache.remove(user)
    }

    @JvmStatic
    fun addMarketItem(item: Item) {
        MarketCache[item.packUid] = item
        insertItem(item)
    }
    @Synchronized
    fun insertItem(item: Item) {
        SqlManage.getConnection().use {
            this.prepareStatement("INSERT INTO market_data(`uid`,`user`,`time`,`points`,`money`,`item`) VALUES(?,?,?,?,?,?)").actions { p ->
                p.setString(1, item.packUid.toString())
                p.setString(2, item.user.toString())
                p.setString(3, item.time)
                p.setString(4, item.points.toString())
                p.setString(5, item.money.toString())
                p.setString(6, item.itemString)
                p.execute()
            }
        }
    }

    @Synchronized
    fun deleteItem(PacKUid: UUID): Boolean {
        var a = 0
        SqlManage.getConnection().use {
            this.prepareStatement("DELETE FROM `market_data` WHERE `uid`=?;").actions { s ->
                s.setString(1, PacKUid.toString())
                a = s.executeUpdate()
            }
        }
        return a != 0
    }


}