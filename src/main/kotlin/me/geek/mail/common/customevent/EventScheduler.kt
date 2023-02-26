package me.geek.mail.common.customevent


import me.geek.mail.api.event.MailBindEvent
import me.geek.mail.api.event.NewPlayerJoinEvent
import me.geek.mail.common.kether.sub.KetherAPI
import org.bukkit.event.player.AsyncPlayerChatEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent

/**
 * 作者: 老廖
 * 时间: 2022/10/3
 *
 **/
object EventScheduler {

    @SubscribeEvent(priority = EventPriority.LOW, ignoreCancelled = true )
    fun a(e: NewPlayerJoinEvent) {
        Event.get(e.eventName)?.let {
            if (KetherAPI.instantKether(e.player, it.condition).any as Boolean) {
                e.data.newPlayer = false
                Event.runAction(e.player, it.action)
            }
        }
    }
    @SubscribeEvent(priority = EventPriority.LOW, ignoreCancelled = true )
    fun b(e: AsyncPlayerChatEvent) {
        Event.get(e.eventName)?.let {
            if (KetherAPI.instantKether(e.player, it.condition).any as Boolean) {
                Event.runAction(e.player, it.action)
            }
        }
    }
    @SubscribeEvent(priority = EventPriority.LOW, ignoreCancelled = true )
    fun b(e: MailBindEvent) {
        Event.get(e.eventName)?.let {
            if (KetherAPI.instantKether(e.player, it.condition).any as Boolean) {
                Event.runAction(e.player, it.action)
            }
        }
    }
}