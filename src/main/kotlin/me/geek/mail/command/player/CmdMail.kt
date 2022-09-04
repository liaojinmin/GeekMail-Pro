package me.geek.mail.command.player



import com.google.common.base.Joiner
import me.geek.mail.command.CmdExp
import me.geek.mail.api.mail.MailManage
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand
import taboolib.library.reflex.Reflex.Companion.invokeConstructor
import java.util.*




/**
 * 作者: 老廖
 * 时间: 2022/8/7
 *
 **/
object CmdMail: CmdExp {
    override val command = subCommand {

        dynamic("邮件种类") {
            suggestion<CommandSender> { _, _ ->
                MailManage.getMailDataMap().keys.map { it }
            }
            dynamic("目标玩家") {
                suggestion<CommandSender> { _, _ ->
                    Bukkit.getOnlinePlayers().map { it.name }
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
                            val value = Joiner.on(",").join(context.args()).replace("&", "§").split(",")
                            val type = value[1]
                            val uuid: UUID = sender.uniqueId
                            val target = Bukkit.getOfflinePlayer(value[2]).uniqueId

                            val all = value[4].split(" ")
                            val vars = if (all.size >= 2) all[1] else "0"
                            val title = value[3]
                            val text = all[0]
                            MailManage.getMailData(type)?.javaClass?.invokeConstructor(
                                UUID.randomUUID(),
                                title,
                                text,
                                uuid,
                                target,
                                "未提取",
                                vars,
                                null
                            )?.sendMail()

                        }
                    }
                }
            }
        }
    }




}