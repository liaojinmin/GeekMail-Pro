package me.geek.mail.common.event.monitor

import me.geek.mail.GeekMail
import me.geek.mail.api.mail.event.NewPlayerJoinEvent
import me.geek.mail.common.event.Event
import me.geek.mail.common.event.sub.EventPack
import me.geek.mail.common.event.sub.EventType
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent

/**
 * 作者: 老廖
 * 时间: 2022/10/3
 *
 **/
object NewPlayerJoin {

    @SubscribeEvent(priority = EventPriority.LOW, ignoreCancelled = true )
    fun onJoin(e: NewPlayerJoinEvent) {
        GeekMail.debug("NewPlayerJoinEvent")
        Event.get().forEach { (_, value) ->
            val c = EventType.valueOf(value.condition)
            if (c == EventType.NewPlayerJoinEvent) {
                Event.runAction(e.player, value.action)
            }
        }
    }
}