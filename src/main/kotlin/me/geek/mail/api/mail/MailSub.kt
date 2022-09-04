package me.geek.mail.api.mail

import me.geek.mail.Configuration.ConfigManager
import me.geek.mail.GeekMail
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
        Bukkit.getScheduler().scheduleAsyncDelayedTask(GeekMail.instance) {
            if (sender == ConfigManager.Console) {
                val targets = Bukkit.getPlayer(target)
                DataManage.insert(this, null)
                if (targets != null) {
                    MailManage.addTargetCache(target, this)
                }
                MailManage.sendMailMessage(this.title, this.text, null, targets)
            } else {
                val send = Bukkit.getPlayer(sender)
                val targets = Bukkit.getPlayer(target)
                DataManage.insert(this, itemStacks)
                if (targets != null) {
                    // 如果目标玩家在线则载入缓存
                    MailManage.addTargetCache(target, this)
                }
                if (send != null) {
                    // 如果发送者在线则载入缓存
                    MailManage.addSenderCache(sender, this)
                }
                MailManage.sendMailMessage(title, text, send, targets)
            }
        }
    }

    fun SendGlobalMail() {
        val player = Bukkit.getOnlinePlayers()
        //DataManage.insert(this, player, itemStacks)
    }
}