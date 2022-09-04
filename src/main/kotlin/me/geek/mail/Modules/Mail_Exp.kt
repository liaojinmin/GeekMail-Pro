package me.geek.mail.Modules

import me.geek.mail.Configuration.ConfigManager
import me.geek.mail.api.mail.MailManage
import me.geek.mail.common.DataBase.DataManage
import me.geek.mail.api.mail.MailSub
import java.util.UUID
import org.bukkit.Bukkit

/**
 * 作者: 老廖
 * 时间: 2022/8/6
 */
class Mail_Exp(
    override val mailID: UUID,
    override var title: String,
    override var text: String,
    override var sender: UUID,
    override var target: UUID,
    override var state: String,
    override val mailType: String,
    override val additional: String,
    override val appendixInfo: String,
    override val senderTime: String,
    override var getTime: String,

    ) : MailSub() {

    constructor(mailID: UUID, Title: String, Text: String, sende: UUID, targe: UUID, state: String, exp: String) : this(
        mailID = mailID,
        mailType = "经验邮件",
        title = Title,
        text = Text,
        sender = sende,
        target = targe,
        state = state,
        appendixInfo = "$exp ${ConfigManager.EXP_MAIL}",
        additional = exp,
        senderTime = System.currentTimeMillis().toString(),
        getTime = ""
    )
    constructor(mailID: UUID, Title: String, Text: String, sende: UUID, targe: UUID, state: String, exp: String, item: Any?, command: Any?, time: Array<Any>) : this(
        mailID = mailID,
        mailType = "经验邮件",
        title = Title,
        text = Text,
        sender = sende,
        target = targe,
        state = state,
        appendixInfo = "$exp  ${ConfigManager.EXP_MAIL}",
        additional = exp,
        senderTime = time[0].toString(),
        getTime = time[1].toString()
    )

    override fun giveAppendix() {
        Bukkit.getPlayer(this.target)?.giveExp(this.additional.toInt())
    }
}