package me.geek.mail.command.admin


import me.geek.mail.GeekMail
import me.geek.mail.command.CmdExp
import me.geek.mail.settings.SetTings
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.function.adaptPlayer
import java.io.IOException

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
                            val world = loc.world?.name
                            val x = loc.x
                            val y = loc.y
                            val z = loc.z
                            val location = "$world,$x,$y,$z"
                            try {
                                val data: FileConfiguration = YamlConfiguration.loadConfiguration(SetTings.config.file!!)
                                data["Block.loc"] = location
                                data["Block.hd"] = listOf("&f邮件箱","&e右键点击 &7| &eLEFT CLICK")
                                data.save(SetTings.config.file!!)
                                SetTings.location = loc
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                            HandlerList.unregisterAll(this)
                            e.player.sendMessage("§8[§6§lGeekMail§8] §f设置完毕")
                        }
                    }
                }
            }, GeekMail.instance)

        }
    }
}