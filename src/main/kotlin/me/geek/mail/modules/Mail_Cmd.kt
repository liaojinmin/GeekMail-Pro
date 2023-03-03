package me.geek.mail.modules

import me.geek.mail.api.mail.MailSub
import me.geek.mail.settings.SetTings
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.platform.compat.replacePlaceholder
import java.util.*

/**
 * 作者: 老廖
 * 时间: 2022/8/6
 */
class Mail_Cmd() : MailSub() {

    constructor(
        senders: UUID,
        targets: UUID,
        cmd: List<String>
    ) : this() {
        this.sender = senders
        this.target = targets
        this.command = cmd
    }

   // override var command: List<String> = emptyList()

    override var sender: UUID = super.sender
    override var target: UUID = super.target
    override val mailType: String = "指令邮件"
    override val permission: String = "mail.exp.command"
    override val mailIcon: String = SetTings.mailIcon.CMD_MAIL


    override fun runAppendixInfo() {
        this.appendixInfo = "§6${command.size} §7${SetTings.CMD_MAIL}"
    }


    override fun giveAppendix(): Boolean {
        return Bukkit.getPlayer(target)?.let {
            command?.let { cmd ->
                cmd.replacePlaceholder(it).forEach { out ->
                    try {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), out)
                    } catch (_: Exception) {}
                }
            }
            true
        } ?: false
    }
    override fun condition(player: Player, appendix: String): Boolean {
        return player.isOp
    }





}