package me.geek.mail.command.player



import me.geek.mail.GeekMail
import me.geek.mail.api.mail.MailBuild
import me.geek.mail.command.CmdExp
import me.geek.mail.api.mail.MailManage
import me.geek.mail.utils.colorify
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand
import taboolib.library.reflex.Reflex.Companion.invokeConstructor
import taboolib.platform.util.sendLang
import java.util.*




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
                        execute<Player> { sender, context, _ ->
                            val mailType = context.args()[2]
                            val title = context.args()[3].colorify()
                            val args = context.args()[4].colorify().split(" ", limit = 2)
                            val target = Bukkit.getOfflinePlayer(context.args()[1])
                            MailManage.getMailClass(mailType)?.let {
                                if (sender.hasPermission(it.permission)) {
                                    try {
                                        if (it.condition(sender, args[1])) {

                                            // 构建邮件信息 start (第一种方法)
                                            MailBuild(mailType, sender, target.uniqueId).build {
                                                this.title = title
                                                this.text = args[0]
                                                this.additional = args[1]
                                            }.sender()
                                            // 构建邮件信息 end

                                            /*
                                            // 构建邮件信息 start (第二种方法)
                                            MailBuild(mailType, sender, target.uniqueId)
                                                .setTitle(title)
                                                .setText(args[0])
                                                .setAdditional(args[1])
                                                .sender()
                                            // 构建邮件信息 end
                                             */

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