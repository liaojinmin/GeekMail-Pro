package me.geek.mail.common.menu


import com.google.common.base.Joiner
import me.geek.mail.GeekMail
import me.geek.mail.common.data.SqlManage
import me.geek.mail.api.hook.HookPlugin
import me.geek.mail.api.mail.MailManage
import me.geek.mail.api.mail.MailManage.sound
import me.geek.mail.api.mail.MailSub
import me.geek.mail.modules.catcher.Chat
import me.geek.mail.common.menu.sub.IconType.*
import me.geek.mail.common.menu.sub.Session
import me.geek.mail.modules.settings.SetTings
import me.geek.mail.utils.colorify

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
import org.bukkit.inventory.meta.BundleMeta
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.submit
import taboolib.common.platform.function.submitAsync
import taboolib.module.lang.sendLang
import java.lang.IllegalArgumentException
import java.text.SimpleDateFormat
import java.util.*

/**
 * 作者: 老廖
 * 时间: 2022/8/1
 */
class MailMenu(private val player: Player, private val tag: Session, private val inv: Inventory) {
    private val format = SimpleDateFormat("yyyy年 MM月 dd日 HH:mm:ss")
    // 邮件索引缓存， key = 邮件所在槽位， value = 邮件唯一标识
    private val cache: MutableMap<String, String> = HashMap()
    // 当前所在页面
    private var page = 0
    // 翻页界面
    private val contents: MutableList<Array<ItemStack>> = ArrayList()

    private val air = ItemStack(Material.AIR)

    private val icon = tag.micon

    // 会话邮件缓存
    private val mail = MailManage.getPlayerMailCache(player.uniqueId)

    private var view: Boolean = false

    private val poxPlayer = adaptPlayer(player)

    init { action() }

    private fun action() {
        Menu.isOpen.add(player)
        GeekMail.debug("为玩家: ${player.uniqueId} 打开UI")

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
                if (e.view.title != tag.title || e.view.player !== player) return
                if (cd < System.currentTimeMillis()) {
                    cd = System.currentTimeMillis() + 300
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
                                            player.sound("BLOCK_SCAFFOLDING_BREAK",1f, 1f)
                                        } else {
                                            player.sound("BLOCK_NOTE_BLOCK_DIDGERIDOO",1f, 1f)
                                        }
                                        return
                                    }
                                    LAST_PAGE -> {
                                        if (page != 0) {
                                            page -= 1
                                            inv.contents = contents[page]
                                            player.sound("BLOCK_SCAFFOLDING_BREAK",1f, 1f)
                                        } else {
                                            player.sound("BLOCK_NOTE_BLOCK_DIDGERIDOO",1f, 1f)
                                        }
                                        return
                                    }
                                    NORMAL -> {
                                       // say("NORMAL 点击了通用")
                                        return
                                    }
                                    DELETE -> {
                                        if (mail.size >= 1) {
                                            onDeleteAll()
                                            player.closeInventory()
                                            player.sound("BLOCK_SOUL_SAND_STEP",0.7f, 1f)
                                        } else {
                                            player.sound("BLOCK_NOTE_BLOCK_DIDGERIDOO",0.7f, 1f)
                                        }
                                        return
                                    }
                                    BIND -> {
                                        if (MailManage.getMailPlayerData(player.uniqueId)!!.mail.isEmpty()) {
                                            Chat(player).start()
                                            player.closeInventory()
                                            return
                                        }
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
                                    GET_ALL -> {
                                        if (mail.size >= 1) {
                                            onGetRewardAll()
                                            player.closeInventory()
                                            player.sound("BLOCK_SOUL_SAND_STEP",0.7f, 1f)
                                        } else {
                                            player.sound("BLOCK_NOTE_BLOCK_DIDGERIDOO",0.7f, 1f)
                                        }
                                        return
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
                if (player === e.player) {
                    HandlerList.unregisterAll(this)
                    Menu.isOpen.removeIf { it === player }
                }
            }
        }, GeekMail.instance)
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

                        mail.state = "已提取"
                        mail.getTime = System.currentTimeMillis().toString()

                        // 更新缓存状态
                        this.mail[i] = mail

                        // 更新GUI物品
                        val itemMeta = itemStacks[index].itemMeta
                        if (itemMeta != null && itemMeta.hasLore()) {
                            itemMeta.lore = itemMeta.lore.apply {
                                val time = mail.getTime.toLong()
                                this?.replaceAll { v: String ->
                                    v.replace("未提取", "已提取")
                                    v.replace("未领取", if (time < 1000) "未领取" else format.format(time))
                                }
                            }
                            itemMeta.removeEnchant(Enchantment.DAMAGE_ALL)
                            itemMeta.removeItemFlags(ItemFlag.HIDE_ENCHANTS)
                            itemStacks[index]!!.itemMeta = itemMeta
                        }
                        // 更新当前页面
                        inv.contents = itemStacks
                        // 更新翻页
                        contents[page] = itemStacks

                        // 更新数据库 数据安全更新后发放附件

                        var execute: Boolean
                        submitAsync {
                            // 数据是否更新成功
                           execute = SqlManage.updateMail(mail)
                           submit {
                               if (execute) {
                                   // 已更新给予玩家邮件附件
                                   mail.giveAppendix()
                                   poxPlayer.sendLang("玩家-领取附件-成功", mail.appendixInfo)
                               } else {
                                   poxPlayer.sendLang("PLAYER-ERROR-LOCK", "getAppendix()")
                               }
                           }
                        }
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
     * 获取所有邮件奖励
     */
    private fun onGetRewardAll() {
        contents.clear()
        cache.clear()
        val data = mutableListOf<MailSub>()
        for ((i, m) in mail.withIndex()) {
            if (m.state == "未提取") {
                m.state = "已提取"
                m.getTime = System.currentTimeMillis().toString()
                // 更新缓存状态
                this.mail[i] = m
                data.add(m)
            }
        }

        var execute: IntArray
        if (data.isNotEmpty()) {
            submitAsync {
                execute = SqlManage.updateListMail(data)
                submit {
                    if (execute.size == data.size) {
                        for ((index, value) in execute.withIndex()) {
                            if (value != 0) {
                                data[index].giveAppendix()
                            }
                        }
                    } else poxPlayer.sendLang("PLAYER-ERROR-LOCK", "GetRewardAll()")
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
                    MailManage.remPlayerMail(player.uniqueId, m.mailID)

                    // 更新数据库
                    submitAsync { SqlManage.deleteMail(m.mailID) }

                    // 发送邮件删除消息
                    poxPlayer.sendLang("玩家-删除邮件-成功")
                } else {
                    // 发送邮件不可删除消息

                    poxPlayer.sendLang("玩家-删除邮件-失败")
                }
            }
        }
    }

    /**
     * 删除所有已领取邮件
     */
    private fun onDeleteAll() {
        contents.clear()
        cache.clear()
        var c = false
        if (mail.isEmpty()) return
        for (m in mail) {
            if (m.state == "已提取" || m.state == "无") {
                c = true
                MailManage.remPlayerMail(player.uniqueId, m.mailID)
            }
        }
        // 更新数据库
        if (c) submitAsync { SqlManage.deleteStateMail(player.uniqueId, "已提取") }

    }

    // 构建邮件图标
    private fun build() {
        var item = inv.contents
        val layout = tag.stringLayout
        var index = layout.indexOf("M")
        val end = layout.lastIndexOf("M")

        icon.forEach {
            if (it.type == BIND) {
                val info = layout.indexOf(it.icon)
                item[info].apply {
                    val meta = itemMeta
                    if (meta != null) {
                        meta.lore = Joiner.on(",").join(it.lore)
                            .replace("[mail_Info]", MailManage.getMailPlayerData(player.uniqueId)!!.mail)
                            .split(",")
                    }
                    itemMeta = meta
                }
            }
        }
        if (mail.isEmpty()) return

        // 开始构建邮件展示
        if (index != -1 && end != -1) {
            for (mail1 in mail) {
                if (index <= end) {

                    if (item[index] != null) {
                        index = layout.indexOf("M", index)
                    }

                    item[index] = mailItem(index, mail1)
                    index++
                } else {
                    contents.add(item)
                    item = inv.contents
                    index = layout.indexOf("M")
                    item[index] = mailItem(index, mail1)
                    index++
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
                // micon.mats   micon.data.toShort()
                val itemStack = mail.getIcon(micon.mats, micon.data.toShort())

                val itemMeta = if (SetTings.USE_BUNDLE) {
                    itemStack.type = Material.BUNDLE
                    (itemStack.itemMeta as BundleMeta).also {
                        it.setItems(mail.itemStacks?.asList())
                    }
                } else itemStack.itemMeta

                if (itemMeta != null) {
                    itemMeta.setDisplayName(name.replace("[title]", mail.title).colorify())
                    itemMeta.lore = mail.parseMailInfo(micon.lore)
                    if (mail.state == "未提取") {
                        itemMeta.addEnchant(Enchantment.DAMAGE_ALL, 1, true)
                        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
                        itemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS)
                    }
                    itemStack.itemMeta = itemMeta
                }
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
        return "$index$Page"
    }

    /**
     *
     * @param index 图标索引位
     * @param mail_id 邮件唯一ID
     * @return 返回拼接字符串
     */
    private fun value(index: Int, mail_id: UUID): String {
        return "$index$mail_id"
    }
}