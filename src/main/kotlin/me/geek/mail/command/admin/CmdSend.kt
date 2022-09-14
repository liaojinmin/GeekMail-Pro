package me.geek.mail.command.admin

import me.geek.mail.command.CmdExp
import me.geek.mail.api.mail.MailManage
import me.geek.mail.modules.settings.SetTings
import me.geek.mail.utils.colorify
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand
import taboolib.library.reflex.Reflex.Companion.invokeConstructor
import java.util.UUID


/**
 * 作者: 老廖
 * 时间: 2022/7/15
 *
 **/
object CmdSend: CmdExp {

    override val command = subCommand {
        dynamic("目标玩家") {
            suggestion<CommandSender>(uncheck = true) { _, _ ->
                Bukkit.getOnlinePlayers().map { it.name }
            }
            dynamic("邮件种类") {
                suggestion<CommandSender> { _, _ ->
                    MailManage.getMailDataMap().keys.map { it }
                }
                dynamic("标题") {
                    suggestion<CommandSender>(uncheck = true) { _, _ ->
                        listOf("[邮件标题]")
                    }
                    dynamic("内容") {
                        suggestion<CommandSender>(uncheck = true) { _, _ ->
                            listOf("[邮件内容]")
                        }
                        execute<CommandSender> { sender, context, _ ->
                            val mailType = context.args()[2]
                            val title = context.args()[3].colorify()
                            val args = context.args()[4].colorify().split(" ", limit = 2)
                            val target = Bukkit.getOfflinePlayer(context.args()[1])
                            val senders = if (sender is Player) sender.uniqueId else SetTings.Console
                            val pack = try {
                                arrayOf(UUID.randomUUID().toString(), title, args[0], senders.toString(), target.uniqueId.toString(), "未提取", args[1], System.currentTimeMillis().toString(), "0")
                            } catch (e: IndexOutOfBoundsException) {
                                arrayOf(UUID.randomUUID().toString(), title, args[0], senders.toString(), target.uniqueId.toString(), "未提取", "0", System.currentTimeMillis().toString(), "0")
                            }
                            MailManage.getMailData(mailType)?.javaClass?.invokeConstructor(pack)?.sendMail()
                        }
                    }
                }
            }
        }
    }
}