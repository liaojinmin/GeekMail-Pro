package me.geek.mail.api.hook


import me.arasple.mc.trhologram.api.base.ClickHandler
import me.arasple.mc.trhologram.api.hologram.HologramBuilder
import me.arasple.mc.trhologram.module.display.Hologram
import me.geek.mail.GeekMail.say
import me.geek.mail.api.hook.impl.*
import me.geek.mail.settings.SetTings
import org.bukkit.Bukkit
import org.bukkit.Location
import java.util.*

/**
 * 作者: 老廖
 * 时间: 2022/8/1
 */
object HookPlugin {

    val money  by lazy { Money() }
    val points by lazy { Points() }
    val itemsAdder by lazy { ItemsAdder() }
    val mythicMobs by lazy { MythicMobs() }



    var id: Hologram? = null


    fun onHook() {
        money
        points
        itemsAdder
        mythicMobs
        display()
        hookPapi()
    }

    private fun hookPapi() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            say("&7软依赖 &fPlaceholderAPI &7已兼容.")
            Placeholder().register()
        }
    }


    fun display() {
        if (Bukkit.getPluginManager().getPlugin("TrHologram") != null) {
            say("&7软依赖 &fTrHologram &7已兼容.")
            val loc = SetTings.location
            if (loc != null) {
                val list = SetTings.config.getStringList("Block.hd")
                id?.destroy()
                val y = loc.y + 0.5
                val x = loc.x - -0.5
                val z = loc.z - -0.5
                id = HologramBuilder(
                    UUID.randomUUID().toString(),
                    Location(loc.world, x, y, z), 0.25, 32.0, null, -1, ClickHandler { _, _ -> }
                ).apply {
                    for (out in list) {
                        this.append(out)
                    }
                }.build()
            }
            }
        }

}