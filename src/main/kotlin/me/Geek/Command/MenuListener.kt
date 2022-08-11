package me.Geek.Command

import me.Geek.GeekMail
import me.Geek.Libs.Menu.MAction
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent


object MenuListener {

    @SubscribeEvent(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onCommand(e: PlayerCommandPreprocessEvent) {
        val message = e.message.removePrefix("/")
        if (message.isNotBlank()) {
            if (GeekMail.menu.getMenuCommand(message) != null) {
                val player = e.player
                val m = GeekMail.menu.getMenuCommand(message)
                MAction(player, GeekMail.menu.getMenuTag(m), GeekMail.menu.Build(player, m))
                e.isCancelled = true
                return
            }
        }
    }
}