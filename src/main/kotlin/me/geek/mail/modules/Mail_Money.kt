package me.geek.mail.modules

import me.geek.mail.api.hook.HookPlugin
import me.geek.mail.api.mail.MailSub
import me.geek.mail.common.kether.sub.KetherAPI
import me.geek.mail.modules.settings.SetTings
import java.util.UUID
import org.bukkit.Bukkit
import org.bukkit.entity.Player

/**
 * 作者: 老廖
 * 时间: 2022/7/24
 */
class Mail_Money() : MailSub() {

    constructor(
        senders: UUID,
        targets: UUID,
        money: Double
    ) : this() {
        this.sender = senders
        this.target = targets
        this.additional = formatDouble(money)
        this.appendixInfo = "$additional ${SetTings.MONEY_MAIL}"
    }

    override var sender: UUID = super.sender
    override var target: UUID = super.target
    override val mailType: String = "金币邮件"
    override val permission: String = "mail.exp.money"
    override val mailIcon: String = SetTings.mailIcon.MONEY_MAIL

    override fun giveAppendix(): Boolean {
        HookPlugin.money.giveMoney(Bukkit.getOfflinePlayer(target), this.additional.toDouble())
        return true
    }

    override fun condition(player: Player, appendix: String): Boolean {
        val d = KetherAPI.instantKether(player, "Money hasTake ${formatDouble(appendix)}").any as Boolean
        if (!d) player.sendMessage("[!] 你没有足够的金币")
        return d
    }


}
