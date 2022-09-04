package me.geek.mail.Modules

import com.google.common.base.Joiner
import me.geek.mail.Configuration.ConfigManager
import me.geek.mail.GeekMail
import me.geek.mail.api.utils.ChineseMaterial
import me.geek.mail.api.mail.MailManage
import me.geek.mail.common.DataBase.DataManage
import me.geek.mail.api.mail.MailSub
import java.util.UUID
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.player.PlayerPickupItemEvent
import org.bukkit.inventory.ItemStack
import taboolib.platform.util.giveItem

/**
 * 作者: 老廖
 * 时间: 2022/8/6
 */
class Mail_Item(
    override val mailID: UUID,
    override val mailType: String,

    override var title: String,
    override var text: String,
    override var sender: UUID,
    override var target: UUID,
    override var state: String,
    override var appendixInfo: String,

    override var itemStacks: Array<ItemStack>?,
    override val senderTime: String,
    override var getTime: String,

    ) : MailSub() {


    constructor(mailID: UUID, Title: String, Text: String, sende: UUID, targe: UUID, state: String, exp: String) : this(
        mailID,
        "物品邮件",
        Title,
        Text,
        sende,
        targe,
        state,
        "",
        null,
        senderTime = System.currentTimeMillis().toString(),
        getTime = ""
    )
    constructor(mailID: UUID, Title: String, Text: String, sende: UUID, targe: UUID, state: String, exp: String,  item: Array<ItemStack>?, command: List<String>?, time: Array<Any>) : this(
        mailID,
        "物品邮件",
        Title,
        Text,
        sende,
        targe,
        state,
        "",
        itemStacks = item,
        senderTime = time[0].toString(),
        getTime = time[1].toString()
    ) {
        appendixInfo = appendixInfo()
    }


    override fun sendMail() {
        Bukkit.getPlayer(this.sender)?.let {
            action(it)
        }
    }

    override fun giveAppendix() {
        Bukkit.getPlayer(this.target)?.let {
            this.itemStacks?.asList()?.let {
                    it1 -> it.giveItem(it1)
            }
        }
    }
    private fun action(player: Player) {
        GeekMail.menu.isOpen.add(player)
        player.openInventory(Bukkit.createInventory(player, 9, "§0§l放入物品 §7| §0§l关闭菜单 "))
        MailManage.Sound(player, "BLOCK_NOTE_BLOCK_HARP", 1f, 1f)
        GeekMail.say("开启物品UI")
        Bukkit.getPluginManager().registerEvents(object : Listener {
            @EventHandler
            fun onDrag(e: InventoryDragEvent) {
                if (player === e.whoClicked) {
                    e.isCancelled = true
                }
            }

            @EventHandler
            fun onPickup(e: PlayerPickupItemEvent) {
                if (player === e.player) {
                    e.isCancelled = true
                }
            }
            @EventHandler
            fun onClose(e: InventoryCloseEvent) {
                if (player == e.player) {
                    GeekMail.menu.isOpen.removeIf { it == player }
                    if (GeekMail.plugin_status) {
                        val item = e.inventory.contents
                        val i1: MutableList<ItemStack> = java.util.ArrayList()
                        for (i2 in item) {
                            if (i2 != null) {
                                i1.add(i2)
                            }
                        }
                        if (i1.size > 1) {
                            itemStacks = i1.toTypedArray()
                            sender()
                            HandlerList.unregisterAll(this)
                            return
                        }

                    } else {
                        for (item in e.inventory) {
                            player.inventory.addItem(item)
                        }
                    }
                    // 无论任何注销所以已注册监听器
                    HandlerList.unregisterAll(this)
                }
            }
        }, GeekMail.instance)
        GeekMail.say("关闭物品UI")
    }
    private fun sender() {
        appendixInfo = appendixInfo()
        super.sendMail()
    }

    private fun appendixInfo(): String {
        val lore: MutableList<String> = ArrayList()
        lore.clear()
        var index = 0
        itemStacks?.let {
            for (Stack in it) {
                val meta = Stack.itemMeta
                if (meta != null) {
                    if (meta.hasDisplayName()) {
                        lore.add(meta.displayName + " §7* §f" + Stack.amount)
                    } else {
                        val manes = ChineseMaterial.translate(Stack.type)
                        if (manes != "null") {
                            lore.add(ChineseMaterial.translate(Stack.type) + " §7* §f" + Stack.amount)
                        } else {
                            index++
                        }
                    }
                }
            }
        }
        if (index > 0) {
            lore.add("§7剩余 §6$index §7项未显示...")
        }
        return Joiner.on(", §f").join(lore)
    }
}