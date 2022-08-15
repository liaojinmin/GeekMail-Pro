package me.Geek.Modules

import me.Geek.GeekMail.instance
import me.Geek.api.mail.MailSub
import java.util.UUID
import me.Geek.api.mail.MailType
import me.Geek.Configuration.ConfigManager
import me.Geek.Libs.DataBase.DataManage
import org.bukkit.Bukkit

/**
 * 作者: 老廖
 * 时间: 2022/7/24
 */
class MailMoney : MailSub {
    private var MailID: UUID
    private val type = MailType.MONEY_MAIL

    private var title: String

    private val sender: UUID
    private var Target: UUID

    private var text: String
    private var money = 0.0
    private var state: String


    // 金币邮件
    constructor(MailID: UUID, sender: UUID, target: UUID, Title: String, Text: String, money: Double) {
        this.MailID = MailID
        this.state = "未提取"
        this.sender = sender
        this.Target = target
        this.title = Title
        this.text = Text
        this.money = money
    }

    // 金币邮件 状态已修改
    constructor(MailID: UUID, state: String, sender: UUID, target: UUID, Title: String, Text: String, money: Double) {
        this.MailID = MailID
        this.state = state
        this.sender = sender
        this.Target = target
        this.title = Title
        this.text = Text
        this.money = money
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

    override fun getMoney(): Double {
        return money
    }

    override fun getState(): String {
        return state
    }


    override fun setState(state: String) {
        this.state = state
    }


    // 更改
    fun setTitle(title: String) {
        this.title = title
    }

    fun setText(text: String) {
        this.text = text
    }

    override fun getAppendix(): String {
        return "$money ${ConfigManager.MONEY_MAIL}"
    }

    override fun getType(): String {
        return ConfigManager.MONEY_MAIL
    }

    override fun SendMail() {
        Bukkit.getScheduler().scheduleAsyncDelayedTask(instance) {
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
}