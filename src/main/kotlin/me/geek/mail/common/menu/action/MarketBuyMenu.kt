package me.geek.mail.common.menu.action

import me.geek.mail.GeekMail
import me.geek.mail.api.mail.MailManage
import me.geek.mail.common.market.Market
import me.geek.mail.common.menu.Menu.openMenu
import me.geek.mail.common.menu.MenuBase
import me.geek.mail.common.menu.sub.IconType
import me.geek.mail.common.menu.sub.MenuData
import me.geek.mail.common.settings.SetTings
import me.geek.mail.common.template.Template
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.i18n.I18n
import taboolib.platform.util.sendLang
import java.util.*

/**
 * 作者: 老廖
 * 时间: 2023/1/23
 *
 **/
class MarketBuyMenu(
    override val player: Player,
    override val menuData: MenuData,
    private val ca: MenuData,
    private val InfoItem: ItemStack,
    private val MarketItemUid: UUID,
): MenuBase() {
    override fun build(): MenuBase {
        menuData.layout.forEachIndexed { index, c ->
            if (c != ' ') {
                menuData.icon[c]?.let {
                    if (it.iconType == IconType.MARKET_ITEM) {
                        this.inventory.setItem(index, InfoItem)
                     //   this.inventory.contents[index] = InfoItem
                        GeekMail.debug("InfoItem -ac ${InfoItem.type}")
                    }
                }
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

        menuData.layout[event.rawSlot].let {
            menuData.icon[it]?.let { icon ->
                when (icon.iconType) {
                    IconType.CONFIRM -> {
                        val i2 = Market.getMarketItem(MarketItemUid) //获取商品
                        if (i2 != null) {
                            if (i2.condition(player)) {
                                val buy = Template.getAdminPack(SetTings.market.player_buy_sendPack)
                                val sell = Template.getAdminPack(SetTings.market.player_sell_sendPack)
                                if (buy == null || sell == null) {
                                    GeekMail.say("&c你的市场邮箱发货模板未正确配置...")
                                    return
                                }
                                i2.runCondition(player) // 扣除玩家需求
                                Market.remMarketItem(i2.packUid, true) // 删缓存
                                val name = if (i2.item.itemMeta!!.hasDisplayName()) i2.item.itemMeta!!.displayName else I18n.instance.getName(i2.item)
                                MailManage.buildMail(buy.type,
                                    "title" to buy.title,
                                    "text" to buy.text.replace("{item-name}", name),
                                    "target" to player.uniqueId,
                                    "itemStacks" to arrayOf(i2.item)
                                ).sendMail()
                                MailManage.buildMail(sell.type,
                                    "title" to sell.title,
                                    "text" to sell.text.replace("{item-name}", name),
                                    "target" to i2.user,
                                    "additional" to "MONEY:${i2.money}@POINTS:${i2.points}"
                                ).sendMail()
                                player.closeInventory()
                            } else player.sendLang("玩家-市场购买-货币不足", i2.money, i2.points)
                        } else player.sendLang("玩家-市场购买-商品不存在")
                        return
                    }
                    IconType.CANCEL -> {
                        player.openMenu(ca)
                        return
                    }
                    else -> {}
                }
            }
        }
    }

    override fun onClose(event: InventoryCloseEvent) {
    }
}