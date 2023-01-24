package me.geek.mail.modules

import me.geek.mail.api.mail.MailSub
import me.geek.mail.common.menu.action.ItemMail
import me.geek.mail.common.settings.SetTings
import me.geek.mail.utils.getEmptySlot
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.platform.util.giveItem
import taboolib.platform.util.sendLang
import java.util.*

/**
 * 作者: 老廖
 * 时间: 2022/8/6
 */
class Mail_Item() : MailSub() {

    constructor(
        senders: UUID,
        targets: UUID,
        itemStacks: Array<ItemStack>? = emptyArray()
    ) : this() {
        this.sender = senders
        this.target = targets
        this.itemStacks = itemStacks
     //   appendixInfo = getItemInfo(StringBuilder(""))
    }

    override var sender: UUID = super.sender
    override var target: UUID = super.target

    override val mailType: String = "物品邮件"

    override val mailIcon: String = SetTings.mailIcon.ITEM_MAIL

    override val permission: String = "mail.exp.items"
    override fun runAppendixInfo() {
        this.appendixInfo = getItemInfo(StringBuilder(""))
    }




    override fun sendMail() {
        itemStacks?.let {
            if (it.isNotEmpty()) {
                super.sendMail()
            } else {
                Bukkit.getPlayer(this.sender)?.let {
                    ItemMail(false, it, this).build()
                }
            }
        }
    }

    override fun giveAppendix(): Boolean {
        Bukkit.getPlayer(this.target)?.let {
            this.itemStacks?.let { item ->
                val air = it.getEmptySlot()
                if (air >= item.size) {
                    it.giveItem(item.asList())
                    return true
                } else {
                    it.sendLang("玩家-没有足够背包格子", item.size-air)
                    return false
                }
            }
        }
        return false
    }

    override fun condition(player: Player, appendix: String): Boolean {
        return true
    }

    override fun sendCrossMail() {
        if (itemStacks == null) {
            Bukkit.getPlayer(this.sender)?.let {
                ItemMail(true, it, this).build()
            }
        } else {
            super.sendCrossMail()
        }
    }
}