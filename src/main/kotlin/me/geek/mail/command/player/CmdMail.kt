package me.geek.mail.command.player



import me.geek.mail.GeekMail
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
                            val senders = sender.uniqueId
                            MailManage.getMailObjData(mailType)?.let {
                               if (sender.hasPermission(it.permission)) {
                                   try {
                                       if (it.condition(sender, args[1])) it.javaClass.invokeConstructor(
                                           arrayOf(UUID.randomUUID().toString(), title, args[0], senders.toString(), target.uniqueId.toString(), "未提取", args[1], System.currentTimeMillis().toString(), "0")
                                       ).sendMail()
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