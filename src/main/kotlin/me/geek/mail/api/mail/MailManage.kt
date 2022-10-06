package me.geek.mail.api.mail


import me.geek.mail.GeekMail
import me.geek.mail.GeekMail.say
import me.geek.mail.common.serialize.base64.StreamSerializer
import me.geek.mail.common.webmail.WebManager
import me.geek.mail.modules.settings.SetTings
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.jetbrains.annotations.NotNull
import taboolib.common.platform.function.adaptPlayer
import taboolib.library.reflex.Reflex.Companion.invokeConstructor
import taboolib.library.xseries.XSound
import taboolib.module.lang.sendLang
import java.lang.IllegalArgumentException
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * 作者: 老廖
 * 时间: 2022/7/29d
 */
object MailManage {

    private val senderCache: MutableMap<UUID, MutableList<MailSub>> = ConcurrentHashMap()
    private val targetCache: MutableMap<UUID, MutableList<MailSub>> = ConcurrentHashMap()
    private val MailData: MutableMap<String, MailSub> = HashMap()
    private val WebMail by lazy { if (SetTings.SMTP_SET) { WebManager() } else null }

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
    fun senderMail(
        @NotNull mailType: String, @NotNull title: String, @NotNull text: String,
        @NotNull senderUuid: UUID, @NotNull targetUuid: UUID, additional: String, cmd: String, item: Array<ItemStack>?) {
         if (MailData.containsKey(mailType)) {
             val senderTime = System.currentTimeMillis().toString()
             val getTime = "0"
             val items = StreamSerializer.serializeItemStacks(item)
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
    fun getMailData(mailType: String) : MailSub? {
        return MailData[mailType]
    }

    /**
     * 获取邮件类型 缓存键
     * @return 所有已注册 邮件类型 Key
     */
    fun getMailDataMap(): MutableSet<String> {
        return MailData.keys
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
    fun addTargetCache(targetUuid: UUID, mail: MailSub) {
        if (targetCache.containsKey(targetUuid)) {
            targetCache.forEach { (key: UUID, value: MutableList<MailSub>) ->
                if (key == targetUuid) { value.add(mail) }
            }
        } else {
            val mail1: MutableList<MailSub> = ArrayList()
            mail1.add(mail)
            targetCache[targetUuid] = mail1
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

    fun getTargetCache(@NotNull uuid: UUID): MutableList<MailSub> {
        if (targetCache.containsKey(uuid)) {
            return ArrayList(targetCache[uuid]!!)
        }
        return mutableListOf()
    }

    fun hasTargetCache(uuid: UUID): Boolean {
        return targetCache.containsKey(uuid)
    }

    /*
    fun sendMailMessage(title: String, text: String, vararg player: Player?) {

        // 0 发送者  1 接收者
        try {
            player[0]?.let{ v1 ->
                adaptPlayer(v1).sendLang("玩家-发送邮件", player[1]!!.name)
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

     */


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