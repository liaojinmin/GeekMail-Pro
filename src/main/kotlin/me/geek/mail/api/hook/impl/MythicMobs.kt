package me.geek.mail.api.hook.impl

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
    private val mythic = try {
            io.lumine.xikage.mythicmobs.MythicMobs.inst()
        } catch (_: NoClassDefFoundError) {
            null
        } catch (_: NoSuchMethodException) {
            null
        }


    init {
        hook()
    }

    private fun hook() {
        if (Bukkit.getPluginManager().getPlugin("MythicMobs") != null) {
            GeekMail.say("&7软依赖 &fMythicMobs &7已兼容.")
            if (mythic != null) {
                isHook = true
            }
        }
    }

    fun getItem(id: String): ItemStack {
        if (isHook && mythic != null) {
            return mythic.itemManager.getItemStack(id)
        } else error("暂不接入次版本 MythicMobs")
    }
}