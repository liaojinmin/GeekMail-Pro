package me.geek.mail.api.hook.impl

import me.geek.mail.GeekMail
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

/**
 * 作者: 老廖
 * 时间: 2022/10/9
 *
 **/
class Money {
    private var economy: Economy? = null

    init {
        hook()
    }

    private fun hook() {
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            Bukkit.getServer().servicesManager.getRegistration(Economy::class.java)?.let {
                GeekMail.say("&7软依赖 &fVault &7已兼容.")
                this.economy = it.provider
            }
        }
    }



    fun giveMoney(player: Player, amt: Double) {
        economy?.depositPlayer(player, amt) ?: GeekMail.say("&c经济 &fVault &c未实现，请检查经济插件！")
    }
    fun giveMoney(player: OfflinePlayer, amt: Double) {
        economy?.depositPlayer(player, amt) ?: GeekMail.say("&c经济 &fVault &c未实现，请检查经济插件！")
    }

    fun takeMoney(player: Player, amt: Double) {
        economy?.withdrawPlayer(player, amt) ?: GeekMail.say("&c经济 &fVault &c未实现，请检查经济插件！")
    }

    fun hasMoney(player: Player, amt: Double): Boolean {
        return economy?.has(player, amt) ?: false.also { GeekMail.say("&c经济 &fVault &c未实现，请检查经济插件！") }
    }

    fun hasTakeMoney(player: Player, amt: Double): Boolean {
        return if (hasMoney(player, amt)) {
            takeMoney(player, amt)
            true
        } else false
    }
}