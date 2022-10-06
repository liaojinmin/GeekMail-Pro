package me.geek.mail.api.mail

import me.geek.mail.GeekMail
import me.geek.mail.api.mail.event.MailReceiveEvent
import me.geek.mail.api.mail.event.MailSenderEvent
import me.geek.mail.modules.settings.SetTings
import org.bukkit.Bukkit
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.lang.sendLang
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

            if (sender == SetTings.Console) {
                if (targets != null) {
                    MailManage.addTargetCache(target, this)
                }
            } else {
                var targetName = "目标"
                if (targets != null) {
                    targetName = targets.name
                    MailManage.addTargetCache(target, this)
                    adaptPlayer(targets).sendLang("玩家-接收邮件", title)
                }
                if (send != null) {
                    MailManage.addSenderCache(sender, this)
                    adaptPlayer(send).sendLang("玩家-发送邮件", targetName)
                }
            }

            GeekMail.DataManage.insertMailData(this)
        }

        MailManage.senderWebMail(title, text, appendixInfo, target)

        MailReceiveEvent(this).call() // StarrySky
    }

    fun sendGlobalMail() {
        val player = Bukkit.getOfflinePlayers()
        GeekMail.DataManage.insert(this, player)
    }
}