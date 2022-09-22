package me.geek.mail.command



import me.geek.mail.GeekMail

import me.geek.mail.command.admin.*
import me.geek.mail.command.player.*

import org.bukkit.command.CommandSender

import taboolib.common.platform.command.*
import taboolib.common.platform.function.adaptCommandSender

import taboolib.module.chat.TellrawJson
import taboolib.module.lang.sendLang

@CommandHeader(name = "GeekMail", aliases = ["gkm"], permissionDefault = PermissionDefault.TRUE )
object CmdCore {


    @CommandBody(permission = "mail.command.admin", optional = true)
    val send = CmdSend.command

    @CommandBody(permission = "mail.command.admin")
    val reload = CmdReload.command

    @CommandBody(permission = "mail.command.admin")
    val setblock = CmdSetBlock.command

    @CommandBody(permission = "mail.command.admin")
    val global = CmdSendGlobal.command


    @CommandBody(permission = "mail.command.pack")
    val pack = CmdPack.command

    @CommandBody(permissionDefault = PermissionDefault.TRUE)
    val mail = CmdMail.command


    @CommandBody
    val main = mainCommand {
        execute { sender, _, _ ->
            createHelp(sender)
        }
    }

    private fun createHelp(sender: CommandSender) {
        val s = adaptCommandSender(sender)
        s.sendMessage("")
        TellrawJson()
            .append("  ").append("§f§lGeekMail§8-§6Pro")
            .hoverText("§7现代化高级邮件系统插件 By GeekCraft.ink")
            .append(" ").append("§f${GeekMail.VERSION} §e付费版")
            .hoverText("""
                §7插件版本: §f${GeekMail.VERSION}
            """.trimIndent()).sendTo(s)
        s.sendMessage("")
        s.sendMessage("  §7指令: §f/gkm §8[...]")
        if (sender.hasPermission("mail.command.admin")) {
            s.sendLang("CMD-HELP-ADMIN")
        }
        s.sendLang("CMD-HELP-PLAYER")

    }
}