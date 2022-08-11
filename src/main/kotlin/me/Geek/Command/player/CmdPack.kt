package me.Geek.Command.player

import com.google.common.base.Joiner
import me.Geek.Command.CmdExp

import me.Geek.Libs.Kether.sub.KetherAPI
import me.Geek.Libs.Template.Template
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
import java.util.*
import java.util.regex.Pattern

/**
 * 作者: 老廖
 * 时间: 2022/8/8
 *
 **/
object CmdPack: CmdExp {
    override val command = subCommand {
        dynamic("模板ID") {
            suggestion<CommandSender> { _, _ ->
                Template.getTempPackMap().map { it.key }
            }
            dynamic("目标玩家") {
                suggestion<CommandSender>(uncheck = true) {_, _ ->
                    Bukkit.getOnlinePlayers().map { it.name }
                }

                execute<Player> { sender, context, _ ->
                    val value = Joiner.on(",").join(context.args()).split(",")
                    val pack = Template.getTempPack(value[1])
                    val target: UUID
                    if (value[2] != "Global") {
                        target = Bukkit.getOfflinePlayer(value[2]).uniqueId
                        if (KetherAPI.instantKether(sender, pack.condition).any as Boolean) {
                            val uuid = sender.uniqueId
                            val type = pack.type
                            val title = pack.title
                            val text = pack.text
                            val app = pack.appendix
                            when (type) {
                                MONEY_MAIL -> {
                                    MailMoney(
                                        UUID.randomUUID(), uuid, target, title, text, add(app)
                                    ).SendMail()
                                    action(pack.action, sender)
                                    return@execute
                                }
                                POINTS_MAIL -> {
                                    MailPoints(
                                        UUID.randomUUID(), uuid, target, title, text, app.filter { it.isDigit() }.toInt()
                                    ).SendMail()
                                    action(pack.action, sender)
                                    return@execute
                                }
                                EXP_MAIL -> {
                                    MailExp(
                                        UUID.randomUUID(), uuid, target, title, text, app.filter { it.isDigit() }.toInt()
                                    ).SendMail()
                                    action(pack.action, sender)
                                    return@execute

                                }
                                TEXT_MAIL -> {
                                    MailText(
                                        UUID.randomUUID(), uuid, target, title, text,
                                    ).SendMail()
                                    action(pack.action, sender)
                                    return@execute
                                }
                                ITEM_MAIL -> {
                                    MItem(
                                        sender, uuid, target, title, text,
                                    )
                                }
                            }
                        } else {
                            if (!pack.deny.equals("null")) {
                                KetherAPI.instantKether(sender, pack.deny)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun action(pack: String, player: Player) {
        if (pack != "null") {
            KetherAPI.instantKether(player, pack)
        }
    }
    private fun add(num1: Any): Double {
        val matcher = Pattern.compile("\\d+\\.?\\d?\\d").matcher(num1.toString())
        var var1 = 0.0
        if (matcher.find()) {
            var1 = matcher.group().toDouble()
        }
        return var1
    }

}