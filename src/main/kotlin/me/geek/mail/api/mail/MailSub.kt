package me.geek.mail.api.mail

import me.geek.mail.GeekMail
import me.geek.mail.api.mail.event.MailReceiveEvent
import me.geek.mail.api.mail.event.MailSenderEvent
import me.geek.mail.api.mail.sub.MailPlaceholder
import me.geek.mail.modules.settings.SetTings
import org.bukkit.Bukkit
import org.jetbrains.annotations.NotNull
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.lang.sendLang
import taboolib.module.nms.getI18nName
import java.util.*
import java.util.regex.Pattern

/**
 * 作者: 老廖
 * 时间: 2022/8/2
 */
abstract class MailSub : MailPlaceholder() {

    val name: String = javaClass.simpleName.uppercase(Locale.ROOT)


    override fun sendMail() {

        val event = MailSenderEvent(this)
        event.call()
        if (event.isCancelled) return

        Bukkit.getScheduler().scheduleAsyncDelayedTask(GeekMail.instance) {
            val send = Bukkit.getPlayer(sender)
            val targets = Bukkit.getPlayer(target)

            var targetName = "目标"

            if (targets != null) {
                MailManage.addTargetCache(target, this)
                targetName = targets.name
                MailManage.addTargetCache(target, this)
                adaptPlayer(targets).sendLang("玩家-接收邮件", title)
            }

            if (sender != SetTings.Console) {
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

    fun parseMailInfo(@NotNull lore: List<String>): List<String> {
        val list = mutableListOf<String>()
        lore.forEach {
            when {
                it.contains(TYPE) -> list.add(it.replace(TYPE, mailType))
                it.contains(SENDER) -> list.add(it.replace(SENDER, if (sender == SetTings.Console) "系统" else Bukkit.getOfflinePlayer(sender).name!!))
                it.contains(SERDER_TIME) -> list.add(it.replace(SERDER_TIME, format.format(senderTime.toLong())))
                it.contains(GET_TIME) -> list.add(it.replace(GET_TIME, if (getTime.toLong() < 1000) "未领取" else format.format(getTime.toLong())))
                it.contains(TEXT) -> list.add(it.replace(TEXT, text.replace(";",",")))
                it.contains(STATE) -> list.add(it.replace(STATE, state))
                it.contains(ITEM) -> list.addAll(it.replace(ITEM, appendixInfo).split(","))
                else -> list.add(it)
            }
        }
        return list
    }
    fun formatDouble(@NotNull num1: Any): String {
        val matcher = Pattern.compile("\\d+\\.?\\d?\\d").matcher(num1.toString())
        var var1 = "0.0"
        if (matcher.find()) {
            var1 = matcher.group()
        }
        return var1
    }
    fun getItemInfo(@NotNull Str: StringBuilder): String {
        var index = 0
        itemStacks?.let {
            for (Stack in it) {
                val meta = Stack.itemMeta
                if (meta != null) {
                    if (meta.hasDisplayName()) {
                        Str.append(meta.displayName + " §7* §f" + Stack.amount + ", §f")
                    } else {
                        val manes = Stack.getI18nName(Bukkit.getPlayer(target))
                        if (manes != "[ERROR LOCALE]") {
                            Str.append(manes + " §7* §f" + Stack.amount + ", §f")
                        } else {
                            index++
                        }
                    }
                }
            }
        }
        if (index > 0) {
            Str.append("§7剩余 §6$index §7项未显示...")
        }
        return Str.toString()
    }
}