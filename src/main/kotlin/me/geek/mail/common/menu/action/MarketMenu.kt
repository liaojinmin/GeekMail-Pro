package me.geek.mail.common.menu.action

import me.geek.mail.common.market.Item
import me.geek.mail.common.market.Market
import me.geek.mail.common.menu.Menu
import me.geek.mail.common.menu.MenuBase
import me.geek.mail.common.menu.sub.IconType
import me.geek.mail.common.menu.sub.MenuData
import me.geek.mail.common.menu.sub.MenuType
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import taboolib.module.nms.i18n.I18n

/**
 * 作者: 老廖
 * 时间: 2023/1/23
 *
 **/
class MarketMenu(
    override val player: Player,
    override val menuData: MenuData
): MenuBase() {
    private val ioc: MutableMap<Int, Item> = mutableMapOf()

    private val itemPack = Market.getMarketListCache()


    override fun build(): MenuBase {
        var item = this.inventory.contents
        // 如果商品为空 截停
        if (itemPack.isNotEmpty()) {
            var itemSize = itemPack.size
            while (itemSize > 0) {
                menuData.layout.forEachIndexed { index, value ->
                    if(value != ' ') {
                        menuData.icon[value]?.let { icon ->
                            if (icon.iconType == IconType.MARKET_ITEM) {
                                if (itemSize > 0) {
                                    val packIndex = itemPack.size - itemSize
                                    ioc[index] = itemPack[packIndex]
                                    val itemStack = itemPack[packIndex].item.clone()
                                    val locLore = itemPack[packIndex].parseItemInfo(" ", icon.lore)
                                    val itemMeta = itemStack.itemMeta
                                    if (itemMeta != null) {
                                        if (itemMeta.hasDisplayName()) {
                                            itemMeta.setDisplayName(icon.name.replace("[item_name]",itemMeta.displayName))
                                        } else itemMeta.setDisplayName(icon.name.replace("[item_name]", I18n.instance.getName(itemStack)))
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
                this.contents.add(item)
                item = this.inventory.contents
            }
        }
        sound("BLOCK_NOTE_BLOCK_HARP",1f, 1f)
        this.openMenu()
        return this

    }
    private var cd: Long = 0
    override fun onClick(event: InventoryClickEvent) {
        event.isCancelled = true
        if (cd < System.currentTimeMillis()) cd = System.currentTimeMillis() + 200 else return
        menuData.layout[event.rawSlot].let { char ->
            menuData.icon[char]?.let { icon ->
                when (icon.iconType) {
                    IconType.LAST_PAGE -> {
                        if (this.page != 0) {
                            sound("BLOCK_SCAFFOLDING_BREAK",1f, 1f)
                            // 上跳页面
                            this.page -= 1
                            this.inventory.contents = this.contents[page]
                        } else sound("BLOCK_NOTE_BLOCK_DIDGERIDOO",1f, 1f)
                        return
                    }
                    IconType.NEXT_PAGE -> {
                        if (this.contents.size > this.page + 1 ) {
                            sound("BLOCK_SCAFFOLDING_BREAK",1f, 1f)
                            // 下跳页面
                            this.page += 1
                            this.inventory.contents = this.contents[page]
                        } else sound("BLOCK_NOTE_BLOCK_DIDGERIDOO",1f, 1f)
                        return
                    }

                    IconType.MARKET_ITEM -> {
                        val item = ioc[event.rawSlot] ?: error("商品索引错误")
                        val data = Menu.getMenuData(MenuType.MARKETBUY)
                        player.closeInventory()
                        MarketBuyMenu(player, data, this.menuData, item.item, item.packUid).build()
                    }
                    else -> return

                }
            }

        }

    }

    override fun onClose(event: InventoryCloseEvent) {
    }
}