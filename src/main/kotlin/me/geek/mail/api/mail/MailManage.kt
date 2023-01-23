package me.geek.mail.api.mail


import me.geek.mail.GeekMail
import me.geek.mail.common.settings.SetTings
import me.geek.mail.common.webmail.WebManager
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
        GeekMail.debug("&8准备发送Smtp邮件")
        WebMail?.onSender(title, text, additional, targetUuid)
    }
    @JvmStatic
    fun getMailBuild(@NotNull mailType: String, player: Player?, target: UUID): MailBuild {
        if (!MailData.containsKey(mailType)) error("getMailBuild() 提供了错误的类型参数，请联系相应开发者。。。")
        return MailBuild(mailType, player, target)
    }


    fun getMailClass(name: String): MailSub? {
        return MailData[name]?.javaClass?.invokeConstructor()
    }

    fun buildMail(name: String, vararg fields: Pair<String, Any?>): MailSub {
        val data = getMailClass(name) ?: error("错误的邮件种类，请检查你的代码，或联系相关开发者。。。")
        fields.forEach { (key, value) ->
            if (value != null) {
                data.setProperty(key, value)
            }
        }
        return data
    }


    /**
     * 获取邮件类型 缓存键
     * @return 所有已注册 邮件类型 Key
     */
    @JvmStatic
    fun getMailTypeKeyMap(): List<String> {
        return MailData.keys.filter { it != "MAIL_NORMAL" }
    }



    /*
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

     */
}