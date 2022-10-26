package me.geek.mail.common.menu

import me.geek.mail.GeekMail
import me.geek.mail.api.mail.MailManage
import me.geek.mail.api.mail.MailManage.sound
import me.geek.mail.common.menu.sub.IconType
import me.geek.mail.common.template.Template
import me.geek.mail.common.market.Market
import me.geek.mail.modules.settings.SetTings
import me.geek.mail.utils.serializeItemStacks
import org.bukkit.Bukkit
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
import taboolib.module.lang.sendLang
import taboolib.module.nms.i18n.I18n
import java.util.*

/**
 * 作者: 老廖
 * 时间: 2022/10/26
 *
 **/
class MarketBuy(
    private val player: Player,
    private val InfoItem: ItemStack,
    private val MarketItemUid: UUID,
    )
{
    private val tag = Menu.getSession("MarketBuy")

    private val inv: Inventory = Menu.build(player, "MarketBuy").apply {
        for ((ind, value) in tag.stringLayout.withIndex()) {
            if (value != ' ') {
                tag.micon.forEach {
                    if (it.icon[0] == value && it.type == IconType.MARKET_ITEM) {
                        GeekMail.debug("找到展示位 ")
                        if (InfoItem.type.isAir) GeekMail.debug("物品数据丢失...")
                        val item = this.contents
                        item[ind] = InfoItem
                        this.contents = item
                    }
                }
            }
        }
    }

    init { action() }

    private fun action() {
        player.sound("BLOCK_NOTE_BLOCK_HARP",1f, 1f)
        player.openInventory(inv)
        Menu.isOpen.add(player)
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
                                    IconType.CONFIRM -> {
                                        buyItem()
                                    }
                                    IconType.CANCEL -> {
                                        player.closeInventory()
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

    private fun buyItem() {

        val i2 = Market.getMarketItem(MarketItemUid) //获取商品
        if (i2 != null) {
             if (i2.condition(player)) {

                val buy = Template.getAdminPack(SetTings.market.player_buy_sendPack)
                val sell = Template.getAdminPack(SetTings.market.player_sell_sendPack)
                if (buy == null || sell == null) {
                    GeekMail.say("&c你的市场邮箱发货模板未正确配置...")
                    return
                }

                Market.remMarketItem(i2.packUid) // 删缓存
                i2.runCondition(player) // 扣除玩家需求

                // 发送给购买者
                MailManage.getMailObjData(buy.type)?.javaClass?.invokeConstructor(
                    arrayOf(
                        UUID.randomUUID().toString(),
                        buy.title,
                        buy.text.replace("{item-name}", I18n.instance.getName(i2.item)),
                        SetTings.Console.toString(),
                        player.uniqueId.toString(),
                        "未提取",
                        "",
                        System.currentTimeMillis().toString(),
                        "0",
                        i2.itemString,
                        ""
                    )
                )?.sendMail()

                // 发送给出售者
                MailManage.getMailObjData(sell.type)?.javaClass?.invokeConstructor(
                    arrayOf(
                        UUID.randomUUID().toString(),
                        sell.title,
                        sell.text.replace("{item-name}", I18n.instance.getName(i2.item)),
                        SetTings.Console.toString(),
                        i2.user.toString(),
                        "未提取",
                        "MONEY:${i2.money}@POINTS:${i2.points}",
                        System.currentTimeMillis().toString(),
                        "0",
                        "",
                        ""
                    )
                )?.sendMail()
                 this@MarketBuy.inv.clear()
                 player.closeInventory()

            } else adaptPlayer(player).sendLang("玩家-市场购买-货币不足", i2.money, i2.points)
        } else adaptPlayer(player).sendLang("玩家-市场购买-商品不存在")
    }

}