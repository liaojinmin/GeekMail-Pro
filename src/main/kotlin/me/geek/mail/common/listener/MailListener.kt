package me.geek.mail.common.listener



import me.geek.mail.GeekMail.dataScheduler
import me.geek.mail.api.mail.MailManage
import me.geek.mail.api.mail.event.NewPlayerJoinEvent
import me.geek.mail.common.customevent.Event
import me.geek.mail.common.data.SqlManage
import me.geek.mail.common.data.MailPlayerData
import me.geek.mail.common.kether.sub.KetherAPI
import me.geek.mail.common.menu.MAction
import me.geek.mail.common.menu.Menu
import me.geek.mail.common.menu.Menu.openMenu
import me.geek.mail.modules.settings.SetTings
import me.geek.mail.scheduler.redis.classUnSerializable
import me.geek.mail.scheduler.redis.toByteArrays
import me.geek.mail.scheduler.redis.toHexString
import org.bukkit.Bukkit
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
    @SubscribeEvent(priority = EventPriority.LOW, ignoreCancelled = true )
    fun onJoin(e: PlayerJoinEvent) {
        submitAsync {
            val player = e.player
            MailManage.PlayerLock.add(player.uniqueId)
            Thread.sleep(400)
            var data: MailPlayerData? = null

            // 处理数据
            SqlManage.getMessage(player.uniqueId.toString())?.let {
                val a = it.toByteArrays().classUnSerializable()
                if (a is MailPlayerData) {
                    data = a
                }
            } ?: SqlManage.selectPlayerData(player.uniqueId, player.name)?.let { data = it }

            // + 缓存
            data?.let {
                MailManage.addMailPlayerData(player.uniqueId, it)
                var amt = 0
                it.mailData.forEach { mail ->
                    if (mail.state == "未提取") amt++
                }.also {
                    if (amt != 0) {
                        adaptPlayer(player).sendLang("玩家-加入游戏-提醒", amt)
                    }
                }
                MailManage.PlayerLock.remove(player.uniqueId)
                if (it.OneJoin) {
                    NewPlayerJoinEvent(player, it).call()
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
        submitAsync {
            val uuid = e.player.uniqueId
            MailManage.getMailPlayerData(uuid)?.let {
                dataScheduler?.let { scheduler ->
                    val str = scheduler.toBytes(it).toHexString()
                    scheduler.sendPublish(Bukkit.getPort().toString(), uuid.toString(), str)
                }
                MailManage.remMailPlayerData(uuid)
            }
        }
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
                        Menu.getMenuCommand(Menu.cmd!!)?.let {
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
                if (MailManage.PlayerLock.contains(e.player.uniqueId)) {
                    adaptPlayer(e.player).sendLang("PLAYER-LOCK")
                    return
                }
                e.player.openMenu(it)
            }
        }
    }

}