package me.geek.mail.api.mail


import me.geek.mail.GeekMail
import me.geek.mail.GeekMail.say
import me.geek.mail.common.data.MailPlayerData

import me.geek.mail.common.webmail.WebManager
import me.geek.mail.modules.settings.SetTings
import me.geek.mail.utils.serializeItemStacks
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.jetbrains.annotations.NotNull
import taboolib.common.platform.function.getProxyPlayer
import taboolib.library.reflex.Reflex.Companion.invokeConstructor
import taboolib.library.xseries.XSound
import taboolib.module.lang.sendLang
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * 作者: 老廖
 * 时间: 2022/7/29d
 */
object MailManage {

    private val MailPlayerCache: MutableMap<UUID, MailPlayerData> = ConcurrentHashMap()

    private val MailData: MutableMap<String, MailSub> = HashMap()

    private val WebMail by lazy { if (SetTings.SMTP_SET) { WebManager() } else null }


    val PlayerLock: MutableList<UUID> = mutableListOf()

    /**
     * 发送web邮件提醒
     */
    @JvmStatic
    fun senderWebMail(title: String, text: String, additional: String, targetUuid: UUID) {
        GeekMail.debug("&8准备发送Smtp邮件")
        WebMail?.onSender(title, text, additional, targetUuid)
    }

    /**
     * @param mail 要注册的对象
     */
    @JvmStatic
    fun register(@NotNull mail: MailSub) {
      MailData[mail.name] = mail
    }

    @JvmStatic
    fun senderMail(@NotNull mailType: String, @NotNull title: String, @NotNull text: String,
        @NotNull senderUuid: UUID, @NotNull targetUuid: UUID, additional: String, cmd: String, item: Array<ItemStack>?)
    {
         if (MailData.containsKey(mailType)) {
             val senderTime = System.currentTimeMillis().toString()
             val getTime = "0"
             val items = item.serializeItemStacks()
             val args = arrayOf(UUID.randomUUID(), title, text, senderUuid, targetUuid, "未提取", additional, senderTime, getTime, items, cmd)
             MailData[mailType]?.javaClass?.invokeConstructor(args)?.sendMail()
         }
    }

    /**
     * @param mailID = 邮件ID
     * @param mailType = 邮件种类
     * @param title = 标题
     * @param text = 文本
     * @param sender  = 发送者UUID
     * @param target = 接收者UUID
     * @param state = 邮件状态
     * @param additional = 附件
     * @param senderTime = 发生时间
     * @param getTime = 附件领取时间
     * @param item = 物品类型附件
     * @param cmd = 指令类型附件
     */
    fun buildMailClass(mailID: String, mailType: String, title: String, text: String, sender: String, target: String,
                       state: String , additional: String, senderTime: String, getTime: String, item: String, cmd: String): MailSub? {
        val args = arrayOf(mailID, title, text, sender, target, state, additional, senderTime, getTime, item, cmd)
        return MailData[mailType]?.javaClass?.invokeConstructor(args)
    }


    /**
     * @param mailType 邮件种类名称
     * @return 种类对象 如果不存在则 null
     * 使用: getMailData("MAIL_ITEM")
     */
    fun getMailObjData(mailType: String) : MailSub? {
        return MailData[mailType]
    }

    /**
     * 获取邮件类型 缓存键
     * @return 所有已注册 邮件类型 Key
     */
    fun getMailTypeKeyMap(): List<String> {
        return MailData.keys.filter { it != "MAIL_NORMAL" }
    }


    /**
     * 此方法会先判断缓存中是否存在对应 目标UID 的数据 再进行存入
     *
     * @param targetUuid 目标ID
     * @param mail 邮件
     */
    fun addPlayerMailCache(targetUuid: UUID, mail: MailSub) {
        MailPlayerCache[targetUuid]?.mailData?.add(mail)
    }

    fun remPlayerMail(targetUuid: UUID, mailID: UUID) {
        MailPlayerCache[targetUuid]?.let {
            it.mailData.removeIf { v -> v.mailID == mailID }
        }
    }

    fun getPlayerMailCache(@NotNull uuid: UUID): MutableList<MailSub> {
        return ArrayList(MailPlayerCache[uuid]?.let {
            it.mailData.also { value ->
                val s = value.size
                value.removeIf { mail ->
                   mail.sender == SetTings.Console && mail.senderTime.toLong() <= (System.currentTimeMillis() - SetTings.ExpiryTime)
                }
                val b = s - value.size
                if (b != 0) {
                    getProxyPlayer(uuid)?.sendLang("玩家-邮件到期-删除", b)
                }
            }
        } ?: mutableListOf())
    }



    fun getMailPlayerData(uuid: UUID): MailPlayerData? {
        return MailPlayerCache[uuid]
    }
    fun addMailPlayerData(uuid: UUID, data: MailPlayerData?) {
        data?.let { MailPlayerCache[uuid] = it }
    }
    fun remMailPlayerData(uuid: UUID) {
        MailPlayerCache.remove(uuid)
    }





    fun Player.sound(name: String, volume: Float, potch: Float) {
        val sound: XSound = try {
            XSound.valueOf(name)
        } catch (e: Throwable) {
            say("未知音效: $name")
            return
        }
        sound.play(this, volume, potch)
    }
}