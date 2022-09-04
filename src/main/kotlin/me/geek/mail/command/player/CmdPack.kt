package me.geek.mail.command.player

import com.google.common.base.Joiner
import me.geek.mail.command.CmdExp


import me.geek.mail.common.Kether.sub.KetherAPI
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
                me.geek.mail.common.Template.Template.getTempPackMap().map { it.key }
            }
            dynamic("目标玩家") {
                suggestion<CommandSender>(uncheck = true) {_, _ ->
                    Bukkit.getOnlinePlayers().map { it.name }
                }

                execute<Player> { sender, context, _ ->
                    val value = Joiner.on(",").join(context.args()).split(",")
                    val pack = me.geek.mail.common.Template.Template.getTempPack(value[1])
                    val target: UUID
                    if (value[2] != "Global") {
                        target = Bukkit.getOfflinePlayer(value[2]).uniqueId
                        if (KetherAPI.instantKether(sender, pack.condition).any as Boolean) {
                            val uuid = sender.uniqueId
                           // val type = pack.type
                            val title = pack.title
                            val text = pack.text
                            val app = pack.appendix
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