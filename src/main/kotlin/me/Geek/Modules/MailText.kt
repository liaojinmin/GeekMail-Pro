package me.Geek.Modules

import java.util.UUID
import me.Geek.api.mail.MailSub
import me.Geek.api.mail.MailType
import me.Geek.Configuration.ConfigManager
import me.Geek.Libs.DataBase.DataManage
import org.bukkit.Bukkit

/**
 * 作者: 老廖
 * 时间: 2022/8/6
 */
class MailText(
    private var MailID: UUID,
    private val sender: UUID,
    private var Target: UUID,
    private val title: String,
    private val text: String
) : MailSub() {

    private val type = MailType.TEXT_MAIL
    override fun getMailID(): UUID {
        return MailID
    }

    override fun getMailType(): MailType {
        return type
    }

    override fun getTitle(): String {
        return title
    }

    override fun getSender(): UUID {
        return sender
    }

    override fun getTarget(): UUID {
        return Target
    }

    override fun getText(): String {
        return text
    }

    override fun getState(): String {
        return "无"
    }

    override fun setState(state: String) {}

    override fun getAppendix(): String {
        return ""
    }

    override fun getType(): String {
        return ConfigManager.TEXT_MAIL
    }

    override fun SendMail() {
        if (sender == ConfigManager.Console) {
            val target = Bukkit.getPlayer(Target)
            DataManage.insert(this)
            if (target != null) {
                // 如果目标玩家在线则载入缓存
                MailManage.addTargetCache(getTarget(), this)
            }
            MailManage.SendMailMessage(this.title, this.text, null, target)
        } else {
            super.SendMail()
        }
    }
}