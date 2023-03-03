package me.geek.mail.api.mail


import me.geek.mail.common.webmail.WebManager
import me.geek.mail.settings.SetTings
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.jetbrains.annotations.NotNull
import taboolib.library.reflex.Reflex.Companion.invokeConstructor
import taboolib.library.reflex.Reflex.Companion.setProperty
import java.util.*

/**
 * 作者: 老廖
 * 时间: 2022/7/29d
 */
object MailManage {

    private val MailData: MutableMap<String, MailSub> = HashMap()

    private val WebMail by lazy { if (SetTings.SMTP_SET) { WebManager() } else null }


    val PlayerLock: MutableList<UUID> = mutableListOf()


    /**
     * @param mail 要注册的对象
     */
    @JvmStatic
    fun register(@NotNull mail: MailSub) {
      MailData[mail.name] = mail
    }



    /**
     * 发送web邮件提醒
     */
    @JvmStatic
    fun senderWebMail(title: String, text: String, additional: String, targetUuid: UUID) {
        WebMail?.onSender(title, text, additional, targetUuid)
    }
    fun webIsActive():Boolean {
        return WebMail != null
    }
    fun senderBindMail(user: String, code: String, toMail: String) {
        WebMail?.onSender(user, code, toMail)
    }
    @JvmStatic
    fun getMailBuild(@NotNull mailType: String, player: Player?, target: UUID): MailBuild {
        if (!MailData.containsKey(mailType)) error("getMailBuild() 提供了错误的类型参数，请联系相应开发者。。。")
        return MailBuild(mailType, player, target)
    }

    @JvmStatic
    fun getMailClass(name: String): MailSub? {
        return MailData[name]?.javaClass?.invokeConstructor()
    }

    @Synchronized
    fun buildGlobalMail(mail: MailSub): Array<MailSub> {
        val data = mutableListOf<MailSub>().apply {
            Bukkit.getOfflinePlayers().forEach {
                val a = MailBuild(mail.name, null, it.uniqueId).build {
                    this.title = mail.title
                    this.text = mail.text
                    this.additional = mail.additional
                    this.item = mail.itemStacks
                    this.command = mail.command
                }.run().also { m -> m.setProperty("mailID", UUID.randomUUID()) }
                if (!it.isOnline) add(a) else a.sendMail()
            }
        }
        return data.toTypedArray()
    }


    /**
     * 获取邮件类型 缓存键
     * @return 所有已注册 邮件类型 Key
     */
    @JvmStatic
    fun getMailTypeKeyMap(): List<String> {
        return MailData.keys.filter { it != "MAIL_NORMAL" }
    }

}