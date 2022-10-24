package me.geek.mail.api.hook.impl

import ink.ptms.um.Mythic
import me.geek.mail.GeekMail
import org.bukkit.Bukkit
import org.bukkit.inventory.ItemStack
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.buildItem

/**
 * 作者: 老廖
 * 时间: 2022/10/24
 *
 **/
class MythicMobs {
    private val empty = buildItem(XMaterial.STONE) { name = "错误的物品命名" }
    var isHook = false

    init {
        hook()
    }

    private fun hook() {
        if (Bukkit.getPluginManager().getPlugin("MythicMobs") != null) {
            GeekMail.say("&7软依赖 &fMythicMobs &7已兼容.")
            isHook = true
        }
    }

    fun getItem(id: String): ItemStack {
        return Mythic.API.getItemStack(id) ?: empty
    }
}