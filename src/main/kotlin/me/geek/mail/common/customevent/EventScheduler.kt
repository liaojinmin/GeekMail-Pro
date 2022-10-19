package me.geek.mail.common.customevent


import me.geek.mail.GeekMail
import me.geek.mail.api.mail.event.NewPlayerJoinEvent
import me.geek.mail.common.data.MailPlayerData.Companion.update
import me.geek.mail.common.kether.sub.KetherAPI
import org.bukkit.event.player.AsyncPlayerChatEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submitAsync

/**
 * 作者: 老廖
 * 时间: 2022/10/3
 *
 **/
object EventScheduler {

    @SubscribeEvent(priority = EventPriority.LOW, ignoreCancelled = true )
    fun a(e: NewPlayerJoinEvent) {
        submitAsync {
            GeekMail.debug("NewPlayerJoinEvent")
            Event.get().forEach { (_, pack) ->
                if (pack.event == e.eventName) {
                    if (KetherAPI.instantKether(e.player, pack.condition).any as Boolean) {
                        e.data.OneJoin = false
                        e.data.update()
                        Event.runAction(e.player, pack.action)
                    }
                }
            }
        }
    }
    @SubscribeEvent(priority = EventPriority.LOW, ignoreCancelled = true )
    fun b(e: AsyncPlayerChatEvent) {
        submitAsync {
            GeekMail.debug("AsyncPlayerChatEvent")
            Event.get().forEach { (_, pack) ->
                if (pack.event == e.eventName) {
                    if (KetherAPI.instantKether(e.player, pack.condition).any as Boolean) {
                        Event.runAction(e.player, pack.action)
                    }
                }
            }
        }
    }
}