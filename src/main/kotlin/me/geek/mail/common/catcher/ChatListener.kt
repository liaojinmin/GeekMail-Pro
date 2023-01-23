package me.geek.mail.common.catcher

import me.geek.mail.GeekMail
import org.bukkit.entity.Player
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.SubscribeEvent

/**
 * 作者: 老廖
 * 时间: 2022/12/21
 *
 **/
object ChatListener {

    val SessionCache: MutableMap<Player, ChatCatCher> = mutableMapOf()

    @SubscribeEvent
    fun e(e: PlayerQuitEvent) {
        if (SessionCache.containsKey(e.player)) SessionCache.remove(e.player)
    }
    @SubscribeEvent
    fun onChat(e: AsyncPlayerChatEvent) {
        GeekMail.debug("AsyncPlayerChatEvent")
        val chat = SessionCache[e.player] ?: return
        GeekMail.debug("chat != null")
        if (chat.timeOut > System.currentTimeMillis()) {
            GeekMail.debug("run")
            e.isCancelled = true
            if (!e.message.contains(chat.cancel)) {
                chat.action(e.message)
            } else {
                // 唤起注销回调
                chat.remove()
            }
            SessionCache.remove(e.player)
        } else {
            // 唤起注销回调
            chat.remove()
            SessionCache.remove(e.player)
        }

    }


}