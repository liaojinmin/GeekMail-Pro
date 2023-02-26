package me.geek.mail.command.admin

import me.geek.mail.api.mail.MailBuild
import me.geek.mail.command.CmdExp
import me.geek.mail.common.settings.SetTings
import me.geek.mail.common.template.Template
import me.geek.mail.utils.deserializeItemStacks
import org.bukkit.command.CommandSender
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.function.submitAsync


/**
 * 作者: 老廖
 * 时间: 2022/9/8
 *
 **/
object CmdSendGlobal: CmdExp {
    override val command = subCommand {
        dynamic("模板ID") {
            suggestion<CommandSender>(uncheck = true) { _, _ ->
                Template.adminPack
            }
            dynamic("全局模式") {
                suggestion<CommandSender> { _, _ ->
                    listOf("全局玩家")
                }
                execute<CommandSender> { _, context, _ ->
                    val pack = Template.getAdminPack(context.args()[1])!!
                    submitAsync {
                        MailBuild(pack.type, null, SetTings.Console).build {
                            this.title = pack.title
                            this.text = pack.text
                            this.additional = pack.additional ?: ""
                            this.item = pack.itemStacks?.deserializeItemStacks()
                            this.command = pack.command
                        }.run().sendGlobalMail()
                    }
                }
            }
        }
    }
}