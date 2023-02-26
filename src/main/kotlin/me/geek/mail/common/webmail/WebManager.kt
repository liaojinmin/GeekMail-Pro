package me.geek.mail.common.webmail

import me.geek.mail.GeekMail
import me.geek.mail.api.data.SqlManage.getData
import me.geek.mail.api.event.WebMailSenderEvent
import org.bukkit.Bukkit
import taboolib.common.platform.function.releaseResourceFile
import java.io.File
import java.util.*
import javax.mail.Message
import javax.mail.Transport
import javax.mail.internet.InternetAddress


/**
 * 作者: 老廖
 * 时间: 2022/9/6
 *
 **/
class WebManager : SubWebMail() {

    private val html by lazy {
        File(GeekMail.instance.dataFolder, "web.html").also {
            if (!it.exists()) releaseResourceFile("web.html", true)
        }.toHtmlString()
    }
    private val bind by lazy {
        File(GeekMail.instance.dataFolder, "bind.html").also {
            if (!it.exists()) releaseResourceFile("bind.html", true)
        }.toHtmlString()
    }
    fun onSender(user: String, code: String, toMail: String) {
        val out = bind
            .replace("{player}", user)
            .replace("{code}", code)
        htmlMessage.setRecipient(Message.RecipientType.TO, InternetAddress(toMail))
        htmlMessage.setContent(out, "text/html;charset=gb2312")
        GeekMail.debug("发送邮箱绑定验证码,目标玩家: $user")
        Transport.send(htmlMessage)
    }

    override fun onSender(title: String, text: String, app: String, targetID: UUID){
        var to = ""
        var name = ""
        val data = Bukkit.getPlayer(targetID)?.getData()

        if (data != null) {
            to = data.mail
            name = data.user
        }

        if (to.isEmpty() || name.isEmpty()) {
            GeekMail.debug("目标玩家: $name 邮箱为空: $to")
            return
        }

        val event = WebMailSenderEvent(to, title, text, app, name)
        event.call()
        if (event.isCancelled) return

        val out = html
            .replace("{name}", color.replace(name,""))
            .replace("{title}", color.replace(title,""))
            .replace("{text}", color.replace(text,""))
            .replace("{app}", color.replace(app, ""))
        htmlMessage.setRecipient(Message.RecipientType.TO, InternetAddress(to))
        htmlMessage.setContent(out, "text/html;charset=gb2312")
        GeekMail.debug("发送Web邮件提醒,目标玩家: $name")
        Transport.send(htmlMessage)
    }


    private val color by lazy { Regex("""(§|&)([a-zA-Z0-9]{1})""") }

}
