package me.Geek.Command.admin

import me.Geek.Command.CmdExp
import me.Geek.GeekMail
import me.Geek.Modules.MailManage
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.function.adaptPlayer

/**
 * 作者: 老廖
 * 时间: 2022/8/7
 *
 **/
object CmdSetBlock : CmdExp {
    override val command = subCommand {
        execute<CommandSender> { sender, _, _ ->
            adaptPlayer(sender).sendTitle("§a请点击指定方块", "", 20, 40, 20)
            Bukkit.getPluginManager().registerEvents(object : Listener {
                @EventHandler
                fun onInteract(e: PlayerInteractEvent) {
                    if (e.player == sender) {
                        val loc = e.clickedBlock?.location
                        if (loc != null) {
                            val world = loc.world
                            val x = loc.blockX
                            val y = loc.blockY
                            val z = loc.blockZ
                            MailManage.CreateBlock("$world,$x,$y,$z")
                            HandlerList.unregisterAll(this)
                            e.player.sendMessage("§8[§6§lGeekMail§8] §f设置完毕")
                            // GeekMail.say(" 设置完毕")
                        }
                    }
                }
            }, GeekMail.instance)

        }
    }
}