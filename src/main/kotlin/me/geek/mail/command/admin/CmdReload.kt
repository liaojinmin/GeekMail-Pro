package me.geek.mail.command.admin

import me.geek.mail.command.CmdExp
import me.geek.mail.api.hook.hookPlugin
import me.geek.mail.common.template.Template
import me.geek.mail.common.menu.Menu
import me.geek.mail.modules.settings.SetTings
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
            Menu.closeGui()
            SetTings.onLoad()
            Template.onLoad()
            Menu.onReload()
            hookPlugin.display()
        }
    }
}