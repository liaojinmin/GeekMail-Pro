package me.geek.mail.common.menu.action

import me.geek.mail.GeekMail
import me.geek.mail.common.menu.MenuBase
import me.geek.mail.common.settings.SetTings
import me.geek.mail.modules.Mail_Item
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.lang.sendLang

/**
 * 作者: 老廖
 * 时间: 2023/1/23
 *
 **/
class ItemMail(
    private val isCross: Boolean = false,
    override val player: Player,
    private val mail: Mail_Item
): MenuBase() {
        override fun build(): MenuBase {
            this.title = "§0放入物品 §7| §0关闭菜单"
            this.size = 27
            this.sound("BLOCK_NOTE_BLOCK_HARP", 1f, 1f)
            this.openMenu()
            return this
        }
        override fun onClick(event: InventoryClickEvent) {
        }
        override fun onClose(event: InventoryCloseEvent) {
            if (GeekMail.plugin_status) {
                val i1 = mutableListOf<ItemStack>().apply {
                    for (i2 in event.inventory.contents) {
                        if (i2 != null) {
                            this.add(i2)
                        }
                    }
                    if (!player.isOp && SetTings.filter.use) player.itemFilter(this)
                }
                if (i1.size > 0) {
                    mail.itemStacks = i1.toTypedArray()
                    mail.appendixInfo = mail.getItemInfo(StringBuilder(""))
                    if (!isCross) {
                        mail.sendMail()
                    } else mail.sendCrossMail()
                    return
                }
            } else {
                for (item in event.inventory) {
                    player.inventory.addItem(item)
                }
            }
        }
    private fun Player.itemFilter(itemStacks: MutableList<ItemStack>) {
        val outItem = mutableListOf<ItemStack>()
        itemStacks.forEach { stack ->
            stack.itemMeta?.let { meta ->
                var isOut = false
                SetTings.filter.contains_name.forEach {
                    if (meta.hasDisplayName()) {
                        if (meta.displayName.contains(it)) {
                            isOut = true
                        }
                    }
                }
                SetTings.filter.contains_lore.forEach {
                    meta.lore?.let { a ->
                        if (a.contains(it)) {
                            isOut = true
                        }
                    }
                }
                if (isOut) outItem.add(stack) //修复错误的返回双倍物品
            }
        }

        if (outItem.size > 0) {
            if (SetTings.filter.type == "黑名单") {
                itemStacks.removeAll(outItem)
                for (a in outItem) {
                    this.inventory.addItem(a)
                }
                adaptPlayer(this).sendLang("玩家-发送物品邮件-物品筛选", outItem.size)
            } else {
                itemStacks.retainAll(outItem)
                for (a in itemStacks) {
                    this.inventory.addItem(a)
                }
                adaptPlayer(this).sendLang("玩家-发送物品邮件-物品筛选", itemStacks.size)
            }
        }
    }


}