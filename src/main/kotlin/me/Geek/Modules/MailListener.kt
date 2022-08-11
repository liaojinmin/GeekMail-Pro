package me.Geek.Modules

import me.Geek.Configuration.ConfigManager
import me.Geek.Configuration.LangManager
import me.Geek.GeekMail
import me.Geek.Libs.DataBase.DataManage
import me.Geek.Libs.Menu.MAction
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.getProxyPlayer
import taboolib.module.chat.TellrawJson

/**
 * 作者: 老廖
 * 时间: 2022/7/29
 *
 **/
object MailListener {
    @SubscribeEvent(priority = EventPriority.LOW, ignoreCancelled = true )
    fun onJoin(e: PlayerJoinEvent) {
        Bukkit.getScheduler().scheduleAsyncDelayedTask(GeekMail.instance) {
            val player = e.player
            val m = DataManage.selectTarget(player.uniqueId)
            var a = false
            if (m != null) {
                MailManage.UpTargetCache(player.uniqueId, m)
                val son = TellrawJson()
                for (out in LangManager.JoinMsg) {
                    if (out.contains("{0}")) {
                        var amt = 0
                        m.forEach { mail ->
                            if (mail.state.equals("未提取")) amt++
                        }
                        if (amt != 0) {
                            a = true
                        }
                        son.append(out.replace("{0}", "$amt") + "\n")
                    } else if (out.contains("[action]")) {
                        son.append(out.replace("[action]", "§8[§a§l点击查看§8]" + "\n")).runCommand("/"+GeekMail.menu.cmd)
                    } else {
                        son.append(out + "\n")
                    }
                }
                if (a) {
                    getProxyPlayer(player.uniqueId)?.sendRawMessage(son.toRawMessage())
                    MailManage.Sound(player, "UI_LOOM_TAKE_RESULT", 1.0F, 1.0F)
                } //else {
                  //  GeekMail.say(" 0")
               // }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW, ignoreCancelled = true )
    fun onQuit(e: PlayerQuitEvent) {
        val player = e.player.uniqueId
        if (MailManage.hasTargetCache(player)) {
            MailManage.remTargetCache(player)
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW, ignoreCancelled = true )
    fun onInteract(e: PlayerInteractEvent) {
        if (e.action == Action.LEFT_CLICK_BLOCK) {
            val loc = e.clickedBlock?.location
            if (loc != null) {
                val world = loc.world
                val x = loc.blockX
                val y = loc.blockY
                val z = loc.blockZ
                if ("$world,$x,$y,$z" == ConfigManager.location) {
                    val player = e.player
                    val m = GeekMail.menu.getMenuCommand(GeekMail.menu.cmd)
                    MAction(player, GeekMail.menu.getMenuTag(m), GeekMail.menu.Build(player, m))
               //     Bukkit.getPluginManager().callEvent(PlayerCommandPreprocessEvent(e.player, GeekMail.menu.cmd))
                   // GeekMail.say(" 方块匹配")
                }
            }
        }
    }

}