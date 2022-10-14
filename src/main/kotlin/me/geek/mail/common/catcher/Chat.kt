package me.geek.mail.common.catcher

import me.geek.mail.GeekMail
import me.geek.mail.GeekMail.DataManage
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import taboolib.common.platform.function.submitAsync
import taboolib.platform.util.sendLang

/**
 * 作者: 老廖
 * 时间: 2022/9/11
 *
 **/
class Chat(
    private val player: Player
) {
    val reg = Regex("""[a-zA-Z0-9]+([-_.][A-Za-zd]+)*@([a-zA-Z0-9]+[-.])+[A-Za-zd]{2,5}""")
    fun start() {
        submitAsync {
            val eve = object : Listener {
                @EventHandler
                fun onChat(e: AsyncPlayerChatEvent) {
                    if (e.player == player) {
                        e.isCancelled = true
                        if (!e.message.contains("cancel|取消|Cancel".toRegex())) {
                            if (reg.matches(e.message)) {
                                DataManage.getMailPlayerData(e.player.uniqueId)?.let {
                                    it.mail = e.message
                                    DataManage.update(it)
                                }
                                player.sendMessage("§a绑定成功.")
                            } else player.sendMessage("§c错误的邮箱格式.")
                        }
                        HandlerList.unregisterAll(this)
                    }
                }
            }
            player.sendLang("玩家-输入捕获")
            Bukkit.getPluginManager().registerEvents(eve, GeekMail.instance)
            runTask(eve)
        }
    }
    private fun runTask(e: Listener) {
        val end = System.currentTimeMillis() + 20000
        while (System.currentTimeMillis() < end) {
            Thread.sleep(1000)
        }
        HandlerList.unregisterAll(e)
    }
}