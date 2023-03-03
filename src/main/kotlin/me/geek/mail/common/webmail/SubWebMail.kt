package me.geek.mail.common.webmail


import me.geek.mail.settings.SetTings
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
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
    private val account = SetTings.SmtpData.account
    private val password = SetTings.SmtpData.password // "JRKHOKSYAPSOUHOS"
    private val personal = SetTings.SmtpData.personal
    private val subjects = SetTings.SmtpData.subjects
    private val props = mapOf(
        "mail.smtp.auth" to "true",
        "mail.smtp.host" to SetTings.SmtpData.host,
        "mail.smtp.port" to SetTings.SmtpData.port,
        "mail.transport.protocol" to "smtp",
        "mail.smtp.timeout" to "25000",
     //   "mail.smtp.starttls.enable" to "true"
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


    fun File.toHtmlString(): String {
        // 获取HTML文件流
        val htmlSb = StringBuffer()
        BufferedReader(
            InputStreamReader(
                FileInputStream(this), "UTF-8"
            )
        ).use {
            while (it.ready()) {
                htmlSb.append(it.readLine())
            }
        }
        return htmlSb.toString()
    }
}