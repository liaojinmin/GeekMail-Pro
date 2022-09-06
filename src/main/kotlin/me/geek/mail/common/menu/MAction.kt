package me.geek.mail.common.menu

import com.google.common.base.Joiner
import me.geek.mail.Configuration.ConfigManager
import me.geek.mail.GeekMail
import me.geek.mail.GeekMail.say
import me.geek.mail.common.DataBase.DataManage

import me.geek.mail.common.menu.sub.IconType.*
import me.geek.mail.api.mail.MailManage
import me.geek.mail.api.utils.HexUtils
import me.geek.mail.api.mail.MailSub
import me.geek.mail.api.utils.Expiry
import me.geek.mail.common.menu.sub.Session
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.console
import taboolib.module.lang.sendLang
import java.util.*

/**
 * 作者: 老廖
 * 时间: 2022/8/1
 */
class MAction(
    private val player: Player, // 会话默认图标缓存
    private val tag: Session, // 会话界面
    private val inv: Inventory
) {
    // 邮件索引缓存， key = 邮件所在槽位， value = 邮件唯一标识
    private val cache: MutableMap<String, String> = HashMap()
    // 当前所在页面
    private var page = 0
    // 翻页界面
    private val contents: MutableList<Array<ItemStack>> = ArrayList()
    private val air = ItemStack(Material.AIR)

    private val icon = tag.micon
    // 插件实列
    private val plugin = GeekMail.instance
    // 会话邮件缓存
    private val mail: MutableList<MailSub> = MailManage.getTargetCache(player.uniqueId)

    private var view: Boolean = false
    private val poxPlayer = adaptPlayer(player)

    init {
        //      long start = System.currentTimeMillis();
        action()
        //     GeekMail.say("打开菜单耗时: "+(System.currentTimeMillis() - start) + " ms");
    }

    private fun action() {
        Menu.isOpen.add(player)
        GeekMail.debug("为玩家: ${player.uniqueId} 打开UI")
        if (mail.isNotEmpty()) {
            Build()
        }
        if (contents.size != 0) {
            inv.contents = contents[0]
        }

        MailManage.Sound(player, "BLOCK_NOTE_BLOCK_HARP",1f, 1f)
        player.openInventory(inv)
        Bukkit.getPluginManager().registerEvents(object : Listener {

            var cd: Long = 0
            @EventHandler
            fun onClick(e: InventoryClickEvent) {
                if (cd < System.currentTimeMillis()) {
                    cd = System.currentTimeMillis() + 300
                    if (e.view.title != tag.title || e.view.player !== player) return
                    if (e.rawSlot < 0) return
                    e.isCancelled = true
                    if (e.rawSlot < tag.stringLayout.length) {
                        if (view) {
                            inv.contents = contents[page]
                            view = false
                        }
                        val id = tag.stringLayout[e.rawSlot].toString()
                        for (micon in icon) {
                            if (micon.icon == id) {
                                when (micon.type) {
                                    NEXT_PAGE -> {
                                        if (contents.size > page + 1) {
                                            page += 1
                                            inv.contents = contents[page]
                                            MailManage.Sound(player, "BLOCK_SCAFFOLDING_BREAK",1f, 1f)
                                        } else {
                                            MailManage.Sound(player, "BLOCK_NOTE_BLOCK_DIDGERIDOO",1f, 1f)
                                        }
                                        return
                                    }
                                    LAST_PAGE -> {
                                        if (page != 0) {
                                            page -= 1
                                            inv.contents = contents[page]
                                            MailManage.Sound(player, "BLOCK_SCAFFOLDING_BREAK",1f, 1f)
                                        } else {
                                            MailManage.Sound(player, "BLOCK_NOTE_BLOCK_DIDGERIDOO",1f, 1f)
                                        }
                                        return
                                    }
                                    NORMAL -> {
                                       // say("NORMAL 点击了通用")
                                        return
                                    }
                                    DELETE -> {
                                        if (e.isRightClick) {
                                            if (mail.size >= 1) {
                                                onDeleteAll()
                                                player.closeInventory()
                                                MailManage.Sound(player, "BLOCK_SOUL_SAND_STEP",0.7f, 1f)
                                            } else {
                                                MailManage.Sound(player, "BLOCK_NOTE_BLOCK_DIDGERIDOO",0.7f, 1f)
                                            }
                                        }
                                        return
                                    }
                                    BACK -> {
                                     //   say("SEND 点击了发送")
                                        return
                                    }
                                    TEXT -> {
                                        if (e.isRightClick) {
                                            onDeleteMail(e.rawSlot)
                                        }
                                        if (e.isLeftClick && !e.isShiftClick) {
                                            view = false;
                                            getAppendix(e.rawSlot)
                                        }
                                        if (e.isShiftClick) {
                                            view = true;
                                            getAppendix(e.rawSlot)
                                        }
                                        return
                                    }
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
                player.updateInventory()
                if (player === e.player) {
                    HandlerList.unregisterAll(this)
                    Menu.isOpen.removeIf { it === player }
                }
            }
        }, plugin)
    }

    /**
     * 获取指定槽位的邮件附件，
     * @param index 槽位索引
     */
    private fun getAppendix(index: Int) {
        if (mail.isNotEmpty()) {
            for ((i, mail) in mail.withIndex()) {
                if (value(index, mail.mailID) == cache[key(index, page)]) {
                    GeekMail.debug("槽位索引: $index - 邮件ID: ${mail.mailID}")
                    if (mail.state == "未提取") {
                        if (view && mail.mailType.contains("物品")) {
                            inv.contents = mail.itemStacks!!
                            return
                        }
                        val itemStacks = inv.contents
                        mail.giveAppendix()
                        mail.state = "已提取"
                        mail.getTime = System.currentTimeMillis().toString()

                        this.mail[i] = mail
                        // 更新GUI物品
                        val itemMeta = itemStacks[index].itemMeta
                        if (itemMeta != null && itemMeta.hasLore()) {
                            val lore = itemMeta.lore
                            lore?.replaceAll { v: String ->
                                v.replace("未提取", "已提取")
                            }
                            /*
                            if (lore != null) {
                                for ((key, value) in lore.withIndex()) {
                                   if (value.contains("未提取")) {
                                       lore[key] = "§7领取时间: §f${Expiry.getExpiryFoData(mail.getTime.toLong())}"
                                    }
                                }

                            }
                             */
                            itemMeta.lore = lore
                            itemMeta.removeEnchant(Enchantment.DAMAGE_ALL)
                            itemMeta.removeItemFlags(ItemFlag.HIDE_ENCHANTS)
                            itemStacks[index]!!.itemMeta = itemMeta
                        }
                        inv.contents = itemStacks
                        contents[page] = itemStacks

                        // 异步更新数据库
                        Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin) { DataManage.update(mail) }

                        poxPlayer.sendLang("玩家-领取附件-成功", mail.appendixInfo)

                    } else {
                        if (mail.state != "无") {
                            poxPlayer.sendLang("玩家-领取附件-失败")
                        }
                    }
                }
            }
        }
    }

    /**
     * 删除指定槽位的邮件
     * @param index 槽位索引
     */
    private fun onDeleteMail(index: Int) {
        for (m in mail) {
            //判断邮件的唯一ID 是否和会话缓存中的一致
            if (value(index, m.mailID) == cache[key(index, page)]) {

                if (m.state != "未提取") {
                    val itemStacks = contents[page]
                    itemStacks[index] = air
                    // 邮件已删除 ，删除本次会话中的该邮件缓存图标显示
                    cache.remove(key(index, page))
                    inv.contents = itemStacks
                    contents[page] = itemStacks

                    MailManage.remIndexTofTarget(player.uniqueId, m.mailID)

                    Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin) { DataManage.delete(m.mailID) }

                    // 发送邮件删除消息
                    poxPlayer.sendLang("玩家-删除邮件-成功")

                    MailManage.Sound(player, "BLOCK_NOTE_BLOCK_DIDGERIDOO",1f, 2f)
                } else {
                    // 发送邮件不可删除消息

                    poxPlayer.sendLang("玩家-删除邮件-失败")
                    MailManage.Sound(player, "BLOCK_NOTE_BLOCK_DIDGERIDOO",1f, 1f)
                }
            }
        }
    }

    /**
     * 删除邮件
     */
    private fun onDeleteAll() {
        contents.clear()
        cache.clear()
        var c = false
        for (m in mail) {
            if (m.state == "已提取" || m.state == "无") {
                c = true
                MailManage.remIndexTofTarget(player.uniqueId, m.mailID)
            }
        }
        if (c) {
            Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin) { DataManage.delete(player.uniqueId, "已提取") }
        }
    }

    // 构建邮件图标
    private fun Build() {
        var item = inv.contents
        val layout = tag.stringLayout
        var index = layout.indexOf("M")
        val end = layout.lastIndexOf("M")
        if (index != -1 && end != -1) {
            for (mail1 in mail) {
                if (index <= end) {
                    try {
                        if (item[index] != null) {
                            index = layout.indexOf("M", index)
                        }
                        item[index] = mailItem(index, mail1)
                        index++
                    } catch (ignored: NullPointerException) {
                        say("空指针")
                        ignored.printStackTrace()
                    }
                } else {
                    contents.add(item)
                    index = layout.indexOf("M")
                    item = inv.contents
                }
            }
            contents.add(item)
        }
    }

    /**
     *
     * @param index 箱子界面的格子索引
     * @param mail 对应格子的邮件信息
     * @return ItemStack
     */
    private fun mailItem(index: Int, mail: MailSub): ItemStack? {
        for (micon in icon) {
            if (micon.type == TEXT) {
                // 缓存 该邮件ID 的索引
                cache[key(index, contents.size)] = value(index, mail.mailID)
                val name = micon.name
                var lore = micon.lore
                val itemStack = ItemStack(Material.valueOf(micon.mats))
                val itemMeta = itemStack.itemMeta
                if (itemMeta != null) {
                    itemMeta.setDisplayName(HexUtils.colorify(name.replace("[title]", mail.title)))
                    lore = if (mail.sender == ConfigManager.Console) {
                        val var100 = "系统"
                        listOf(
                            *HexUtils.colorify(Joiner.on(",").join(lore).replace("[type]", mail.mailType)
                                .replace("[sender]", var100)
                                .replace("[senderTime]", Expiry.getExpiryFoData(mail.senderTime.toLong()))
                                .replace("[getTime]", Expiry.getExpiryFoData(mail.getTime.toLong()))
                                .replace("[text]", mail.text.replace(";",","))
                                .replace("[state]", mail.state)
                                .replace("[item]", mail.appendixInfo)).split(",").toTypedArray()
                        )
                    } else {
                        val var100 = Bukkit.getOfflinePlayer(mail.sender).name
                        listOf(
                            *HexUtils.colorify(Joiner.on(",").join(lore).replace("[type]", mail.mailType)
                                .replace("[sender]", var100!!)
                                .replace("[senderTime]", Expiry.getExpiryFoData(mail.senderTime.toLong()))
                                .replace("[getTime]", Expiry.getExpiryFoData(mail.getTime.toLong()))
                                .replace("[text]", mail.text.replace(";",","))
                                .replace("[state]", mail.state)
                                .replace("[item]", mail.appendixInfo)).split(",").toTypedArray()
                        )
                    }
                    itemMeta.lore = lore
                    if (mail.state == "未提取") {
                        itemMeta.addEnchant(Enchantment.DAMAGE_ALL, 1, true)
                        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
                    }
                }
                itemStack.itemMeta = itemMeta
                return itemStack
            }
        }
        return null
    }

    /**
     *
     * @param index 图标索引位
     * @param Page 当前页面
     * @return 返回拼接字符串
     */
    private fun key(index: Int, Page: Int): String {
        return index.toString() + Page
    }

    /**
     *
     * @param index 图标索引位
     * @param mail_id 邮件唯一ID
     * @return 返回拼接字符串
     */
    private fun value(index: Int, mail_id: UUID): String {
        return index.toString() + mail_id.toString()
    }
}