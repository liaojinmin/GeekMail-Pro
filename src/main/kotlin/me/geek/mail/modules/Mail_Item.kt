package me.geek.mail.modules

import com.google.common.base.Joiner
import me.geek.mail.GeekMail
import me.geek.mail.api.utils.ChineseMaterial
import me.geek.mail.api.mail.MailManage
import me.geek.mail.api.mail.MailSub
import me.geek.mail.common.menu.Menu
import me.geek.mail.common.serialize.base64.StreamSerializer
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


    constructor() : this(
        UUID.fromString("00000000-0000-0000-0000-000000000001"),
        "物品邮件",
        "",
        "",
        UUID.fromString("00000000-0000-0000-0000-000000000001"),
        UUID.fromString("00000000-0000-0000-0000-000000000001"),
        "",
        "-",
        null,
        senderTime = "",
        getTime = ""
    )
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
            itemStacks = StreamSerializer.deserializeItemStacks(args[9])
            appendixInfo = runAppendixInfo()
        }
    }


    override fun sendMail() {
        if (itemStacks == null) {
            Bukkit.getPlayer(this.sender)?.let {
                action(it)
            }
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
    private fun action(player: Player) {
        Menu.isOpen.add(player)
        player.openInventory(Bukkit.createInventory(player, 9, "§0§l放入物品 §7| §0§l关闭菜单 "))
        MailManage.Sound(player, "BLOCK_NOTE_BLOCK_HARP", 1f, 1f)
        GeekMail.debug("开启物品UI")
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
                    Menu.isOpen.removeIf { it == player }
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
    }
    private fun sender() {
        appendixInfo = runAppendixInfo()
        super.sendMail()
    }

    private fun runAppendixInfo(): String {
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
                            lore.add(manes + " §7* §f" + Stack.amount)
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