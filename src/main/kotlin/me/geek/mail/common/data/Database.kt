package me.geek.mail.common.data

import com.google.common.base.Joiner
import me.clip.placeholderapi.PlaceholderAPI
import me.geek.mail.GeekMail.debug
import me.geek.mail.api.mail.MailManage.buildMailClass

import me.geek.mail.api.mail.MailManage.senderWebMail
import me.geek.mail.api.mail.MailSub
import me.geek.mail.common.data.sub.*
import me.geek.mail.common.data.sub.MailPlayerData.Companion.defaut_Data
import me.geek.mail.common.serialize.base64.StreamSerializer
import me.geek.mail.modules.settings.SetTings.DATA_TYPE
import org.bukkit.OfflinePlayer
import java.sql.Connection
import java.util.*
import kotlin.collections.ArrayList

/**
 * 作者: 老廖
 * 时间: 2022/9/15
 *
 **/
class Database {
    private var dataSub: DataSub? = null
    private val MAIL_PLAYER_DATA = HashMap<UUID, MailPlayerData?>()



    private fun getConnection(): Connection {
        return dataSub!!.connection
    }

    fun closeData() {
        dataSub!!.onStop()
    }

    fun start() {
        dataSub = if (DATA_TYPE.equals("mysql", ignoreCase = true)){
            Mysql()
        } else Sqlite()
        dataSub!!.onLoad()
    }

    fun addMailPlayerData(uuid: UUID, data: MailPlayerData?) {
        MAIL_PLAYER_DATA[uuid] = data
    }

    fun remMailPlayerData(uuid: UUID) {
        MAIL_PLAYER_DATA.remove(uuid)
    }

    fun getMailPlayerData(uuid: UUID): MailPlayerData? {
        return MAIL_PLAYER_DATA[uuid]
    }


    @Synchronized
    fun insertPlayerData(data: MailPlayerData) {
        getConnection().use {
            this.prepareStatement("INSERT INTO mail_player_data(`uuid`,`name`,`mail`,`one_join`) VALUES(?,?,?,?)").run { p ->
                p.setString(1, data.uuid.toString())
                p.setString(2, data.name)
                p.setString(3, data.mail)
                p.setBoolean(4, data.OneJoin)
                p.execute()
            }
        }
    }

    @Synchronized
    fun insertMailData(mailDate: MailSub) {
        getConnection().use {
            this.prepareStatement(
                "INSERT INTO maildata(`mail_id`,`state`,`type`,`sender`,`target`,`title`,`text`,`additional`,`item`,`commands`,`sendertime`,`gettime`) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)"
            ).run { p ->
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
                        9, StreamSerializer.serializeItemStacks(mailDate.itemStacks)
                    )
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
    fun insert(mailDate: MailSub, players: Array<OfflinePlayer>) {
        getConnection().use {
            this.prepareStatement(
                "INSERT INTO maildata(`mail_id`,`state`,`type`,`sender`,`target`,`title`,`text`,`additional`,`item`,`commands`,`sendertime`,`gettime`) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)"
            ).run { p ->
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
                        items = StreamSerializer.serializeItemStacks(mailDate.itemStacks)
                    }
                    p.setString(9, items)
                    if (mailDate.command != null) {
                        command = Joiner.on(",").join(mailDate.command!!)
                    }
                    p.setString(10, command)
                    p.setString(11, mailDate.senderTime)
                    p.setString(12, mailDate.getTime)
                    if (ps.isOnline) {
                        buildMailClass(
                            mailID,
                            mailDate.name,
                            title, text,
                            sender, target,
                            mailDate.state,
                            mailDate.additional!!, mailDate.senderTime, mailDate.getTime, items, command
                            )?.sendMail()

                       // sendMailMessage(title, text, null, ps.player)
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
    fun delete(mail_id: UUID) {
        getConnection().use {
            this.prepareStatement("DELETE FROM `maildata` WHERE `mail_id`=?;").run { s ->
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
    fun delete(targetID: UUID, state: String?) {
        getConnection().use {
            this.prepareStatement("DELETE FROM `maildata` WHERE `target`=? AND `state`=?;").run { s ->
                s.setString(1, targetID.toString())
                s.setString(2, state)
                s.execute()
            }
        }
    }

    /**
     * 向数据库查询该玩家的所有邮件信息 。
     * @param targetUid 目标玩家
     * @return 邮件合集
     */
    @Synchronized
    fun selectPlayerMail(targetUid: UUID): MutableList<MailSub> {
        val mail: MutableList<MailSub> = ArrayList()
            getConnection().use {
                this.prepareStatement("SELECT * FROM `maildata` WHERE target=?;").run { s ->
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
                this.prepareStatement("SELECT * FROM `mail_player_data` WHERE uuid=?;").run { s ->
                    s.setString(1, targetUid.toString())
                    val r = s.executeQuery()
                    if (!r.isBeforeFirst) {
                        data = defaut_Data(name, targetUid)
                        insertPlayerData(data!!)
                        addMailPlayerData(targetUid, data)
                        return@run data
                    }
                    while (r.next()) {
                        val Uuid = UUID.fromString(r.getString("uuid"))
                        val Name = r.getString("name")
                        val mails = r.getString("mail")
                        val join = r.getBoolean("one_join")
                        debug("name: $Name mails: $mails")
                        data = MailPlayerData(Name, Uuid, mails, join)
                    }
                    addMailPlayerData(targetUid, data)
                    return@run data
                }
            }
        return data
    }

    @Synchronized
    fun selectPlayerBindMail(targetUid: UUID): Array<String>? {
        getConnection().use {
            this.prepareStatement("SELECT mail,name FROM `mail_player_data` WHERE uuid=?;").run { s ->
                s.setString(1, targetUid.toString())
                val r = s.executeQuery()
                if (!r.isBeforeFirst) {
                    return@run null
                }
                var mails = ""
                var name = ""
                while (r.next()) {
                    mails = r.getString("mail")
                    name = r.getString("name")
                }
                return@run arrayOf(name, mails)
            }
        }
        return null
    }

    // 邮件ID
    @Synchronized
    fun update(mail: MailSub) {
        getConnection().use {
            this.prepareStatement("UPDATE `maildata` SET `state`=?,`getTime`=? WHERE `mail_id`=?;").run { s ->
                s.setString(1, mail.state)
                s.setString(2, mail.getTime)
                s.setString(3, mail.mailID.toString())
                s.executeUpdate()
            }
        }
    }
    @Synchronized
    fun update(mail: MutableList<MailSub>) {
        getConnection().use {
            this.prepareStatement("UPDATE `maildata` SET `state`=?,`getTime`=? WHERE `mail_id`=?;").run { s ->
                mail.forEach {
                    s.setString(1, it.state)
                    s.setString(2, it.getTime)
                    s.setString(3, it.mailID.toString())
                    s.addBatch()
                }
                s.executeBatch()
            }
        }
    }

    /**
     * 更新玩家数据
     * @param data 玩家数据
     */
    @Synchronized
    fun update(data: MailPlayerData) {
        getConnection().use {
            this.prepareStatement("UPDATE `mail_player_data` SET `mail`=?,`one_join`=?,`name`=? WHERE `uuid`=?;")
                .run { s ->
                    s.setString(1, data.mail)
                    s.setBoolean(2, data.OneJoin)
                    s.setString(3, data.name)
                    s.setString(4, data.uuid.toString())
                    s.executeUpdate()
                }
        }
    }
}