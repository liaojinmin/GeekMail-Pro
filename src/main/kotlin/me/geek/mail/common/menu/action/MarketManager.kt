package me.geek.mail.common.menu.action

import me.geek.mail.api.mail.MailBuild
import me.geek.mail.common.market.Item
import me.geek.mail.common.market.Market
import me.geek.mail.common.menu.Menu
import me.geek.mail.common.menu.MenuBasic
import me.geek.mail.common.menu.sub.IconType
import me.geek.mail.common.menu.sub.MenuData
import me.geek.mail.common.menu.sub.MenuType
import me.geek.mail.common.template.Template
import me.geek.mail.settings.SetTings
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import taboolib.module.nms.getName
import taboolib.module.nms.i18n.I18n

class MarketManager(
    override val player: Player,
    override val menuData: MenuData = Menu.getMenuData(MenuType.MARKET_MANAGER)
): MenuBasic() {
    private val ioc: MutableMap<Int, Item> = mutableMapOf()

    private val itemPack = Market.getPlayerAllMarket(player)

    override fun build(): MenuBasic {
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

    override fun onClick(event: InventoryClickEvent) {
        event.isCancelled = true
        menuData.layout[event.rawSlot].let { char ->
            menuData.icon[char]?.let { icon ->
                if (icon.command.isNotEmpty()) {
                    icon.executeCmd(player)
                }
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
                        val i1 = ioc[event.rawSlot] ?: return
                        val i2 = Market.getMarketItem(i1.packUid) ?: return //获取商品
                        Market.remMarketItem(i2.packUid, true)
                        val quit = Template.getAdminPack(SetTings.market.player_quit_sendPack)
                        MailBuild(quit!!.type, null, player.uniqueId).build {
                            title = quit.title
                            text = quit.text.replace("{item-name}", i2.item.getName())
                            setItems(arrayOf(i2.item))
                        }.sender()
                        player.closeInventory()
                        return
                    }
                    else -> return
                }
            }
        }

    }

    override fun onClose(event: InventoryCloseEvent) {
    }

}