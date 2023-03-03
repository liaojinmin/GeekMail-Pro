package me.geek.mail.command.admin

import me.geek.mail.GeekMail
import me.geek.mail.command.CmdExp
import me.geek.mail.common.template.Template
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


import taboolib.common.platform.command.subCommand
import taboolib.platform.util.giveItem

object CmdStore: CmdExp {
    override val command = subCommand {
        dynamic("动作种类") {
            suggestion<CommandSender> { _, _ ->
                listOf("give","save")
            }
            dynamic("储存ID") {
                suggestion<CommandSender>(uncheck = true) { _, _ ->
                    Template.getStoreKeys()
                }
                execute<Player> { player, context, argument ->
                    GeekMail.say(argument)
                    val action = context.args()[1]
                    if (action == "give") {
                        Template.getStoreItem(argument)?.let {
                            player.giveItem(it)
                            player.sendMessage("已给与 ${it.itemMeta?.displayName ?: argument} 物品")
                        }
                    } else if (action == "save") {
                       val item = player.itemInHand
                        if (item.type == Material.AIR) {
                            player.sendMessage("你手上没有物品")
                        } else {
                           // if (Template.getStoreItem(argument) == null) {
                                Template.setStoreItem(argument, item)
                                player.sendMessage("保存成功...")

                         //   } else player.sendMessage("已存在这个物品")
                        }
                    }
                }
            }
        }
    }
}