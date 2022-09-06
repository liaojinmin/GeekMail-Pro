package me.geek.mail.api.mail


import me.geek.mail.GeekMail.debug

import me.geek.mail.GeekMail.say
import org.bukkit.entity.Player
import org.jetbrains.annotations.NotNull
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.console
import taboolib.library.reflex.Reflex.Companion.invokeConstructor
import taboolib.library.xseries.XSound
import taboolib.module.lang.sendLang
import taboolib.platform.util.asLangText
import java.lang.IllegalArgumentException
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.HashMap

/**
 * 作者: 老廖
 * 时间: 2022/7/29d
 */
object MailManage {
    private val senderCache: MutableMap<UUID, MutableList<MailSub>> = ConcurrentHashMap()
    private val targetCache: MutableMap<UUID, MutableList<MailSub>> = ConcurrentHashMap()
    private val MailData: MutableMap<String, MailSub> = HashMap()

    @JvmStatic
    fun register(@NotNull mail: MailSub) {
      MailData[mail.name] = mail
    }
    @JvmStatic
    fun getMailData(mailType: String) : MailSub? =
        MailData[mailType]
    @JvmStatic
    fun getMailDataMap(): MutableMap<String, MailSub> {
        return MailData
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
    @JvmStatic
    fun buildMailClass(mailID: String, mailType: String, title: String, text: String, sender: String, target: String,
                       state: String , additional: String, senderTime: String, getTime: String, item: String, cmd: String): MailSub? {
        val args = arrayOf(mailID, title, text, sender, target, state, additional, senderTime, getTime, item, cmd)
        return MailData[mailType]?.javaClass?.invokeConstructor(args)
    }
    /**
     * 此方法会先判断缓存中是否存在对应 目标UID 的数据 再进行存入
     *
     * @param senderUuid 发送者ID
     * @param mail 邮件
     */
    fun addSenderCache(senderUuid: UUID, mail: MailSub) {
        if (senderCache.containsKey(senderUuid)) {
            senderCache.forEach { (key: UUID, value: MutableList<MailSub>) ->
                if (key == senderUuid) {
                    value.add(mail)
                    return
                }
            }
        } else {
            val mail1: MutableList<MailSub> = ArrayList()
            mail1.add(mail)
            senderCache[senderUuid] = mail1
        }
    }

    /**
     * 此方法会先判断缓存中是否存在对应 目标UID 的数据 再进行存入
     *
     * @param targetUuid 目标ID
     * @param mail 邮件
     */
    @JvmStatic
    fun addTargetCache(targetUuid: UUID, mail: MailSub) {
        if (targetCache.containsKey(targetUuid)) {
            debug("addTargetCache-已存在缓存-UUID: " + targetUuid + " 邮件ID：" + mail.mailID)
            targetCache.forEach { (key: UUID, value: MutableList<MailSub>) ->
                if (key == targetUuid) {
                    value.add(mail)
                    debug("addTargetCache-已存在缓存.新增-UUID: " + targetUuid + " 邮件ID：" + mail.mailID)
                }
            }
        } else {
            debug("addTargetCache-不在缓存-UUID: " + targetUuid + " 邮件ID：" + mail.mailID)
            val mail1: MutableList<MailSub> = ArrayList()
            mail1.add(mail)
            targetCache[targetUuid] = mail1
            debug("addTargetCache-不在缓存.新增-UUID: " + targetUuid + " 邮件ID：" + mail.mailID)
        }
    }

    // 直接替换缓存中的数据
    fun upTargetCache(targetUuid: UUID, mail: MutableList<MailSub>) {
        targetCache[targetUuid] = mail
    }

    fun remTargetCache(targetUuid: UUID) {
        targetCache.remove(targetUuid)
    }

    fun remIndexTofTarget(targetUuid: UUID, mailID: UUID) {
        if (targetCache.containsKey(targetUuid)) {
            targetCache.forEach { (key: UUID, value: MutableList<MailSub>) ->
                if (key == targetUuid) {
                    value.removeIf { it.mailID == mailID }
                }
            }
        } else {
            say("缓存 null 异常")
        }
    }


    fun getTargetCache(uuid: UUID): MutableList<MailSub> {
        if (targetCache.containsKey(uuid)) {
            return ArrayList(targetCache[uuid]!!)
        }
        return ArrayList()
    }

    fun hasTargetCache(uuid: UUID): Boolean {
        return targetCache.containsKey(uuid)
    }

    @JvmStatic
    fun sendMailMessage(title: String, text: String, vararg player: Player?) {
        // 0 发送者  1 接收者
        try {
            player[0]?.let{
                adaptPlayer(it).sendLang("玩家-发送邮件", player[1]!!.name)
            }
            player[1]?.let { v ->
                adaptPlayer(v).sendLang("玩家-接收邮件", title)
                /**
                 * if (MinecraftVersion.INSTANCE.getMajorLegacy() >= 11300) {
                 * NMSKt.sendToast(player[1], Material.BOOK,"你有新的邮件待查看！", ToastFrame.TASK, ToastBackground.END);
                 * } */
            }
        } catch (ignored: IllegalArgumentException) { }
    }


    fun Sound(player: Player, name: String, volume: Float, potch: Float) {
        val sound: XSound = try {
            XSound.valueOf(name)
        } catch (e: Throwable) {
            say("未知音效: $name")
            return
        }
        sound.play(player, volume, potch)
    }
}