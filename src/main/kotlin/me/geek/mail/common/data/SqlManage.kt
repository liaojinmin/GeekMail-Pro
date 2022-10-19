package me.geek.mail.common.data

import com.google.common.base.Joiner
import me.clip.placeholderapi.PlaceholderAPI
import me.geek.mail.GeekMail
import me.geek.mail.api.mail.MailManage.buildMailClass

import me.geek.mail.api.mail.MailManage.senderWebMail
import me.geek.mail.api.mail.MailSub
import me.geek.mail.scheduler.sql.*
import me.geek.mail.common.data.MailPlayerData.Companion.defaultsData
import me.geek.mail.modules.settings.SetTings
import org.bukkit.OfflinePlayer
import taboolib.expansion.geek.serialize.serializeItemStacks
import java.sql.Connection
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList

/**
 * 作者: 老廖
 * 时间: 2022/9/15
 *
 **/
object SqlManage {

    private val dataSub by lazy {
        if (SetTings.StorageDate.use_type.equals("mysql", ignoreCase = true)){
            return@lazy Mysql(SetTings.StorageDate)
        } else return@lazy Sqlite(SetTings.StorageDate)
    }

    private val messageCache: MutableMap<String, String> by lazy { ConcurrentHashMap() }

    fun addMessage(key: String, value: String) {
        messageCache[key] = value
    }

    fun getMessage(key: String): String? {
       return messageCache[key]
    }





    fun getConnection(): Connection {
        return dataSub.getConnection()
    }

    fun closeData() {
        dataSub.onClose()
    }

    fun start() {
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
    }



    /**********  玩家操作  **********/

    @Synchronized
    fun insertPlayerData(data: MailPlayerData) {
        getConnection().use {
            this.prepareStatement("INSERT INTO mail_player_data(`uuid`,`name`,`mail`,`one_join`) VALUES(?,?,?,?)").actions { p ->
                p.setString(1, data.uuid.toString())
                p.setString(2, data.name)
                p.setString(3, data.mail)
                p.setBoolean(4, data.OneJoin)
                p.execute()
            }
        }
    }


    /**
     * 更新玩家数据
     * @param data 玩家数据
     */
    @Synchronized
    fun updatePlayerData(data: MailPlayerData) {
        getConnection().use {
            this.prepareStatement("UPDATE `mail_player_data` SET `mail`=?,`one_join`=?,`name`=? WHERE `uuid`=?;")
                .actions { s ->
                    s.setString(1, data.mail)
                    s.setBoolean(2, data.OneJoin)
                    s.setString(3, data.name)
                    s.setString(4, data.uuid.toString())
                    s.executeUpdate()
                }
        }
    }
    /**
     * 查询玩家数据，如果没有则返回默认数据，并一同载入缓存
     * @param targetUid 目标玩家UUID
     * @param name 玩家名称
     * @return data
     */
    @Synchronized
    fun selectPlayerData(targetUid: UUID, name: String): MailPlayerData? {
        var data: MailPlayerData? = null
            getConnection().use {
                this.prepareStatement("SELECT * FROM `mail_player_data` WHERE uuid=?;").actions { s ->
                    s.setString(1, targetUid.toString())
                    val r = s.executeQuery()

                    // 无数据情况
                    if (!r.isBeforeFirst) {
                        data = defaultsData(name, targetUid)
                        insertPlayerData(data!!)
                        return@actions data
                    }
                    // 有数据情况
                    while (r.next()) {
                        val u = UUID.fromString(r.getString("uuid"))
                        val n = r.getString("name")
                        val mails = r.getString("mail")
                        val join = r.getBoolean("one_join")
                        data = MailPlayerData(n, u, mails, join)
                    }
                    val mailData = selectMail(targetUid)
                    data?.mailData?.addAll(mailData)
                    return@actions data
                }
            }
        return data
    }







    /**********  邮件操作  **********/






    @Synchronized
    fun insertMail(mailDate: MailSub) {
        getConnection().use {
            this.prepareStatement(
                "INSERT INTO maildata(`mail_id`,`state`,`type`,`sender`,`target`,`title`,`text`,`additional`,`item`,`commands`,`sendertime`,`gettime`) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)"
            ).actions { p ->
                p.setString(1, mailDate.mailID.toString())
                p.setString(2, mailDate.state)
                p.setString(3, mailDate.name)
                p.setString(4, mailDate.sender.toString())
                p.setString(5, mailDate.target.toString())
                p.setString(6, mailDate.title)
                p.setString(7, mailDate.text)
                p.setString(8, mailDate.additional)
                if (mailDate.itemStacks != null) {
                    p.setString(
                        9, mailDate.itemStacks.serializeItemStacks())
                } else {
                    p.setString(9, "null")
                }
                if (mailDate.command != null) {
                    p.setString(10, Joiner.on(",").join(mailDate.command))
                } else {
                    p.setString(10, "")
                }
                p.setString(11, mailDate.senderTime)
                p.setString(12, mailDate.getTime)
                p.execute()
            }
        }
    }
    /**
     * 插入全局玩家数据
     * @param mailDate 邮件
     * @param players 玩家数组
     */
    @Synchronized
    fun insertGlobalMail(mailDate: MailSub, players: Array<OfflinePlayer>) {
        getConnection().use {
            this.prepareStatement(
                "INSERT INTO maildata(`mail_id`,`state`,`type`,`sender`,`target`,`title`,`text`,`additional`,`item`,`commands`,`sendertime`,`gettime`) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)").actions { p ->
                val sender = mailDate.sender.toString()
                var items = "null"
                var command = ""
                for (ps in players) {
                    val title = PlaceholderAPI.setPlaceholders(ps, mailDate.title)
                    val text = PlaceholderAPI.setPlaceholders(ps, mailDate.text)
                    val mailID = UUID.randomUUID().toString()
                    val target = ps.uniqueId.toString()
                    p.setString(1, mailID)
                    p.setString(2, mailDate.state)
                    p.setString(3, mailDate.name)
                    p.setString(4, sender)
                    p.setString(5, target)
                    p.setString(6, title)
                    p.setString(7, text)
                    p.setString(8, mailDate.additional)
                    if (mailDate.itemStacks != null) {
                        items = mailDate.itemStacks.serializeItemStacks()
                    }
                    p.setString(9, items)
                    if (mailDate.command != null) {
                        command = Joiner.on(",").join(mailDate.command!!)
                    }
                    p.setString(10, command)
                    p.setString(11, mailDate.senderTime)
                    p.setString(12, mailDate.getTime)
                    if (ps.isOnline) {
                        buildMailClass(mailID, mailDate.name, title, text, sender, target, mailDate.state, mailDate.additional!!, mailDate.senderTime, mailDate.getTime, items, command)?.sendMail()
                    }
                    senderWebMail(mailDate.title, mailDate.text, mailDate.appendixInfo, ps.uniqueId)
                    p.addBatch()
                }
                p.executeBatch()
            }
        }
    }

    /**
     * 删除数据库中指定邮件ID的邮件
     * @param mail_id 邮件唯一标识
     */
    fun deleteMail(mail_id: UUID) {
        getConnection().use {
            this.prepareStatement("DELETE FROM `maildata` WHERE `mail_id`=?;").actions { s ->
                s.setString(1, mail_id.toString())
                s.execute()
            }
        }
    }

    /**
     * 删除所有满足条件的邮件
     * 删除数据库中所有指定状态的邮件
     * @param targetID 邮目标玩家
     * @param state 需要删除的邮件状态
     */
    fun deleteStateMail(targetID: UUID, state: String = "已提取") {
        getConnection().use {
            this.prepareStatement("DELETE FROM `maildata` WHERE `target`=? AND `state`=?;").actions { s ->
                s.setString(1, targetID.toString())
                s.setString(2, state)
                s.execute()
            }
        }
    }

    // 邮件ID
    @Synchronized
    fun updateMail(mail: MailSub): Boolean {
        var a = 0
        getConnection().use {
            this.prepareStatement("UPDATE `maildata` SET `state`=?,`getTime`=? WHERE `mail_id`=?;").actions { s ->
                s.setString(1, mail.state)
                s.setString(2, mail.getTime)
                s.setString(3, mail.mailID.toString())
                a = s.executeUpdate()
            }
        }
        GeekMail.debug("&8updateMail * $a")
        return a != 0
    }

    @Synchronized
    fun updateListMail(mail: MutableList<MailSub>): IntArray {
        var a = intArrayOf()
        getConnection().use {
            this.prepareStatement("UPDATE `maildata` SET `state`=?,`getTime`=? WHERE `mail_id`=?;").actions { s ->
                mail.forEach {
                    s.setString(1, it.state)
                    s.setString(2, it.getTime)
                    s.setString(3, it.mailID.toString())
                    s.addBatch()
                }
                a = s.executeBatch()
            }
        }
        GeekMail.debug("&8updateMail * ${a.joinToString()}")
        return a
    }
    /**
     * 向数据库查询该玩家的所有邮件信息 。
     * @param targetUid 目标玩家
     * @return 邮件合集
     */
    @Synchronized
    fun selectMail(targetUid: UUID): MutableList<MailSub> {
        val mail: MutableList<MailSub> = ArrayList()
        getConnection().use {
            this.prepareStatement("SELECT * FROM `maildata` WHERE target=?;").actions { s ->
                s.setString(1, targetUid.toString())
                val r = s.executeQuery()
                if (r.isBeforeFirst) {
                    while (r.next()) {
                        val mailID = r.getString("mail_id")
                        val state = r.getString("state")
                        val type = r.getString("type")
                        val sender = r.getString("sender")
                        val target = r.getString("target")
                        val title = r.getString("title")
                        val text = r.getString("text")
                        val additional = r.getString("additional")
                        val itemStacks = r.getString("item")
                        val commands = r.getString("commands")
                        val senderTime = r.getString("sendertime")
                        val getTime = r.getString("gettime")
                        buildMailClass(mailID, type, title, text, sender, target, state, additional, senderTime, getTime, itemStacks, commands
                        )?.let { mail.add(it) }
                    }
                }
            }
        }
        return mail
    }

    enum class SqlTab(val tab: String) {

        SQLITE_1("CREATE TABLE IF NOT EXISTS `mail_player_data` (" +
                " `uuid` CHAR(36) PRIMARY KEY," +
                " `name` VARCHAR(16) NOT NULL," +
                " `mail` CHAR(254) NOT NULL," +
                " `one_join` CHAR(5) NOT NULL" +
                ");"),

        SQLITE_2("CREATE TABLE IF NOT EXISTS `maildata` (" +
                " `id` integer PRIMARY KEY, " +
                " `mail_id` CHAR(36) NOT NULL, " +
                " `state` VARCHAR(256) NOT NULL, " +
                " `type` text NOT NULL, " +
                " `sender` CHAR(36) NOT NULL, " +
                " `target` CHAR(36) NOT NULL, " +
                " `title` text NOT NULL, " +
                " `text` longtext NOT NULL, " +
                " `additional` text NOT NULL, " +
                " `item` longtext NOT NULL, " +
                " `commands` longtext NOT NULL," +
                " `sendertime` BIGINT(20) NOT NULL," +
                " `gettime` BIGINT(20) NOT NULL);"),

        MYSQL_1("CREATE TABLE IF NOT EXISTS `mail_player_data` (" +
                " `uuid` CHAR(36) NOT NULL UNIQUE," +
                " `name` VARCHAR(16) NOT NULL," +
                " `mail` CHAR(254) NOT NULL," +
                " `one_join` CHAR(5) NOT NULL," +
                "PRIMARY KEY (`uuid`)" +
                ");"),

        MYSQL_2("CREATE TABLE IF NOT EXISTS `maildata` (" +
                " `id` integer NOT NULL AUTO_INCREMENT, " +
                " `mail_id` CHAR(36) NOT NULL UNIQUE, " +
                " `state` VARCHAR(256) NOT NULL, " +
                " `type` text NOT NULL, " +
                " `sender` CHAR(36) NOT NULL, " +
                " `target` CHAR(36) NOT NULL, " +
                " `title` text NOT NULL, " +
                " `text` longtext NOT NULL, " +
                " `additional` text NOT NULL, " +
                " `item` longtext NOT NULL, " +
                " `commands` longtext NOT NULL, " +
                " `sendertime` BIGINT(20) NOT NULL, " +
                " `gettime` BIGINT(20) NOT NULL, " +
                " PRIMARY KEY (`id`,`target`)" +
                ");")
    }

}