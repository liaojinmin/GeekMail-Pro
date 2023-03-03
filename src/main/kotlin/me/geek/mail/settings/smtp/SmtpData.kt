package me.geek.mail.settings.smtp

/**
 * 作者: 老廖
 * 时间: 2022/10/12
 *
 **/
data class SmtpData(
    val start: Boolean = false,
    val account: String = "****r@163.com",
    val password: String = " ",
    val personal: String = "GeekMail-高级邮件系统",
    val subjects: String = "GeekMail-收件提醒",
    val host: String = "smtp.163.com",
    val port: String = "25"
)