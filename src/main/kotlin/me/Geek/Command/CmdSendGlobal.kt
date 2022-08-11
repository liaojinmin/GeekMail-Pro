package me.Geek.Command

import com.google.common.base.Joiner
import me.Geek.Configuration.ConfigManager
import me.Geek.Libs.Menu.MItem
import me.Geek.Libs.Template.Template
import me.Geek.Modules.MailExp
import me.Geek.Modules.MailMoney
import me.Geek.Modules.MailPoints
import me.Geek.Modules.MailText
import me.Geek.api.mail.MailType
import me.Geek.api.mail.MailType.*
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand
import java.util.*
import java.util.regex.Pattern

/**
 * 作者: 老廖
 * 时间: 2022/8/11
 *
 **/
object CmdSendGlobal: CmdExp {

    override val command = subCommand {
        dynamic("模板ID") {
            suggestion<CommandSender>(uncheck = true) { _, _ ->
                Template.getTempPackMap().map { it.key }
            }
            dynamic("全局模式") {
                suggestion<CommandSender> {_, _ ->
                    listOf("全局在线")
                }
                execute<CommandSender> { sender, context, _ ->
                    val value = Joiner.on(",").join(context.args()).split(",")
                    val pack = Template.getTempPack(value[1])
                    val type = pack.type
                    val title = pack.title
                    val text = pack.text
                    val app = pack.appendix
                    when (type) {
                        MONEY_MAIL -> {
                            MailMoney(
                                ConfigManager.Console, ConfigManager.Console, ConfigManager.Console, title, text, add(app)
                            ).SendGlobalMail()
                            return@execute
                        }
                        POINTS_MAIL -> {
                            MailPoints(
                                ConfigManager.Console, ConfigManager.Console, ConfigManager.Console, title, text, app.filter { it.isDigit() }.toInt()
                            ).SendGlobalMail()
                            return@execute
                        }
                        EXP_MAIL -> {
                            MailExp(
                                ConfigManager.Console, ConfigManager.Console, ConfigManager.Console, title, text, app.filter { it.isDigit() }.toInt()
                            ).SendGlobalMail()
                            return@execute

                        }
                        TEXT_MAIL -> {
                            MailText(
                                ConfigManager.Console, ConfigManager.Console, ConfigManager.Console, title, text,
                            ).SendGlobalMail()
                            return@execute
                        }
                        ITEM_MAIL -> TODO()
                    }
                }
            }
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