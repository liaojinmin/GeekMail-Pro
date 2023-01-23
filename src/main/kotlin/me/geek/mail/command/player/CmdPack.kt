package me.geek.mail.command.player

import me.clip.placeholderapi.PlaceholderAPI
import me.geek.mail.api.mail.MailBuild
import me.geek.mail.command.CmdExp


import me.geek.mail.common.kether.sub.KetherAPI
import me.geek.mail.common.template.Template
import me.geek.mail.utils.deserializeItemStacks
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand

/**
 * 作者: 老廖
 * 时间: 2022/8/8
 *
 **/
object CmdPack: CmdExp {
    override val command = subCommand {
        dynamic("模板ID") {
            suggestion<CommandSender> { _, _ ->
                Template.tempPackMap.map { it.key }
            }
            dynamic("目标玩家") {
                suggestion<CommandSender>(uncheck = true) {_, _ ->
                    Bukkit.getOnlinePlayers().map { it.name }
                }
                execute<Player> { senders, context, _ ->
                    val pack = Template.getTempPack(context.args()[1])
                    if (context.args()[2] != "Global") {
                        if (KetherAPI.instantKether(senders, pack.condition).any as Boolean) {
                            KetherAPI.instantKether(senders, pack.action)
                            val target = Bukkit.getOfflinePlayer(context.args()[2])
                            val title = PlaceholderAPI.setPlaceholders(target, pack.title)
                            val text = PlaceholderAPI.setPlaceholders(target, pack.text)
                            MailBuild(pack.type, senders, target.uniqueId).build {
                                this.title = title
                                this.text = text
                                this.additional = pack.additional ?: ""
                                this.item = pack.itemStacks?.deserializeItemStacks()
                                this.command = pack.command
                            }.sender()
                        } else KetherAPI.instantKether(senders, pack.deny)
                    }
                }
            }
        }
    }
}
