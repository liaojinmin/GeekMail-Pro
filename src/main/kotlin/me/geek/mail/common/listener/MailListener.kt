package me.geek.mail.common.listener



import me.geek.mail.GeekMail
import me.geek.mail.api.mail.MailManage
import me.geek.mail.common.DataBase.DataManage
import me.geek.mail.common.menu.MAction
import me.geek.mail.common.menu.Menu
import org.bukkit.Bukkit
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.lang.sendLang

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
            val list = DataManage.selectTarget(player.uniqueId)
            if (list != null) {
                MailManage.upTargetCache(player.uniqueId, list)
                var amt = 0
                list.forEach { mail ->
                    if (mail.state == "未提取") amt++
                }
                if (amt != 0) {
                    adaptPlayer(player).sendLang("玩家-加入游戏-提醒", amt,)
                }
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
        if (e.action == Action.RIGHT_CLICK_BLOCK || e.action == Action.LEFT_CLICK_BLOCK) {
            val loc = e.clickedBlock?.location
            if (loc != null) {
                val world = loc.world
                val x = loc.blockX
                val y = loc.blockY
                val z = loc.blockZ
                if ("$world,$x,$y,$z" == me.geek.mail.Configuration.ConfigManager.location) {
                    val player = e.player
                    Menu.getMenuCommand(Menu.cmd!!)?.let {
                        MAction(player, Menu.getSession(it), Menu.Build(player, it))
                    }
                }
            }
        }
    }

}