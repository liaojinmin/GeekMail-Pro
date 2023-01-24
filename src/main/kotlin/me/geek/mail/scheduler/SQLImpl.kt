package me.geek.mail.scheduler

import me.geek.mail.api.data.PlayerData
import me.geek.mail.api.data.SqlManage
import me.geek.mail.api.mail.MailSub
import me.geek.mail.scheduler.sql.actions
import me.geek.mail.scheduler.sql.use
import org.bukkit.entity.Player
import java.util.*

/**
 * 作者: 老廖
 * 时间: 2023/1/21
 *
 **/
class SQLImpl {
    private val manager by lazy { SqlManage }

    /**   处理离线邮件路由表    **/
    fun insertOff(vararg mail: MailSub) {
        if (manager.isActive()) {
            manager.getConnection().use {
                this.prepareStatement("INSERT INTO off_data(`uid`, `target`, `data`, `time`) VALUES(?,?,?,?);"
                ).actions { p ->
                    mail.forEach {
                        p.setString(1, it.mailID.toString())
                        p.setString(2, it.target.toString())
                        p.setBytes(3, it.toByteArray())
                        p.setLong(4, System.currentTimeMillis())
                        p.addBatch()
                    }
                    p.executeBatch()
                }
            }
        }
    }
    // (key = target, value = mailID)
    fun deleteOff(vararg target: Pair<UUID, UUID>) {
        if (manager.isActive()) {
            manager.getConnection().use {
                this.prepareStatement("DELETE FROM `off_data` WHERE `target`=? AND `uid`=?;"
                ).actions { p ->
                    target.forEach { (key, value) ->
                        p.setString(1, key.toString())
                        p.setString(2, value.toString())
                        p.addBatch()
                    }
                    p.executeBatch()
                }
            }
        }
    }
    fun selectOff(player: UUID, data: MutableList<MailSub>) {
        if (manager.isActive()) {
            manager.getConnection().use {
                this.prepareStatement(
                    "SELECT `data` FROM `off_data` WHERE `target`=?;"
                ).actions { p ->
                    p.setString(1, player.toString())
                    val res = p.executeQuery()
                    while (res.next()) {
                        data.add(res.getBytes("data").toMailSub())
                    }
                }
            }
        } else error("数据库发生异常，请检查sql服务器状态，并报告此问题。。。")
    }


    /**   处理玩家数据表    **/
    private fun insert(data: PlayerData) {
        if (manager.isActive()) {
            //submitAsync {
                manager.getConnection().use {
                    this.prepareStatement(
                        "INSERT INTO player_data(`uuid`,`user`,`data`,`time`) VALUES(?,?,?,?);"
                    ).actions { p ->
                        p.setString(1, data.uuid.toString())
                        p.setString(2, data.user)
                        p.setBytes(3, data.toByteArray())
                        p.setString(4, System.currentTimeMillis().toString())
                        p.execute()
                    }
                }
            //}
        }
    }
    fun update(data: PlayerData) {
        if (manager.isActive()) {
            manager.getConnection().use {
                this.prepareStatement(
                    "UPDATE `player_data` SET `user`=?,`data`=?,`time`=? WHERE `uuid`=?;"
                ).actions { p ->
                    p.setString(1, data.user)
                    p.setBytes(2, data.toByteArray())
                    p.setString(3, System.currentTimeMillis().toString())
                    p.setString(4, data.uuid.toString())
                    p.executeUpdate()
                }
            }
        }
    }

    fun select(player: Player, upEmpty: Boolean = true): PlayerData {
        var data: PlayerData? = null
        if (manager.isActive()) {
            manager.getConnection().use {
                this.prepareStatement(
                    "SELECT `data` FROM `player_data` WHERE uuid=?;"
                ).actions { p ->
                    p.setString(1, player.uniqueId.toString())
                    val res = p.executeQuery()
                    data = if (res.next()) {
                        val var10 = res.getBytes("data").toPlayerData()
                        var10
                    } else {
                        getDefaultData(player).also {
                            if (upEmpty) insert(it)
                        }
                    }
                }
            }
            return data!!
        } else error("数据库发生异常，请检查sql服务器状态，并报告此问题。。。")
    }



    private fun getDefaultData(player: Player): PlayerData {
        return MailPlayerData(player)
    }
}