package me.geek.mail.api.data


import me.geek.mail.GeekMail
import me.geek.mail.api.event.PlayerDataLoadEvent
import me.geek.mail.api.mail.MailSub
import me.geek.mail.common.settings.SetTings
import me.geek.mail.scheduler.RedisImpl
import me.geek.mail.scheduler.SQLImpl
import me.geek.mail.scheduler.migrator.PData
import me.geek.mail.scheduler.sql.*
import me.geek.mail.utils.removeE
import org.bukkit.entity.Player
import taboolib.platform.util.sendLang
import java.sql.Connection
import java.util.*
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
    private val PlayerCache: MutableMap<UUID, PlayerData> = mutableMapOf()
    private val SqlImpl: SQLImpl = SQLImpl()

    val bindCode = mutableMapOf<UUID, String>()

    fun saveAllData() {
        SqlImpl.updateGlobal(PlayerCache.map { it.value })
    }



    /**
     * 离线邮件调度 - 存
     */
    fun addOffMail(vararg mail: MailSub) {
        SqlImpl.insertOff(*mail)
    }

    /**
     * 离线邮件调度 - 取
     */
    fun PlayerData.getOffMail() {
        val list = mutableListOf<MailSub>()
        val map = mutableListOf<Pair<UUID, UUID>>().apply {
            list.forEach {
                add(it.target to it.mailID)
            }
        }
        SqlImpl.selectOff(this.uuid, list)
        SqlImpl.deleteOff(*map.toTypedArray())
        this.mailData.addAll(list)
    }

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

            if (SetTings.UseExpiry) it.mailData.removeE { mail -> mail.senderTime <= (System.currentTimeMillis() - SetTings.ExpiryTime)
            }.also { amt ->
                if (amt != 0) this.sendLang("玩家-邮件到期-删除", amt)
            }

            GeekMail.debug("getData == OK")
            return it
        } ?: select(this).also { GeekMail.debug("getData == null AC-2") }
        return PlayerCache[this.uniqueId] ?: error("数据异常")
    }

    /**
     * 保存玩家数据
     */
    fun Player.saveData(deleteCache: Boolean = false) {
        PlayerCache[this.uniqueId]?.let {
            if (SetTings.UseExpiry) {
                it.mailData.removeE { mail -> mail.senderTime <= (System.currentTimeMillis() - SetTings.ExpiryTime) }
            }
            RedisScheduler?.setPlayerData(it)
            SqlImpl.update(it)
        }
        if (deleteCache) PlayerCache.remove(this.uniqueId)
    }
    /**
     * 迁移器数据储存入口
     */
    fun migratorSave(data: MutableMap<UUID, PData>) {
        if (isActive()) {
            getConnection().use {
                this.prepareStatement(
                    "UPDATE `player_data` SET `user`=?,`data`=?,`time`=? WHERE `uuid`=?;"
                ).actions { p ->
                    data.forEach { (_, value) ->
                        p.setString(1, value.user)
                        p.setBytes(2, value.toByteArray())
                        p.setString(3, System.currentTimeMillis().toString())
                        p.setString(4, value.uuid.toString())
                        p.addBatch()
                    }
                    p.executeBatch()
                }
            }
        }
    }

    /**
     * 统一查库入口
     */
    private fun select(player: Player) {
        PlayerCache[player.uniqueId] = SqlImpl.select(player).also { PlayerDataLoadEvent(it).call() }
    }


    val RedisScheduler by lazy {
        if (SetTings.redisData.use) {
            RedisImpl(SetTings.redisData)
        } else null
    }

    private val dataSub by lazy {
        if (SetTings.StorageDate.use_type.equals("mysql", ignoreCase = true)) {
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
                            statement.addBatch(SqlTab.OffMail.tab)
                        } else {
                            statement.addBatch("PRAGMA foreign_keys = ON;")
                            statement.addBatch("PRAGMA encoding = 'UTF-8';")
                            statement.addBatch(SqlTab.SQLITE_1.tab)
                            statement.addBatch(SqlTab.SQLITE_2.tab)
                            statement.addBatch(SqlTab.OffSmail.tab)
                        }
                        statement.executeBatch()
                    }
                }
            }
        }
        RedisScheduler // 数据调度初始化
    }

    enum class SqlTab(val tab: String) {

        SQLITE_1(
            "CREATE TABLE IF NOT EXISTS `player_data` (" +
                    " `uuid` CHAR(36) NOT NULL UNIQUE PRIMARY KEY, " +
                    " `user` varchar(16) NOT NULL UNIQUE," +
                    " `data` longblob NOT NULL," +
                    " `time` BIGINT(20) NOT NULL" +
                    ");"
        ),
        SQLITE_2(
            "CREATE TABLE IF NOT EXISTS `market_data` (" +
                    " `id` integer PRIMARY KEY, " +
                    " `uid` CHAR(36) NOT NULL UNIQUE," +
                    " `user` CHAR(36) NOT NULL," +
                    " `time` BIGINT(20) NOT NULL," +
                    " `points` BIGINT(20) NOT NULL," +
                    " `money` BIGINT(20) NOT NULL," +
                    " `item` longtext NOT NULL" +
                    ");"
        ),
        OffSmail(
            "CREATE TABLE IF NOT EXISTS `off_data` (" +
                    " `uid` CHAR(36) NOT NULL UNIQUE PRIMARY KEY," +
                    " `target` CHAR(36) NOT NULL," +
                    " `data` longblob NOT NULL," +
                    " `time` BIGINT(20) NOT NULL" +
                    ");"
        ),

        MYSQL_1(
            "CREATE TABLE IF NOT EXISTS `player_data` (" +
                    " `uuid` CHAR(36) NOT NULL UNIQUE," +
                    " `user` varchar(16) NOT NULL UNIQUE," +
                    " `data` longblob NOT NULL," +
                    " `time` BIGINT(20) NOT NULL," +
                    "PRIMARY KEY (`uuid`, `user`)" +
                    ");"
        ),

        MYSQL_2(
            "CREATE TABLE IF NOT EXISTS `market_data` (" +
                    " `id` integer NOT NULL AUTO_INCREMENT, " +
                    " `uid` CHAR(36) NOT NULL UNIQUE," +
                    " `user` CHAR(36) NOT NULL," +
                    " `time` BIGINT(20) NOT NULL," +
                    " `points` BIGINT(20) NOT NULL," +
                    " `money` BIGINT(20) NOT NULL," +
                    " `item` longtext NOT NULL," +
                    "PRIMARY KEY (`id`)" +
                    ");"
        ),
        OffMail(
            "CREATE TABLE IF NOT EXISTS `off_data` (" +
                    " `uid` CHAR(36) NOT NULL UNIQUE," +
                    " `target` CHAR(36) NOT NULL," +
                    " `data` longblob NOT NULL," +
                    " `time` BIGINT(20) NOT NULL," +
                    "PRIMARY KEY (`uid`, `target`)" +
                    ");"
        ),
    }
}