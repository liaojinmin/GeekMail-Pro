package me.geek.mail.command.admin

import com.google.common.base.Joiner
import me.geek.mail.command.CmdExp
import me.geek.mail.Configuration.ConfigManager
import me.geek.mail.Modules.Mail_Exp
import me.geek.mail.api.mail.MailManage
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand
import taboolib.library.reflex.Reflex.Companion.invokeConstructor
import java.util.UUID
import java.util.regex.Pattern


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
                            val text = Joiner.on(",").join(context.args()).replace("&", "§").split(",")
                            val target = Bukkit.getOfflinePlayer(text[1])
                            val exp = text[4].replace("&", "§").split(" ")
                            val uuid: UUID = if (sender is Player) sender.uniqueId else ConfigManager.Console
                            val time = arrayOf(System.currentTimeMillis(), 0L)
                            try {
                                MailManage.getMailData(text[2])?.javaClass?.invokeConstructor(
                                    UUID.randomUUID(), text[3], exp[0], uuid, target.uniqueId, "未提取", exp[1], null, null, time
                                )?.sendMail()
                            } catch (e: IndexOutOfBoundsException) {
                                MailManage.getMailData(text[2])?.javaClass?.invokeConstructor(
                                    UUID.randomUUID(), text[3], exp[0], uuid, target.uniqueId, "未提取", "0", null, null, time
                                )?.sendMail()
                            }
                        }
                    }
                }
            }
        }
    }
}