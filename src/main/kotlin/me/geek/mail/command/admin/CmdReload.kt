package me.geek.mail.command.admin

import me.geek.mail.command.CmdExp
import me.geek.mail.Configuration.ConfigManager
import me.geek.mail.Configuration.LangManager
import me.geek.mail.GeekMail
import me.geek.mail.common.Template.Template
import org.bukkit.command.CommandSender
import taboolib.common.platform.command.subCommand

/**
 * 作者: 老廖
 * 时间: 2022/8/4
 *
 **/
object CmdReload: CmdExp {
    override val command = subCommand {
        execute<CommandSender> { _, _, _ ->
            GeekMail.menu.CloseGui()
            ConfigManager.Load()
            LangManager.onReload()
            Template.onLoad()
            GeekMail.menu.onReload()
        }
    }
}