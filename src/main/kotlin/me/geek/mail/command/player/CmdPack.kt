package me.geek.mail.command.player

import me.geek.mail.api.mail.MailManage
import me.geek.mail.command.CmdExp


import me.geek.mail.common.kether.sub.KetherAPI
import me.geek.mail.common.template.Template
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
                        MailManage.getMailData(pack.type)?.let {

                            if (KetherAPI.instantKether(senders, pack.condition).any as Boolean) {
                                KetherAPI.instantKether(senders, pack.action)
                                val target = Bukkit.getOfflinePlayer(context.args()[2]).uniqueId
                                it.javaClass.invokeConstructor(
                                    arrayOf(
                                        UUID.randomUUID().toString(), pack.title, pack.text,
                                        senders.uniqueId.toString(), target.toString(), "未提取",
                                        pack.additional, System.currentTimeMillis().toString(), "0", pack.itemStacks, pack.command
                                    )
                                ).sendMail()

                            } else {
                                KetherAPI.instantKether(senders, pack.deny)
                            }
                        }
                    }
                }
            }
        }
    }
}