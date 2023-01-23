package me.geek.mail.api.data


import me.geek.mail.GeekMail
import me.geek.mail.api.event.PlayerDataLoadEvent
import me.geek.mail.common.settings.SetTings
import me.geek.mail.scheduler.RedisImpl
import me.geek.mail.scheduler.SQLImpl
import me.geek.mail.scheduler.sql.Mysql
import me.geek.mail.scheduler.sql.Sqlite
import me.geek.mail.scheduler.sql.action
import me.geek.mail.scheduler.sql.use
import org.bukkit.entity.Player
import taboolib.common.platform.function.submitAsync
import java.sql.Connection
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.set

/**
 * 作者: 老廖
 * 时间: 2022/9/15
 *
 **/
object SqlManage {
    /**
     * 玩家在线缓存
     */
    private val PlayerCache: MutableMap<UUID, PlayerData> = ConcurrentHashMap()
    private val SqlImpl: SQLImpl = SQLImpl()

    fun getPlayerDataList() = PlayerCache.map { it.value }

    /**
     * 获取玩家数据
     */
    fun Player.getData(): PlayerData {
        GeekMail.debug("Player.getData()")
        // 先本地获取 再Redis调度获取，均为null，查库
        var data = PlayerCache[this.uniqueId]
        if (data == null) {
            GeekMail.debug("getData == null AC-1")
            data = RedisScheduler?.getPlayerData(this.uniqueId.toString())
        }
        data?.let {
            GeekMail.debug("getData == OK")
            return it
        } ?: select(this).also { GeekMail.debug("getData == null AC-2") }
        return PlayerCache[this.uniqueId] ?: error("数据异常")
    }
    /**
     * 保存玩家数据
     */
    fun Player.saveData(isAsync: Boolean = false) {
        if (isAsync) {
            submitAsync {
                PlayerCache[this@saveData.uniqueId]?.let {
                    RedisScheduler?.setPlayerData(it)
                    SqlImpl.update(it)
                }
                PlayerCache.remove(this@saveData.uniqueId)
            }
        } else {
            PlayerCache[this.uniqueId]?.let { SqlImpl.update(it) }
            PlayerCache.remove(this.uniqueId)
        }
    }

    /**
     * 统一查库入口
     */
    private fun select(player: Player, isAsync: Boolean = false) {
        if (isAsync) {
            submitAsync {
                PlayerCache[player.uniqueId] = SqlImpl.select(player).also { PlayerDataLoadEvent(it).call() }
            }
        }
        else PlayerCache[player.uniqueId] = SqlImpl.select(player).also { PlayerDataLoadEvent(it).call() }
    }


    val RedisScheduler by lazy {
        if (SetTings.redisData.use) {
            RedisImpl(SetTings.redisData)
        } else null
    }

    private val dataSub by lazy {
        if (SetTings.StorageDate.use_type.equals("mysql", ignoreCase = true)){
            return@lazy Mysql(SetTings.StorageDate)
        } else return@lazy Sqlite(SetTings.StorageDate)
    }

    fun isActive(): Boolean = dataSub.isActive


    fun getConnection(): Connection {
        return dataSub.getConnection()
    }

    fun closeData() {
        dataSub.onClose()
    }

    fun start() {
        if (dataSub.isActive) return //避免重复启动
        dataSub.onStart()
        if (dataSub.isActive) {
            dataSub.createTab {
                getConnection().use {
                    createStatement().action { statement ->
                        if (dataSub is Mysql) {
                            statement.addBatch(SqlTab.MYSQL_1.tab)
                            statement.addBatch(SqlTab.MYSQL_2.tab)
                        } else {
                            statement.addBatch("PRAGMA foreign_keys = ON;")
                            statement.addBatch("PRAGMA encoding = 'UTF-8';")
                            statement.addBatch(SqlTab.SQLITE_1.tab)
                            statement.addBatch(SqlTab.SQLITE_2.tab)
                        }
                        statement.executeBatch()
                    }
                }
            }
        }
        RedisScheduler // 数据调度初始化
    }

    enum class SqlTab(val tab: String) {

        SQLITE_1("CREATE TABLE IF NOT EXISTS `player_data` (" +
                " `uuid` CHAR(36) NOT NULL UNIQUE PRIMARY KEY, " +
                " `user` varchar(16) NOT NULL UNIQUE," +
                " `data` longblob NOT NULL," +
                " `time` BIGINT(20) NOT NULL" +
                ");"),
        SQLITE_2("CREATE TABLE IF NOT EXISTS `market_data` (" +
                " `id` integer PRIMARY KEY, " +
                " `uid` CHAR(36) NOT NULL UNIQUE," +
                " `user` CHAR(36) NOT NULL," +
                " `time` BIGINT(20) NOT NULL," +
                " `points` BIGINT(20) NOT NULL," +
                " `money` BIGINT(20) NOT NULL," +
                " `item` longtext NOT NULL" +
                ");"),

        MYSQL_1("CREATE TABLE IF NOT EXISTS `player_data` (" +
                " `uuid` CHAR(36) NOT NULL UNIQUE," +
                " `user` varchar(16) NOT NULL UNIQUE," +
                " `data` longblob NOT NULL," +
                " `time` BIGINT(20) NOT NULL," +
                "PRIMARY KEY (`uuid`, `user`)" +
                ");"),

        MYSQL_2("CREATE TABLE IF NOT EXISTS `market_data` (" +
                " `id` integer NOT NULL AUTO_INCREMENT, " +
                " `uid` CHAR(36) NOT NULL UNIQUE," +
                " `user` CHAR(36) NOT NULL," +
                " `time` BIGINT(20) NOT NULL," +
                " `points` BIGINT(20) NOT NULL," +
                " `money` BIGINT(20) NOT NULL," +
                " `item` longtext NOT NULL," +
                "PRIMARY KEY (`id`)" +
                ");"),
    }
}