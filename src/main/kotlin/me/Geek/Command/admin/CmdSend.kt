package me.Geek.Command.admin

import com.google.common.base.Joiner
import me.Geek.Command.CmdExp
import me.Geek.Configuration.ConfigManager
import me.Geek.Libs.Menu.MItem
import me.Geek.Modules.MailExp
import me.Geek.Modules.MailMoney
import me.Geek.Modules.MailPoints
import me.Geek.Modules.MailText
import me.Geek.api.mail.MailType.*
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand
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
                    values().map { it.name }
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
                            when (valueOf(text[2])) {
                                MONEY_MAIL -> {
                                    MailMoney(
                                        UUID.randomUUID(), uuid, target.uniqueId, text[3], exp[0], add(exp[1])
                                    ).SendMail()
                                        return@execute
                                    }
                                    POINTS_MAIL -> {
                                        MailPoints(
                                            UUID.randomUUID(), uuid, target.uniqueId, text[3], exp[0], exp[1].filter { it.isDigit() }.toInt()
                                        ).SendMail()
                                        return@execute
                                    }
                                    EXP_MAIL -> {
                                        MailExp(
                                            UUID.randomUUID(), uuid, target.uniqueId, text[3], exp[0], exp[1].filter { it.isDigit() }.toInt()
                                        ).SendMail()
                                        return@execute

                                    }
                                    TEXT_MAIL -> {
                                        MailText(
                                            UUID.randomUUID(), uuid, target.uniqueId, text[3], exp[0],
                                        ).SendMail()
                                        return@execute
                                    }
                                    ITEM_MAIL -> {
                                        MItem(
                                            sender as Player, uuid, target.uniqueId, text[3], exp[0],
                                        )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    private fun add(num1: Any): Double {
        val matcher = Pattern.compile("\\d+\\.?\\d").matcher(num1.toString())
        var var1 = 0.0
        if (matcher.find()) {
            var1 = matcher.group().toDouble()
        }
        return var1
    }

}