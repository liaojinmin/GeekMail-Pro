package me.Geek.Command

import com.google.common.base.Joiner
import me.Geek.GeekMail.say
import me.Geek.Libs.Kether.sub.KetherAPI
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand

/**
 * 作者: 老廖
 * 时间: 2022/8/8
 *
 **/
object CmdTest: CmdExp {
    override val command = subCommand {
        dynamic("测试") {
            suggestion<CommandSender>(uncheck = true) { _, _ ->
                listOf("脚本")
            }
            execute<CommandSender> { sender, context, _ ->
                val value = Joiner.on(",").join(context.args()).replace("&", "§").split(",")
                say(value.toString())
                if (sender is Player) {
                    say("${sender.inventory.contents.filterNotNull().size}")

                }
                say("返回值: ${KetherAPI.instantKether(sender as Player, value[1]).asBoolean()}")
            }
        }
    }
}