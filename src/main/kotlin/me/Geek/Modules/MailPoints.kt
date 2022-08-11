package me.Geek.Modules

import me.Geek.api.mail.MailSub
import java.util.UUID
import me.Geek.api.mail.MailType
import me.Geek.Configuration.ConfigManager
import me.Geek.Libs.DataBase.DataManage
import org.bukkit.Bukkit

/**
 * 作者: 老廖
 * 时间: 2022/7/30
 */
class MailPoints : MailSub {
    private val mailID: UUID
    private val type = MailType.POINTS_MAIL
    private val title: String
    private val sender: UUID
    private var Target: UUID
    private val text: String
    private val points: Int
    private var state: String

    // 点券邮件邮件
    constructor(MailID: UUID, sender: UUID, target: UUID, Title: String, Text: String, points: Int) {
        mailID = MailID
        state = "未提取"
        this.sender = sender
        Target = target
        title = Title
        text = Text
        this.points = points
    }

    // 点券邮件 状态已修改
    constructor(MailID: UUID, state: String, sender: UUID, target: UUID, Title: String, Text: String, points: Int) {
        mailID = MailID
        this.state = state
        this.sender = sender
        Target = target
        title = Title
        text = Text
        this.points = points
    }

    override fun getMailID(): UUID {
        return mailID
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

    override fun getPoints(): Int {
        return points
    }

    // 更改
    override fun setState(state: String) {
        this.state = state
    }
    override fun setTarget(target: UUID) {
        this.Target = target
    }

    override fun getAppendix(): String {
        return points.toString() + " " + ConfigManager.POINTS_MAIL
    }

    override fun getType(): String {
        return ConfigManager.POINTS_MAIL
    }

    override fun SendMail() {
        if (sender == ConfigManager.Console) {
            val target = Bukkit.getPlayer(Target)
            DataManage.insert(this)
            if (target != null) {
                // 如果目标玩家在线则载入缓存
                MailManage.addTargetCache(getTarget(), this)
            }
            MailManage.SendMailMessage(this, null, target)
        } else {
            super.SendMail()
        }
    }
}