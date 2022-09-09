package me.geek.mail.common.webmail.sub


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
    private val account = "hsdserver@163.com"
    private val password = "JRKHOKSYAPSOUHOS"
    private val personal = "GeekMail-高级邮件系统"
    private val subjects = "GeekMail-收件提醒"
    private val props = mapOf(
        "mail.smtp.auth" to "true",
        "mail.smtp.host" to "smtp.163.com",
        "mail.smtp.port" to "25",
        "mail.transport.protocol" to "smtp"
    )
    private val properties = Properties().apply { putAll(props) }
    private var authenticator: Authenticator = object : Authenticator() {
        override fun getPasswordAuthentication(): PasswordAuthentication {
            return PasswordAuthentication(account, password)
        }
    }
    private var mailSession: Session = Session.getInstance(properties, authenticator)

    val htmlMessage = MimeMessage(mailSession).apply {
        setFrom(InternetAddress(account, personal, "UTF-8"))
        subject = subjects
    }

    abstract fun onSender(title: String, text: String, app: String, name: String)
}