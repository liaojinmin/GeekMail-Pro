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
                        dynamic("附件") {
                            suggestion<CommandSender>(uncheck = true) { _, _ ->
                                listOf("[附件内容]", "0")
                            }
                            execute<CommandSender> { sender, context, arg ->
                                val mailType = context["邮件种类"]
                                val title = context["标题"].colorify()
                                val text = context["内容"].colorify()
                                val args = arg.colorify().split(";")
                                val target = Bukkit.getOfflinePlayer(context["目标玩家"])

                                MailBuild(mailType, if (sender is Player) sender else null, target.uniqueId).build {
                                    this.title = title
                                    this.text = text
                                    if (mailType == "CMD_MAIL") {
                                        setCommands(args.toMutableList())
                                    } else this.additional = args[0]
                                }.sender()

                            }
                        }
                    }
                }
            }
        }
    }
}