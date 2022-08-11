package me.Geek.Command

import me.Geek.Configuration.ConfigManager
import me.Geek.Configuration.LangManager
import me.Geek.GeekMail
import me.Geek.Libs.Template.Template
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