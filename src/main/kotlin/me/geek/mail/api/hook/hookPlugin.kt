package me.geek.mail.api.hook


import net.milkbowl.vault.economy.Economy
import org.black_ixx.playerpoints.PlayerPointsAPI
import dev.lone.itemsadder.api.CustomStack
import dev.lone.itemsadder.api.Events.ItemsAdderFirstLoadEvent
import me.arasple.mc.trhologram.api.base.ClickHandler
import me.arasple.mc.trhologram.api.hologram.HologramBuilder
import me.arasple.mc.trhologram.module.display.Hologram
import me.geek.mail.GeekMail
import me.geek.mail.GeekMail.say
import me.geek.mail.common.menu.Menu
import me.geek.mail.modules.settings.SetTings

import org.black_ixx.playerpoints.PlayerPoints
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.event.SubscribeEvent
import java.util.*

/**
 * 作者: 老廖
 * 时间: 2022/8/1
 */
object hookPlugin {

    lateinit var money: Economy
    lateinit var points: PlayerPointsAPI
    var id: Hologram? = null

    @JvmStatic
    fun getItemsAdder(id: String?): ItemStack {
        return CustomStack.getInstance(id)!!.itemStack
    }

    fun onHook() {
        hookEconomy()
        hookPlayerPoints()
        display()
    }

    private fun hookEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            val rsp = Bukkit.getServer().servicesManager.getRegistration(
                Economy::class.java
            )
            if (rsp != null) {
                say("&7软依赖 &fVault &7已兼容.")
                money = rsp.provider
            }
        }
    }

    private fun hookPlayerPoints() {
        val plugin = Bukkit.getServer().pluginManager.getPlugin("PlayerPoints")
        if (plugin != null) {
            say("&7软依赖 &fPlayerPoints &7已兼容.")
            val p = plugin as PlayerPoints
            points = p.api
        }
    }

    fun display() {
        if (Bukkit.getPluginManager().getPlugin("TrHologram") != null) {
            say("&7软依赖 &fTrHologram &7已兼容.")
            val loc = SetTings.location
            if (loc != null) {
                val list = GeekMail.config.getStringList("Block.hd")
                id?.destroy()
                val y = loc.y + 0.5
                val x = loc.x - -0.5
                val z = loc.z - -0.5
                id = HologramBuilder(
                    UUID.randomUUID().toString(),
                    Location(loc.world, x, y, z), 0.25, 32.0, null, -1, ClickHandler { _, _ -> }).apply {
                    for (out in list) {
                        this.append(out)
                    }
                }.build()
            }
            }
        }

    @SubscribeEvent
    fun onHook(e: ItemsAdderFirstLoadEvent) {
        say("&7软依赖 &fItemsAdder &7已兼容.")
        Menu.loadMenu()
    }
}