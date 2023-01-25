package me.geek.mail.scheduler.migrator

import me.geek.mail.GeekMail
import me.geek.mail.api.data.SqlManage
import me.geek.mail.api.mail.MailManage
import me.geek.mail.api.mail.MailState
import me.geek.mail.api.mail.MailSub
import me.geek.mail.utils.deserializeItemStacks
import org.bukkit.configuration.file.YamlConfiguration
import taboolib.library.reflex.Reflex.Companion.setProperty
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.util.*

/**
 * 作者: 老廖
 * 时间: 2023/1/24
 *
 **/
class Migrator(name: String) {
    private val config = YamlConfiguration.loadConfiguration(File(GeekMail.instance.dataFolder, "$name.yml"))
    private val connection: Connection by lazy {
        if (config.getString("migrator.use_type").equals("sqlite", ignoreCase = true)) {
            DriverManager.getConnection("jdbc:sqlite:${GeekMail.instance.dataFolder}${File.separator}data.db")
        } else
        DriverManager.getConnection("jdbc:mysql://" +
                "${config.getString("migrator.mysql.host")}:" +
                "${config.getString("migrator.mysql.port")}/" +
                "${config.getString("migrator.mysql.database")}" +
                "${config.getString("migrator.mysql.params")}",
            config.getString("migrator.mysql.username"),
            config.getString("migrator.mysql.password")
        )
    }
    private val dataCache = mutableMapOf<UUID, PData>()
    private val mailCache = mutableListOf<MailSub>()

    fun start() {
        selectPlayer().also { GeekMail.say("正在收集所有玩家数据...") }
        selectMail().also { GeekMail.say("正在收集所有邮件数据...") }
        dataCache.forEach { data ->
            mailCache.forEach {
                if (data.key == it.target) {
                    data.value.mailData.add(it)
                }
            }
        }
        SqlManage.migratorSave(dataCache)
        connection.close()
        GeekMail.debug("数据迁移完成...")

    }
    private fun selectPlayer() {
        val sta = connection.createStatement()
        val res = sta.executeQuery("SELECT * FROM `mail_player_data`")
        try {
            while (res.next()) {
                val name = res.getString("name")
                val uuid = UUID.fromString(res.getString("uuid"))
                val mail = res.getString("mail")
                val newPlayer = res.getBoolean("one_join")
                dataCache[uuid] = PData(name, uuid, mail, newPlayer)
            }
        } finally {
            res.close()
            sta.close()
        }
    }
    private fun selectMail() {
        val sta = connection.createStatement()
        val res = sta.executeQuery("SELECT * FROM `maildata`")
        try {
            while (res.next()) {
                val mailID = UUID.fromString(res.getString("mail_id"))
                val state = when (res.getString("state")) {
                    "已提取" -> MailState.Acquired
                    "未提取" -> MailState.NotObtained
                    else -> MailState.Text
                }
                MailManage.getMailClass(res.getString("type"))?.let {
                    it.setProperty("mailID", mailID)
                    it.setProperty("title", res.getString("title"))
                    it.setProperty("text", res.getString("text"))
                    it.setProperty("sender", UUID.fromString(res.getString("sender")))
                    it.setProperty("target", UUID.fromString(res.getString("target")))
                    it.setProperty("additional", res.getString("additional"))
                    it.setProperty("itemStacks", res.getString("item").deserializeItemStacks())
                    it.setProperty("command", res.getString("commands").split(";"))
                    it.setProperty("senderTime", res.getString("sendertime").toLong())
                    it.setProperty("getTime", res.getString("gettime").toLong())
                    it.setProperty("state", state)
                    mailCache.add(it)
                }
            }
        } finally {
            res.close()
            sta.close()
        }
    }

}