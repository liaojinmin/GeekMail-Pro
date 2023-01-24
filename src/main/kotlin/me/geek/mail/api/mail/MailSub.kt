package me.geek.mail.api.mail

import com.google.gson.annotations.Expose
import me.geek.mail.GeekMail
import me.geek.mail.api.data.SqlManage.RedisScheduler
import me.geek.mail.api.data.SqlManage.addOffMail
import me.geek.mail.api.data.SqlManage.getData
import me.geek.mail.api.event.MailReceiveEvent
import me.geek.mail.api.event.MailSenderEvent
import me.geek.mail.api.hook.HookPlugin
import me.geek.mail.common.menu.sub.Icon
import me.geek.mail.common.settings.SetTings
import me.geek.mail.scheduler.redis.RedisMessageType
import me.geek.mail.utils.colorify
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BundleMeta
import org.jetbrains.annotations.NotNull
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.submitAsync
import taboolib.module.lang.sendLang
import taboolib.module.nms.getI18nName
import java.util.*
import java.util.regex.Pattern

/**
 * 作者: 老廖
 * 时间: 2023/1/16
 *
 **/
abstract class MailSub : MailPlaceholder() {

    override val name: String = javaClass.simpleName.uppercase(Locale.ROOT)
    /*
    标注 # 则为需要实现类重写的常量
     */
    override val mailID: UUID = UUID.randomUUID()

    //override val mailType: String = ""
    //override val mailIcon: String = ""

    override var title: String = "未知邮件标题"
    override var text: String = "未知邮件内容"
    override var state: MailState = MailState.NotObtained
    override var senderTime: Long = System.currentTimeMillis()
    override var getTime: Long = 0L

    @Expose
    override var appendixInfo: String = ""

    //override val permission: String = "mail.global"

    override var additional: String = "0"

    @Expose
    override var itemStacks: Array<ItemStack>? = emptyArray()

    // 物品序列化用
    override var itemStackString: String = ""

    override var command: List<String>? = emptyList()


    fun getIcon(icon: Icon): ItemStack {
        return try {
            val item: ItemStack =
                if (this.mailIcon.contains("IA:", ignoreCase = true) && HookPlugin.itemsAdder.isHook) {
                    HookPlugin.itemsAdder.getItem(this.mailIcon.substring(3))
                } else ItemStack(Material.valueOf(this.mailIcon))
            val itemMeta = if (SetTings.USE_BUNDLE) {
                item.type = Material.BUNDLE
                (item.itemMeta as BundleMeta).also {
                    it.setItems(this.itemStacks?.asList())
                }
            } else item.itemMeta
            if (itemMeta != null) {
                itemMeta.setDisplayName(icon.name.replace("[title]", this.title).colorify())
                itemMeta.lore = this.parseMailInfo(icon.lore)
                if (this.state == MailState.NotObtained) {
                    itemMeta.addEnchant(Enchantment.DAMAGE_ALL, 1, true)
                    itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
                    itemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS)
                }
                item.itemMeta = itemMeta
            }
            item
        } catch (ing: IllegalArgumentException) {
            ItemStack(Material.BOOK, 1)
            error("错误的邮件图标配置")
        }
    }

    override fun sendMail() {
        this.runAppendixInfo()
        val send = Bukkit.getPlayer(this.sender)
        // 玩家数据异常，锁定
        send?.let {
            if (MailManage.PlayerLock.contains(it.uniqueId)) {
                adaptPlayer(it).sendLang("PLAYER-LOCK")
                return
            }
        }

        // 发送事件 start
        val event = MailSenderEvent(this)
        event.call()
        if (event.isCancelled) return
        // 发送事件 end

        // 处理发送逻辑 start
        submitAsync {
            val targets = Bukkit.getPlayer(this@MailSub.target)
            var targetName = "目标"
            if (targets != null) {
                targetName = targets.name

                targets.getData().mailData.add(this@MailSub) // + 缓存

                val info = if (text.length >= 11) text.substring(0, 10) else text
                adaptPlayer(targets).sendLang("玩家-接收邮件", title, "$info §8...")
            } else {
                // 未处理 Redis 跨服问题
                sendCrossMail()
            }
            if (this@MailSub.sender != SetTings.Console) {
                if (send != null) {
                    adaptPlayer(send).sendLang("玩家-发送邮件", targetName)
                }
            }
            MailManage.senderWebMail(title, text, appendixInfo, target)
        }
        // 处理发送逻辑 end

        // 唤起送达事件
        MailReceiveEvent(this).call() // StarrySky
    }


    override fun sendCrossMail() {
        RedisScheduler?.let {
            it.sendCrossMailPublish(
                Bukkit.getPort().toString(),
                RedisMessageType.CROSS_SERVER_MAIL,
                this.target.toString(),
                this.mailID.toString()
            )
            it.setMailData(this)
            GeekMail.say("&e未解决集群在线玩家收集问题,如果玩家不在任何集群，该邮件将失效。。。")
        } ?: addOffMail(this)
    }

    override fun sendGlobalMail() {
        addOffMail(*MailManage.buildGlobalMail(this))
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
            if ((bs - 6) > 0) {
                Str.append("§7剩余 §6${index + (bs - 6)} §7项未显示...")
            }
        }
        return Str.toString()
    }
}