package me.geek.mail.command.player

import me.geek.mail.api.data.SqlManage
import me.geek.mail.api.data.SqlManage.getData
import me.geek.mail.api.event.MailBindEvent
import me.geek.mail.command.CmdExp
import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand
import taboolib.platform.util.sendLang

object CmdBind: CmdExp {
    override val command = subCommand {
        dynamic("验证码") {
            execute<Player> { sender, context, _ ->
                SqlManage.bindCode[sender.uniqueId]?.let {
                    val text = it.split(";")
                    if (context.args()[1] == text[0]) {
                        sender.getData().mail = text[1]
                        SqlManage.bindCode.remove(sender.uniqueId)
                        MailBindEvent(sender, text[1]).call()
                        sender.sendLang("玩家-邮箱绑定-成功")
                    }
                }
            }
        }
    }
}