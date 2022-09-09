package me.geek.mail.api.mail

import me.geek.mail.Configuration.ConfigManager
import me.geek.mail.GeekMail
import me.geek.mail.api.mail.event.MailSenderEvent
import me.geek.mail.common.DataBase.DataManage
import org.bukkit.Bukkit
import java.util.*

/**
 * 作者: 老廖
 * 时间: 2022/8/2
 */
abstract class MailSub : Mail {
    val name: String = javaClass.simpleName.uppercase(Locale.ROOT)


    override fun sendMail() {

        val event = MailSenderEvent(this)
        event.call()
        if (event.isCancelled) return

        Bukkit.getScheduler().scheduleAsyncDelayedTask(GeekMail.instance) {
            val send = Bukkit.getPlayer(sender)
            val targets = Bukkit.getPlayer(target)
            if (sender == ConfigManager.Console) {
                if (targets != null) {
                    MailManage.addTargetCache(target, this)
                }
            } else {
                if (targets != null) {
                    MailManage.addTargetCache(target, this)
                }
                if (send != null) {
                    MailManage.addSenderCache(sender, this)
                }
            }
            MailManage.WebMail.onSender(title, text, appendixInfo,targets?.name ?: Bukkit.getOfflinePlayer(target).name ?: Bukkit.getOfflinePlayer(target).uniqueId.toString())
            MailManage.sendMailMessage(title, text, send, targets)
            DataManage.insert(this)
        }
    }

    fun sendGlobalMail() {
        val player = Bukkit.getOfflinePlayers()
        DataManage.insert(this, player)
    }
}