package me.geek.mail.command



import me.geek.mail.GeekMail
import me.geek.mail.api.mail.MailManage
import me.geek.mail.command.admin.*
import me.geek.mail.command.player.*
import me.geek.mail.common.webmail.WebManager
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.platform.command.*
import taboolib.expansion.createHelper

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
        createHelper()
    }

    @CommandBody(permission = "admin")
    val test = subCommand {
        dynamic("菜单名称") {
            execute<Player> { _, _, _ ->
            }
        }
    }
}