package me.geek.mail.modules

import com.google.gson.annotations.Expose
import me.geek.mail.GeekMail
import me.geek.mail.api.mail.MailSub
import me.geek.mail.modules.settings.SetTings
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.platform.compat.replacePlaceholder
import java.util.*

/**
 * 作者: 老廖
 * 时间: 2022/8/6
 */
class Mail_Cmd(
    override val mailID: UUID,
    override val mailType: String,

    override var title: String,
    override var text: String,
    override var sender: UUID,
    override var target: UUID,
    override var state: String,
    override var appendixInfo: String,
    override var command: List<String>?,
    override val senderTime: String,
    override var getTime: String,
    ) : MailSub() {
    @Expose
    override val permission: String = "mail.exp.command"

    @Expose
    override val mailIcon: String = SetTings.mailIcon.CMD_MAIL


    constructor() : this (
        UUID.fromString("00000000-0000-0000-0000-000000000001"),
        "指令邮件",
        "",
        "",
        UUID.fromString("00000000-0000-0000-0000-000000000001"),
        UUID.fromString("00000000-0000-0000-0000-000000000001"),
        "",
        "-",
        null,
        senderTime = "",
        getTime = ""
    )
    constructor(args: Array<String>) : this(
        mailID = UUID.fromString(args[0]),
        mailType = "指令邮件",
        title = args[1],
        text = args[2],
        sender = UUID.fromString(args[3]),
        target = UUID.fromString(args[4]),
        state = args[5],
        appendixInfo = "§6{0} §7${SetTings.CMD_MAIL}",
        command = null,
        senderTime = args[7],
        getTime = args[8]
    ) {
        command = if (args.size >= 11) {
            args[10].split(";")
        } else {
            args[6].split(";")
        }
        appendixInfo = appendixInfo.replace("{0}","${command?.size}")
    }


    override fun giveAppendix() {
        Bukkit.getPlayer(target)?.let {
            command?.let { cmd ->
                cmd.replacePlaceholder(it).forEach { out ->
                    try {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), out)
                    } catch (_: Exception) {}
                }
            } ?: GeekMail.say("giveAppendix 异常&c null")
        }
    }
    override fun condition(player: Player, appendix: String): Boolean {
        return player.isOp
    }





}