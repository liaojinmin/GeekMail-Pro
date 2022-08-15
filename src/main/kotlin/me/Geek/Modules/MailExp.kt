package me.Geek.Modules

import me.Geek.api.mail.MailSub
import java.util.UUID
import me.Geek.api.mail.MailType
import me.Geek.Configuration.ConfigManager
import me.Geek.Libs.DataBase.DataManage
import org.bukkit.Bukkit

/**
 * 作者: 老廖
 * 时间: 2022/8/6
 */
class MailExp : MailSub {
    private var MailID: UUID
    private val type = MailType.EXP_MAIL
    private var title: String
    private val sender: UUID
    private var Target: UUID
    private var text: String
    private val exp: Int
    private var state: String

    constructor(MailID: UUID, sender: UUID, target: UUID, Title: String, Text: String, exp: Int) {
        this.MailID = MailID
        state = "未提取"
        this.sender = sender
        Target = target
        title = Title
        text = Text
        this.exp = exp
    }

    constructor(MailID: UUID, state: String, sender: UUID, target: UUID, Title: String, Text: String, exp: Int) {
        this.MailID = MailID
        this.state = state
        this.sender = sender
        Target = target
        title = Title
        text = Text
        this.exp = exp
    }

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
        return state
    }

    override fun setState(state: String) {
        this.state = state
    }


    override fun getAppendix(): String {
        return exp.toString() + " " + ConfigManager.EXP_MAIL
    }

    override fun getType(): String {
        return ConfigManager.EXP_MAIL
    }

    override fun getExp(): Int {
        return exp
    }

    override fun SendMail() {
        if (sender == ConfigManager.Console) {
            val target = Bukkit.getPlayer(Target)
            DataManage.insert(this)
            if (target != null) {
                MailManage.addTargetCache(getTarget(), this)
            }
            MailManage.SendMailMessage(this.title, this.text, null, target)
        } else {
            super.SendMail()
        }
    }
}