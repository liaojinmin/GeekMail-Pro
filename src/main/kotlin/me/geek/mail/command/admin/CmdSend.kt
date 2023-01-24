package me.geek.mail.command.admin

import me.geek.mail.api.mail.MailBuild
import me.geek.mail.api.mail.MailManage
import me.geek.mail.command.CmdExp
import me.geek.mail.utils.colorify
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand


/**
 * 作者: 老廖
 * 时间: 2022/7/15
 *
 **/
object CmdSend: CmdExp {

    override val command = subCommand {
        dynamic("目标玩家") {
            suggestion<CommandSender>(uncheck = true) { _, _ ->
                // 自行管理玩家列表
                Bukkit.getOnlinePlayers().map { it.name }
            }
            dynamic("邮件种类") {
                suggestion<CommandSender> { _, _ ->
                    MailManage.getMailTypeKeyMap().map { it }
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
                            MailBuild(mailType, if (sender is Player) sender else null, target.uniqueId).build {
                                this.title = title
                                this.text = args[0]
                                if (args.size >= 2) this.additional = args[1]
                            }.sender()
                        }
                    }
                }
            }
        }
    }
}