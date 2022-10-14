package me.geek.mail.command.admin

import me.clip.placeholderapi.PlaceholderAPI
import me.geek.mail.GeekMail
import me.geek.mail.api.mail.MailManage
import me.geek.mail.command.CmdExp


import me.geek.mail.common.kether.sub.KetherAPI
import me.geek.mail.common.template.Template
import me.geek.mail.modules.settings.SetTings
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
                    GeekMail.debug("command: ${pack.command}")
                    val target = Bukkit.getOfflinePlayer(context.args()[2])
                    val title = PlaceholderAPI.setPlaceholders(target, pack.title)
                    val text = PlaceholderAPI.setPlaceholders(target, pack.text)
                    val mail = if (senders is Player) {
                        arrayOf(
                            UUID.randomUUID().toString(), title, text,
                            senders.uniqueId.toString(), target.uniqueId.toString(), "未提取",
                            pack.additional, System.currentTimeMillis().toString(), "0", pack.itemStacks, pack.command
                        )
                    } else {
                        arrayOf(
                            UUID.randomUUID().toString(), title, text,
                            SetTings.Console.toString(), target.uniqueId.toString(), "未提取",
                            pack.additional, System.currentTimeMillis().toString(), "0", pack.itemStacks, pack.command
                        )
                    }
                    MailManage.getMailData(pack.type)?.javaClass?.invokeConstructor(mail)?.sendMail()
                }
            }
        }
    }
}