package me.geek.mail.modules

import com.google.gson.annotations.Expose
import me.geek.mail.GeekMail

import me.geek.mail.api.mail.MailManage.sound
import me.geek.mail.api.mail.MailSub
import me.geek.mail.common.menu.Menu
import me.geek.mail.modules.settings.SetTings
import me.geek.mail.utils.deserializeItemStacks

import java.util.UUID
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.lang.sendLang
import taboolib.platform.util.giveItem

/**
 * 作者: 老廖
 * 时间: 2022/8/6
 */
class Mail_Item(
    override val mailID: UUID = UUID.fromString("00000000-0000-0000-0000-000000000001"),
    override val mailType: String = "物品邮件",
    override var title: String = "",
    override var text: String = "",
    override var sender: UUID = UUID.fromString("00000000-0000-0000-0000-000000000001"),
    override var target: UUID = UUID.fromString("00000000-0000-0000-0000-000000000001"),
    override var state: String = "",
    override var appendixInfo: String = "-",
    @Expose
    override var itemStacks: Array<ItemStack>? = null,
    override val senderTime: String = "",
    override var getTime: String = "",
    @Expose
    override val permission: String = "mail.exp.items",
    ) : MailSub() {

    constructor(args: Array<String>) : this(
        UUID.fromString(args[0]),
        "物品邮件",
        title = args[1],
        text = args[2],
        sender = UUID.fromString(args[3]),
        target = UUID.fromString(args[4]),
        state = args[5],
        "",
        itemStacks = null,
        senderTime = args[7],
        getTime = args[8]
    ) {
        if (args.size >= 10) {
            itemStacks = args[9].deserializeItemStacks()
            appendixInfo = getItemInfo(StringBuilder(""))
        }
    }


    override fun sendMail() {
        if (itemStacks == null) {
            Bukkit.getPlayer(this.sender)?.action(false)
        } else {
            super.sendMail()
        }
    }

    override fun giveAppendix() {
        Bukkit.getPlayer(this.target)?.let {
            this.itemStacks?.asList()?.let {
                    it1 -> it.giveItem(it1)
            }
        }
    }
    private fun Player.action(isCross: Boolean) {
        Menu.isOpen.add(this)

        this.openInventory(Bukkit.createInventory(player, 27, "§0放入物品 §7| §0关闭菜单 "))

        this.sound("BLOCK_NOTE_BLOCK_HARP", 1f, 1f)

        Bukkit.getPluginManager().registerEvents(object : Listener {

            @EventHandler
            fun onClose(e: InventoryCloseEvent) {
                if (player == e.player) {
                    Menu.isOpen.removeIf { it == player }
                    if (GeekMail.plugin_status) {
                        val i1 = mutableListOf<ItemStack>().apply {
                            for (i2 in e.inventory.contents) {
                                if (i2 != null) {
                                    this.add(i2)
                                }
                            }
                            if (!this@action.isOp) this@action.itemFilter(this)
                        }

                        if (i1.size > 0) {
                            itemStacks = i1.toTypedArray()
                            if (!isCross) {
                                sender()
                            }
                            HandlerList.unregisterAll(this)
                            isOk = true
                            return
                        }
                    } else {
                        for (item in e.inventory) {
                            this@action.inventory.addItem(item)
                        }
                    }
                    // 无论任何注销监听器
                    HandlerList.unregisterAll(this)
                    isOk = true
                }
            }
        }, GeekMail.instance)
    }

    private fun sender() {
        appendixInfo = getItemInfo(StringBuilder(""))
        super.sendMail()
    }

    override fun condition(player: Player, appendix: String): Boolean {
        return true
    }

    fun sendCrossMail() {
        Bukkit.getPlayer(this.sender)?.action(true)
    }

    // 用于发送跨服邮件时的等待物品装填。
    var isOk = false

    private fun Player.itemFilter(itemStacks: MutableList<ItemStack>) {
        val outItem = mutableListOf<ItemStack>()
        itemStacks.forEach { stack ->
            stack.itemMeta?.let { meta ->
                SetTings.filter.contains_name.forEach {
                    if (meta.displayName.contains(it)) {
                        outItem.add(stack)
                    }
                }
                SetTings.filter.contains_lore.forEach {
                    meta.lore?.let { a ->
                        if (a.contains(it)) {
                            outItem.add(stack)
                        }
                    }
                }
            }
        }
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