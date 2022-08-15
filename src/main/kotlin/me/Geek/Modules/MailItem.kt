package me.Geek.Modules

import com.google.common.base.Joiner
import me.Geek.Configuration.ConfigManager
import me.Geek.GeekMail
import me.Geek.GeekMail.instance
import me.Geek.Libs.DataBase.DataManage
import me.Geek.api.mail.MailSub
import me.Geek.api.mail.MailType
import org.bukkit.Bukkit
import org.bukkit.inventory.ItemStack
import taboolib.platform.util.giveItem
import java.util.*
import kotlin.collections.ArrayList

/**
 * 作者: 老廖
 * 时间: 2022/8/6
 */
class MailItem : MailSub {
    private var mailID: UUID
    private val type = MailType.ITEM_MAIL
    private var title: String
    private val sender: UUID
    private var Target: UUID
    private var text: String
    private val item: Array<ItemStack>
    private var state: String
    private val name: MutableList<String> = ArrayList()

    // 点券邮件邮件
    constructor(MailID: UUID, sender: UUID, target: UUID, Title: String, Text: String, vararg itemStacks: ItemStack) {
        mailID = MailID
        state = "未提取"
        this.sender = sender
        Target = target
        title = Title
        text = Text
        item = itemStacks as Array<ItemStack>
    }

    // 点券邮件 状态已修改
    constructor(MailID: UUID, state: String, sender: UUID, target: UUID, Title: String, Text: String, vararg itemStacks: ItemStack) {
        mailID = MailID
        this.state = state
        this.sender = sender
        Target = target
        title = Title
        text = Text
        item = itemStacks as Array<ItemStack>
    }

    override fun getMailID(): UUID {
        return mailID
    }

    override fun getMailType(): MailType {
        return type
    }

    override fun getTitle(): String {
        return title
    }

    override fun getSender(): UUID {
        return sender
    }

    override fun getTarget(): UUID {
        return Target
    }

    override fun getText(): String {
        return text
    }

    override fun getState(): String {
        return state
    }

    override fun setState(state: String) {
        this.state = state
    }


    override fun giveAppendix() {
        val p = Bukkit.getPlayer(this.target)
        if (p != null) {
            val item = this.itemStacks.asList()
            p.giveItem(item)
        }
    }

    override fun getAppendix(): String {
        name.clear()
        var index = 0
        for (itemStack in item) {
            val meta = itemStack.itemMeta
            if (meta != null) {
                if (meta.hasDisplayName()) {
                    name.add(meta.displayName + " §7* §f" + itemStack.amount)
                } else {
                    val manes = GeekMail.lang.translate(itemStack.type)
                    if (!manes.equals("null")) {
                        name.add(GeekMail.lang.translate(itemStack.type) + " §7* §f" + itemStack.amount)
                    } else {
                        index++
                    }
                }
            }
        }
        if (index > 0) {
            name.add("§7剩余 §6$index §7项未显示...")
        }
        return Joiner.on(", §f").join(name)
    }

    override fun getType(): String {
        return ConfigManager.ITEM_MAIL
    }

    override fun getItemStacks(): Array<ItemStack> {
        return item
    }

    override fun SendMail() {
        Bukkit.getScheduler().scheduleAsyncDelayedTask(instance) {
            if (sender == ConfigManager.Console) {
                val target = Bukkit.getPlayer(Target)
                DataManage.insert(this, *item)
                if (target != null) {
                    MailManage.addTargetCache(getTarget(), this)
                }
                MailManage.SendMailMessage(this.title, this.text, null, target)
            } else {
                super.SendMail()
            }
        }
    }
}