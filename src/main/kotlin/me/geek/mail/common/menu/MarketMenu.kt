package me.geek.mail.common.menu

import me.geek.mail.GeekMail
import me.geek.mail.api.mail.MailManage
import me.geek.mail.api.mail.MailManage.sound
import me.geek.mail.common.menu.sub.IconType
import me.geek.mail.common.menu.sub.Session
import me.geek.mail.common.template.Template
import me.geek.mail.common.market.Market
import me.geek.mail.modules.settings.SetTings
import me.geek.mail.utils.serializeItemStacks
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.adaptPlayer
import taboolib.library.reflex.Reflex.Companion.invokeConstructor
import taboolib.module.nms.i18n.I18n
import java.util.*

/**
 * 作者: 老廖
 * 时间: 2022/10/24
 *
 **/
class MarketMenu(
    private val player: Player,
    private val tag: Session,
    private val inv: Inventory
) {
    // 当前所在页面
    private var page = 0
    private val contents: MutableList<Array<ItemStack>> = ArrayList()
    // 邮件索引缓存， key = 邮件所在槽位， value = 邮件唯一标识
    private val cache: MutableMap<String, String> = HashMap()
    private val itemPack = Market.getMarketListCache()

    init { action() }

    private fun action() {
        Menu.isOpen.add(player)
        GeekMail.debug("为玩家: ${player.uniqueId} 打开市场UI")
        build()
        if (contents.size != 0) {
            inv.contents = contents[0]
        }
        player.sound("BLOCK_NOTE_BLOCK_HARP",1f, 1f)
        player.openInventory(inv)
        Bukkit.getPluginManager().registerEvents(object : Listener {
            var cd: Long = 0
            @EventHandler
            fun onClick(e: InventoryClickEvent) {
                if (e.view.title != tag.title || e.view.player != player) return
                if (cd < System.currentTimeMillis()) {
                    cd = System.currentTimeMillis() + 300
                    if (e.rawSlot < 0) return
                    e.isCancelled = true
                    if (e.rawSlot < tag.stringLayout.length) {
                        val id = tag.stringLayout[e.rawSlot].toString()
                        for (micon in tag.micon) {
                            if (micon.icon == id) {
                                when (micon.type) {
                                    IconType.NEXT_PAGE -> {
                                        if (contents.size > page + 1) {
                                            page += 1
                                            inv.contents = contents[page]
                                            player.sound("BLOCK_SCAFFOLDING_BREAK",1f, 1f)
                                        } else {
                                            player.sound("BLOCK_NOTE_BLOCK_DIDGERIDOO",1f, 1f)
                                        }
                                        return
                                    }
                                    IconType.LAST_PAGE -> {
                                        if (page != 0) {
                                            page -= 1
                                            inv.contents = contents[page]
                                            player.sound("BLOCK_SCAFFOLDING_BREAK",1f, 1f)
                                        } else {
                                            player.sound("BLOCK_NOTE_BLOCK_DIDGERIDOO",1f, 1f)
                                        }
                                        return
                                    }
                                    IconType.MARKET_ITEM -> {
                                        if (e.isLeftClick) {
                                            if (e.currentItem != null) {
                                                buyItem(e.rawSlot, inv.contents[e.rawSlot])
                                            }
                                        }
                                    }
                                    else -> {}
                                }
                            }
                        }
                    }
                } else {
                    e.isCancelled = true
                }
            }

            @EventHandler
            fun onDrag(e: InventoryDragEvent) {
                if (player === e.whoClicked) {
                    e.isCancelled = true
                }
            }

            @EventHandler
            fun onClose(e: InventoryCloseEvent) {
                GeekMail.debug("关闭界面")
                player.updateInventory()
                if (player == e.player) {
                    HandlerList.unregisterAll(this)
                    Menu.isOpen.removeIf { it === player }
                }
            }
        }, GeekMail.instance)
    }

    private fun buyItem(index: Int, item: ItemStack) {
        if (itemPack.isNotEmpty()) {
            for (i2 in itemPack) {
                if (value(index, i2.packUid) == cache[key(index, page)]) {
                    val i = item.clone()
                    player.closeInventory()
                    this@MarketMenu.cache.clear()
                    this@MarketMenu.inv.clear()
                    this@MarketMenu.contents.clear()
                    MarketBuy(player, i, i2.packUid)
                }
            }
        }
    }

    private fun build() {
        var itemSize = itemPack.size
        var item = inv.contents
        // 如果商品为空 截停
        if (itemPack.isEmpty()) return
        // 循环梯减物品，直至全部分配完
        while (itemSize > 0) {
            for ((index, value) in tag.stringLayout.withIndex()) {
             //   GeekMail.debug("build().index: $index value: $value")
                if (value != ' ') {
                    tag.micon.forEach { micon ->
                        if (micon.icon[0] == value && micon.type == IconType.MARKET_ITEM) {
                            if (itemSize > 0) {
                               // GeekMail.debug("itemPack.size: ${itemPack.size} 递减: ${itemPack.size - itemSize}")

                                val packIndex = itemPack.size - itemSize
                                cache[key(index, contents.size)] = value(index, itemPack[packIndex].packUid)

                                val itemStack = itemPack[packIndex].item.clone()

                                val locLore = itemPack[packIndex].parseItemInfo(" ", micon.lore)
                                val itemMeta = itemStack.itemMeta
                                if (itemMeta != null) {
                                    if (itemMeta.hasDisplayName()) {
                                        itemMeta.setDisplayName(micon.name.replace("[item_name]",itemMeta.displayName))
                                    } else itemMeta.setDisplayName(micon.name.replace("[item_name]", I18n.instance.getName(itemStack)))
                                    if (itemMeta.hasLore()) {
                                        locLore.addAll(0, itemMeta.lore!!)
                                        itemMeta.lore = locLore
                                    } else {
                                        itemMeta.lore = locLore
                                    }
                                }
                                itemStack.itemMeta = itemMeta
                                item[index] = itemStack
                                itemSize--
                            }
                        }
                    }
                }
            }
            contents.add(item)
            item = inv.contents
        }
    }

    /**
     *
     * @param index 图标索引位
     * @param Page 当前页面
     * @return 返回拼接字符串
     */
    private fun key(index: Int, Page: Int): String {
        return "$index$Page"
    }

    /**
     *
     * @param index 图标索引位
     * @param item_id 唯一ID
     * @return 返回拼接字符串
     */
    private fun value(index: Int, item_id: UUID): String {
        return "$index$item_id"
    }
}