package me.Geek.Libs.Menu

import me.Geek.GeekMail.instance
import me.Geek.GeekMail.plugin_status
import java.util.UUID
import me.Geek.GeekMail
import me.Geek.Modules.MailItem
import me.Geek.Modules.MailManage
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.player.PlayerPickupItemEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import java.util.ArrayList

/**
 * 作者: 老廖
 * 时间: 2022/8/6
 */
class MItem(
    private val player: Player,
    private val sender: UUID,
    private val target: UUID,
    private val Title: String,
    private val Text: String
) {
    private val plugin: Plugin = instance
    init {
        action()
    }
    private fun action() {
        GeekMail.menu.isOpen.add(player)
        player.openInventory(Bukkit.createInventory(player, 9, "§0§l放入物品 §7| §0§l关闭菜单 "))
        MailManage.Sound(player, "BLOCK_NOTE_BLOCK_HARP", 1f, 1f)
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
                    if (plugin_status) {
                        val itemStacks = e.inventory.contents

                        val i1: MutableList<ItemStack> = ArrayList()

                        for (i2 in itemStacks) {
                            if (i2 != null) {
                                i1.add(i2)
                            }
                        }
                        if (i1.size < 1) {
                            HandlerList.unregisterAll(this)
                            return
                        } else {
                            MailItem(
                                UUID.randomUUID(), sender, target, Title, Text, *i1.toTypedArray()
                            ).SendMail()
                        }
                    } else {
                        for (item in e.inventory) {
                            player.inventory.addItem(item)
                        }
                    }
                    HandlerList.unregisterAll(this)
                }
            }
        }, plugin)
    }
}