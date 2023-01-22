package me.geek.mail.common.market

import me.geek.mail.api.data.SqlManage
import me.geek.mail.scheduler.redis.RedisMessageType
import me.geek.mail.scheduler.sql.action
import me.geek.mail.scheduler.sql.actions
import me.geek.mail.scheduler.sql.use
import me.geek.mail.utils.deserializeItemStack
import org.bukkit.Bukkit
import taboolib.common.platform.function.submitAsync
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
    fun getMarketItem(packUid: UUID): Item? {
        return MarketCache[packUid]
    }

    /**
     * @param packUid 要删除的商品ID
     * @param publish 双层意思，是否删除数据库商品， 如果是Redis模式，将发布删除消息，通知其它服务器删除缓存，但不会再次访问数据库。
     */
    @JvmStatic
    fun remMarketItem(packUid: UUID, publish: Boolean) {
        if (publish) {
            deleteItem(packUid) // 只在本机发起删除时修改数据库
            SqlManage.RedisScheduler?.let {
                getMarketItem(packUid)?.let { item ->
                    submitAsync {
                        it.setMarketData(item)
                        it.sendMarketPublish(Bukkit.getPort().toString(), RedisMessageType.MARKET_REM, item.packUid.toString())
                    }
                }
            }
        }
        MarketCache.remove(packUid)
    }

    @JvmStatic
    fun addMarketItem(item: Item) {
        MarketCache[item.packUid] = item
    }
    fun loadItem() {
        SqlManage.getConnection().use {
            createStatement().action { statement ->
               val res = statement.executeQuery("SELECT * FROM `market_data` WHERE id;")
                if (!res.isBeforeFirst) return@action
                while (res.next()) {
                    val packUid = UUID.fromString(res.getString("uid"))
                    val user = UUID.fromString(res.getString("user"))
                    val time = res.getString("time")
                    val points = res.getString("points")
                    val money = res.getString("money").toDouble()
                    val item = res.getString("item").deserializeItemStack()
                    MarketCache[packUid] = Item(packUid, user, time, points, money = money, item = item!!)
                }
            }
        }
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