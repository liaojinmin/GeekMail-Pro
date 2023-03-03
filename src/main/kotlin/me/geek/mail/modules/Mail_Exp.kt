package me.geek.mail.modules

import me.geek.mail.api.mail.MailSub
import me.geek.mail.common.kether.sub.KetherAPI
import me.geek.mail.settings.SetTings
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

/**
 * 作者: 老廖
 * 时间: 2022/8/6
 */
class Mail_Exp() : MailSub() {
    constructor(
        senders: UUID,
        targets: UUID,
        exp: Int
    ) : this() {
        this.sender = senders
        this.target = targets
        this.additional = exp.toString()
      //  this.appendixInfo = "$additional ${SetTings.EXP_MAIL}"
    }

    override var sender: UUID = super.sender
    override var target: UUID = super.target
    override val mailType: String = "经验邮件"
    override val permission: String = "mail.exp.exp"
    override val mailIcon: String = SetTings.mailIcon.EXP_MAIL

    override fun runAppendixInfo() {
        this.appendixInfo = "$additional ${SetTings.EXP_MAIL}"
    }

    override fun giveAppendix(): Boolean {
        Bukkit.getPlayer(this.target)?.giveExp(this.additional.toInt())
        return true
    }

    override fun condition(player: Player, appendix: String): Boolean {
        val d = KetherAPI.instantKether(player, "Exp hasTake ${appendix.filter { it.isDigit() }}").any as Boolean
        if (!d) player.sendMessage("[!] 你没有足够的经验值")
        return d
    }

}