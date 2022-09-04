package me.geek.mail.api.hook


import net.milkbowl.vault.economy.Economy
import org.black_ixx.playerpoints.PlayerPointsAPI
import dev.lone.itemsadder.api.CustomStack
import me.geek.mail.GeekMail.say

import org.black_ixx.playerpoints.PlayerPoints
import org.bukkit.Bukkit
import org.bukkit.inventory.ItemStack

/**
 * 作者: 老廖
 * 时间: 2022/8/1
 */
object hookPlugin {

    lateinit var money: Economy
    lateinit var points: PlayerPointsAPI

    @JvmStatic
    fun getItemsAdder(id: String?): ItemStack {
        return CustomStack.getInstance(id)!!.itemStack
    }

    fun onHook() {
        hookEconomy()
        hookPlayerPoints()
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
}