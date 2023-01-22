package me.geek.mail.modules


import me.geek.mail.api.hook.HookPlugin
import me.geek.mail.api.mail.MailSub
import me.geek.mail.common.kether.sub.KetherAPI
import me.geek.mail.modules.settings.SetTings
import org.bukkit.entity.Player
import java.util.*


/**
 * 作者: 老廖
 * 时间: 2022/8/6
 */
class Mail_Points() : MailSub() {
    constructor(
        senders: UUID,
        targets: UUID,
        points: Int
    ) : this() {
        this.sender = senders
        this.target = targets
        this.additional = points.toString()
        this.appendixInfo = "$additional ${SetTings.POINTS_MAIL}"
    }

    override var sender: UUID = super.sender
    override var target: UUID = super.target
    override val mailType: String = "点券邮件"
    override val permission: String = "mail.exp.points"
    override val mailIcon: String = SetTings.mailIcon.POINTS_MAIL


    override fun giveAppendix(): Boolean {
        HookPlugin.points.givePoints(target, additional.toInt())
        return true
    }

    override fun condition(player: Player, appendix: String): Boolean {
        val d = KetherAPI.instantKether(player, "Points hasTake ${appendix.filter { it.isDigit() }}").any as Boolean
        if (!d) player.sendMessage("[!] 你没有足够的点券")
        return d
    }

}