package me.geek.mail.api.mail

import me.geek.mail.api.mail.event.MailReceiveEvent
import me.geek.mail.api.mail.event.MailSenderEvent
import me.geek.mail.common.data.SqlManage
import me.geek.mail.modules.settings.SetTings
import org.bukkit.Bukkit
import org.jetbrains.annotations.NotNull
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.submitAsync
import taboolib.expansion.geek.Expiry
import taboolib.module.lang.sendLang
import taboolib.module.nms.getI18nName
import java.util.*
import java.util.regex.Pattern

/**
 * 作者: 老廖
 * 时间: 2022/8/2
 */
abstract class MailSub(
) : MailPlaceholder() {
    // 序列化用
    override var itemStackString: String = ""


    val name: String = javaClass.simpleName.uppercase(Locale.ROOT)


    override fun sendMail() {
        val send = Bukkit.getPlayer(this.sender)
        send?.let {
            if (MailManage.PlayerLock.contains(it.uniqueId)) {
                adaptPlayer(it).sendLang("PLAYER-LOCK")
                return
            }
        }

        val event = MailSenderEvent(this)
        event.call()
        if (event.isCancelled) return

        submitAsync {
            val targets = Bukkit.getPlayer(this@MailSub.target)
            var targetName = "目标"
            if (targets != null) {
                targetName = targets.name
                MailManage.addPlayerMailCache(this@MailSub.target, this@MailSub)
                adaptPlayer(targets).sendLang("玩家-接收邮件", title)
            }
            if (this@MailSub.sender != SetTings.Console) {
                if (send != null) {
                    adaptPlayer(send).sendLang("玩家-发送邮件", targetName)
                }
            }
            SqlManage.insertMail(this@MailSub)
        }

        MailManage.senderWebMail(this.title, this.text, this.appendixInfo, this.target)
        MailReceiveEvent(this).call() // StarrySky
    }

    fun sendGlobalMail() {
        val player = Bukkit.getOfflinePlayers()
        SqlManage.insertGlobalMail(this, player)
    }

    fun parseMailInfo(@NotNull lore: List<String>): List<String> {
        val list = mutableListOf<String>()
        lore.forEach {
            when {
                it.contains(TYPE) -> list.add(it.replace(TYPE, this.mailType))
                it.contains(SENDER) -> list.add(it.replace(SENDER, if (this.sender == SetTings.Console) "系统" else Bukkit.getOfflinePlayer(this.sender).name!!))
                it.contains(SERDER_TIME) -> list.add(it.replace(SERDER_TIME, format.format(this.senderTime.toLong())))
                it.contains(GET_TIME) -> list.add(it.replace(GET_TIME, if (getTime.toLong() < 1000) "未领取" else format.format(this.getTime.toLong())))
                it.contains(TEXT) -> list.addAll(it.replace(TEXT, this.text.replace(";",",")).split(","))
                it.contains(STATE) -> list.add(it.replace(STATE, this.state))
                it.contains(ITEM) -> list.addAll(it.replace(ITEM, this.appendixInfo).split(","))
                it.contains(EXPIRE) -> {
                    if (SetTings.UseExpiry) {
                    list.add(it.replace(EXPIRE, Expiry.getExpiryDate(this.senderTime.toLong() + SetTings.ExpiryTime, false)))
                    } else list.add(it.replace(EXPIRE, ""))
                }
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
        var bs = 0
        this.itemStacks?.let {
            val player = Bukkit.getPlayer(this.target)
            for (Stack in it) {
                val meta = Stack.itemMeta
                if (meta != null && bs < 6) {
                    if (meta.hasDisplayName()) {
                        Str.append(meta.displayName + " §7* §f" + Stack.amount + ", §f")
                    } else {
                        val manes = Stack.getI18nName(player)
                        if (manes != "[ERROR LOCALE]") {
                            Str.append(manes + " §7* §f" + Stack.amount + ", §f")
                        } else {
                            index++
                        }
                    }
                    bs++
                } else bs++
            }
        }
        if (index > 0 || bs >= 6) {
            val vv = bs - 6
            if (vv > 0) {
                Str.append("§7剩余 §6${index + (bs - 6)} §7项未显示...")
            }
        }
        return Str.toString()
    }

}