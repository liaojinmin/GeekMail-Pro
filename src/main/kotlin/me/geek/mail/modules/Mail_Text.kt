package me.geek.mail.modules


import com.google.gson.annotations.Expose
import me.geek.mail.api.mail.MailSub
import me.geek.mail.modules.settings.SetTings
import org.bukkit.entity.Player
import java.util.UUID

/**
 * 作者: 老廖
 * 时间: 2022/8/6
 */
class Mail_Text() : MailSub() {

    override var sender: UUID = super.sender
    override var target: UUID = super.target
    override val mailType: String = "文本邮件"
    override val permission: String = "mail.exp.text"
    override val mailIcon: String = SetTings.mailIcon.TEXT_MAIL


    override fun giveAppendix(): Boolean {
        return true
    }
    override fun condition(player: Player, appendix: String): Boolean {
        return true
    }
}