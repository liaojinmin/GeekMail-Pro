package me.geek.mail.common.webmail.sub


import me.geek.mail.GeekMail
import me.geek.mail.modules.settings.SetTings
import java.util.*
import javax.mail.Authenticator
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

/**
 * 作者: 老廖
 * 时间: 2022/9/7
 *
 **/
abstract class SubWebMail {
    private val account = GeekMail.config.getString("SmtpSet.account")
    private val password = GeekMail.config.getString("SmtpSet.password")  // "JRKHOKSYAPSOUHOS"
    private val personal = GeekMail.config.getString("SmtpSet.personal")
    private val subjects = GeekMail.config.getString("SmtpSet.subjects")
    private val props = mapOf(
        "mail.smtp.auth" to "true",
        "mail.smtp.host" to GeekMail.config.getString("SmtpSet.host"),
        "mail.smtp.port" to GeekMail.config.getString("SmtpSet.port"),
        "mail.transport.protocol" to "smtp"
    )
    private val properties = Properties().apply { putAll(props) }
    private val authenticator: Authenticator = object : Authenticator() {
        override fun getPasswordAuthentication(): PasswordAuthentication {
            return PasswordAuthentication(account, password)
        }
    }
    private val mailSession: Session = Session.getInstance(properties, authenticator)

    val htmlMessage = MimeMessage(mailSession).apply {
        setFrom(InternetAddress(account, personal, "UTF-8"))
        subject = subjects
    }

    abstract fun onSender(title: String, text: String, app: String, targetID: UUID)
}