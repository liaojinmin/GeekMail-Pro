package me.geek.mail.command.admin


import me.geek.mail.command.CmdExp
import me.geek.mail.common.template.Template
import org.bukkit.Bukkit
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
                            Template.setStoreItem(argument, item)
                            player.sendMessage("保存成功...")
                        }
                    }
                }
                dynamic("玩家") {
                    execute<CommandSender> { player, context, argument ->
                        Bukkit.getPlayer(context["玩家"])?.let {
                            if (context["动作种类"] == "give") {
                                Template.getStoreItem(context["储存ID"])?.let { items ->
                                    it.giveItem(items)
                                    player.sendMessage("已给与 ${items.itemMeta?.displayName ?: argument} 物品")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}