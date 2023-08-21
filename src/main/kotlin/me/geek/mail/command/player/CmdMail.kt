package me.geek.mail.command.player



import me.geek.mail.GeekMail
import me.geek.mail.api.mail.MailBuild
import me.geek.mail.api.mail.MailManage
import me.geek.mail.command.CmdExp
import me.geek.mail.utils.colorify
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand
import taboolib.platform.util.sendLang


/**
 * 作者: 老廖
 * 时间: 2022/8/7
 *
 **/
object CmdMail: CmdExp {
    override val command = subCommand {

        dynamic("目标玩家") {
            suggestion<CommandSender> { _, _ ->
                Bukkit.getOfflinePlayers().map { it.name ?: "null" }
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
                            execute<Player> { sender, context, arg ->
                                val mailType = context["邮件种类"]
                                val title = context["标题"].colorify()
                                val text = context["内容"].colorify()
                                val args = arg.colorify().split(";")
                                val target = Bukkit.getOfflinePlayer(context["目标玩家"])
                                MailManage.getMailClass(mailType)?.let {
                                    if (sender.hasPermission(it.permission)) {
                                        try {
                                            if (it.condition(sender, args[0])) {
                                                // 构建邮件信息 start (第一种方法)
                                                MailBuild(mailType, sender, target.uniqueId).build {
                                                    this.title = title
                                                    this.text = text
                                                    this.additional = args[0]
                                                }.sender()
                                                // 构建邮件信息 end
                                            }
                                        } catch (_: IndexOutOfBoundsException) {
                                            return@execute
                                        }
                                    } else {
                                        sender.sendLang("玩家-没有权限-发送邮件")
                                        GeekMail.say("&4玩家 &f${sender.name} &4执行命令缺少权限&f ${it.permission}")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }




}