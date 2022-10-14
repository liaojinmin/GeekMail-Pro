package me.geek.mail.common.listener



import me.geek.mail.GeekMail
import me.geek.mail.api.mail.MailManage
import me.geek.mail.api.mail.event.NewPlayerJoinEvent
import me.geek.mail.common.customevent.Event
import me.geek.mail.common.data.sub.MailPlayerData.Companion.update
import me.geek.mail.common.kether.sub.KetherAPI
import me.geek.mail.common.menu.MAction
import me.geek.mail.common.menu.Menu
import me.geek.mail.common.menu.Menu.openMenu
import me.geek.mail.modules.settings.SetTings
import org.bukkit.event.player.PlayerCommandPreprocessEvent
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
object MailListener {
    private val database = GeekMail.DataManage
    @SubscribeEvent(priority = EventPriority.LOW, ignoreCancelled = true )
    fun onJoin(e: PlayerJoinEvent) {
        submitAsync {
            val player = e.player

            database.selectPlayerMail(player.uniqueId).let {
                MailManage.upTargetCache(player.uniqueId, it)
                var amt = 0
                it.forEach { mail ->
                    if (mail.state == "未提取") amt++
                }
                if (amt != 0) {
                    adaptPlayer(player).sendLang("玩家-加入游戏-提醒", amt)
                }
            }
            database.selectPlayerData(player.uniqueId, player.name)?.let { data ->
                if (data.OneJoin) {
                    NewPlayerJoinEvent(player, data).call()
                }
           }

            // 处理自定义事件动作
            Event.get().forEach { (_, pack) ->
                if (pack.event == e.eventName) {
                    if (KetherAPI.instantKether(e.player, pack.condition).any as Boolean) {
                        Event.runAction(e.player, pack.action)
                    }
                }
            }
        }

    }

    @SubscribeEvent(priority = EventPriority.LOW, ignoreCancelled = true )
    fun onQuit(e: PlayerQuitEvent) {
        val player = e.player.uniqueId
        MailManage.remTargetCache(player)
        GeekMail.DataManage.remMailPlayerData(player)
    }


    @SubscribeEvent(priority = EventPriority.LOW, ignoreCancelled = true )
    fun onInteract(e: PlayerInteractEvent) {
        SetTings.location?.let {
            if (e.isLeftClickBlock()) {
                e.clickedBlock?.location?.let { v ->
                    if (it == v) {
                        e.isCancelled = true
                        Menu.getMenuCommand(Menu.cmd!!)?.let {
                            val player = e.player
                            MAction(player, Menu.getSession(it), Menu.Build(player, it))
                        }
                    }
                }
            }
        }
    }
    @SubscribeEvent(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onCommand(e: PlayerCommandPreprocessEvent) {
        val message = e.message.removePrefix("/")
        if (message.isNotBlank()) {
            Menu.getMenuCommand(message)?.let {
                e.isCancelled = true
                e.player.openMenu(it)
            }
        }
    }

}