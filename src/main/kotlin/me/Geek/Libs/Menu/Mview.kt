package me.Geek.Libs.Menu

import me.Geek.GeekMail
import me.Geek.GeekMail.instance
import me.Geek.Modules.MailManage
import me.Geek.api.mail.Mail
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerPickupItemEvent

import org.bukkit.plugin.Plugin

/**
 * 作者: 老廖
 * 时间: 2022/8/6
 */
class Mview(
    private val player: Player,
    private val mail: Mail
) {
    private val plugin: Plugin = instance
    private val inv = Bukkit.createInventory(player, 9, mail.title+" §0§l的附件")
    init {
        main()
    }
    private fun main() {
        GeekMail.menu.isOpen.add(player)
        inv.contents = mail.itemStacks
        player.openInventory(inv)
        MailManage.Sound(player, "BLOCK_NOTE_BLOCK_HARP",1f, 1f)
        Bukkit.getPluginManager().registerEvents(object : Listener {
            @EventHandler
            fun onClick(e: InventoryClickEvent) {
                if (player === e.whoClicked) {
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
            fun onPickup(e: PlayerPickupItemEvent) {
                if (player === e.player) {
                    e.isCancelled = true
                }
            }

            @EventHandler
            fun onClose(e: InventoryCloseEvent) {
                if (player == e.player) {
                    GeekMail.menu.isOpen.removeIf { it == player }
                    HandlerList.unregisterAll(this)
                    Bukkit.getPluginManager().callEvent(PlayerCommandPreprocessEvent(player, GeekMail.menu.cmd))
                }
            }
        }, plugin)
    }
}