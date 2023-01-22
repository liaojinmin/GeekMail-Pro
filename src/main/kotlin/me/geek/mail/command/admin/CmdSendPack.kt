package me.geek.mail.command.admin

import me.clip.placeholderapi.PlaceholderAPI
import me.geek.mail.api.mail.MailBuild
import me.geek.mail.api.mail.MailManage
import me.geek.mail.command.CmdExp


import me.geek.mail.common.template.Template
import me.geek.mail.modules.settings.SetTings
import me.geek.mail.utils.deserializeItemStacks
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand
import taboolib.library.reflex.Reflex.Companion.invokeConstructor
import java.util.*

/**
 * 作者: 老廖
 * 时间: 2022/8/8
 *
 **/
object CmdSendPack: CmdExp {
    override val command = subCommand {
        dynamic("模板ID") {
            suggestion<CommandSender> { _, _ ->
                Template.adminPack
            }
            dynamic("目标玩家") {
                suggestion<CommandSender>(uncheck = true) {_, _ ->
                    Bukkit.getOnlinePlayers().map { it.name }
                }
                execute<CommandSender> { senders, context, _ ->
                    val pack = Template.getAdminPack(context.args()[1])!!
                    val target = Bukkit.getOfflinePlayer(context.args()[2])
                    val title = PlaceholderAPI.setPlaceholders(target, pack.title)
                    val text = PlaceholderAPI.setPlaceholders(target, pack.text)
                    MailBuild(pack.type, if (senders is Player) senders else null, target.uniqueId).build {
                        this.title = title
                        this.text = text
                        this.additional = pack.additional ?: ""
                        this.item = pack.itemStacks?.deserializeItemStacks()
                        this.command = pack.command?.split(";")
                    }.sender()
                }
            }
        }
    }
}