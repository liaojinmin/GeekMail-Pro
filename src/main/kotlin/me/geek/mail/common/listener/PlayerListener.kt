package me.geek.mail.common.listener



import me.geek.mail.GeekMail
import me.geek.mail.api.data.SqlManage.getData
import me.geek.mail.api.data.SqlManage.saveData
import me.geek.mail.api.event.NewPlayerJoinEvent
import me.geek.mail.api.mail.MailManage
import me.geek.mail.api.mail.MailState
import me.geek.mail.common.customevent.Event
import me.geek.mail.common.kether.sub.KetherAPI
import me.geek.mail.common.menu.Menu
import me.geek.mail.common.menu.Menu.openMenu
import me.geek.mail.common.settings.SetTings
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.submitAsync
import taboolib.module.lang.sendLang
import taboolib.platform.util.isLeftClickBlock

/**
 * 作者: 老廖
 * 时间: 2022/7/29
 *
 **/
object PlayerListener {
    @SubscribeEvent(priority = EventPriority.LOW, ignoreCancelled = true )
    fun onJoin(e: PlayerJoinEvent) {
        submitAsync {
            val player = e.player
            MailManage.PlayerLock.add(player.uniqueId)
            val data = player.getData()
            GeekMail.debug("添加玩家数据..." )
            if (data.player == player) {
                MailManage.PlayerLock.remove(player.uniqueId)
                var amt = 0
                data.mailData.forEach { mail ->
                    if (mail.state == MailState.NotObtained) amt++
                }.also { if (amt != 0) adaptPlayer(player).sendLang("玩家-加入游戏-提醒", amt) }

                if (data.newPlayer) NewPlayerJoinEvent(player, data).call()
            }
            // 处理自定义事件动作
            Event.get(e.eventName)?.let {
                if (KetherAPI.instantKether(e.player, it.condition).any as Boolean) {
                    Event.runAction(e.player, it.action)
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW, ignoreCancelled = true )
    fun onQuit(e: PlayerQuitEvent) {
        e.player.saveData(true)
    }



    @SubscribeEvent(priority = EventPriority.LOW, ignoreCancelled = true )
    fun onInteract(e: PlayerInteractEvent) {
        SetTings.location?.let {
            if (e.isLeftClickBlock()) {
                e.clickedBlock?.location?.let { v ->
                    if (it == v) {
                        e.isCancelled = true
                        val player = e.player
                        if (MailManage.PlayerLock.contains(player.uniqueId)) {
                            adaptPlayer(player).sendLang("PLAYER-LOCK")
                            return
                        }
                        e.player.openMenu(Menu.mainMenu)
                    }
                }
            }
        }
    }

}