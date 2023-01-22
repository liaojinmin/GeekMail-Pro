package me.geek.mail.common.menu

import me.geek.mail.GeekMail

import org.bukkit.Bukkit

import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import taboolib.library.xseries.XSound
import java.util.ArrayList

/**
 * 作者: 老廖
 * 时间: 2023/1/19
 *
 **/
abstract class MenuBase: MenuHeader {
    abstract fun build(): MenuBase

    override var page = 0
    override var title: String = "§6GeekMail-Pro"
    override var size: Int = 54

    val contents: MutableList<Array<ItemStack>> = ArrayList()


    open val inventory: Inventory by lazy {
        this.menuData?.let {
            Bukkit.createInventory(this.player, it.size, it.title).apply {
                if (it.items.isNotEmpty()) {
                    this.contents = it.items
                }
            }
        } ?: Bukkit.createInventory(this.player, size, title)
    }

    /**
     * 打开菜单
     */
    override fun openMenu() {
        Menu.SessionCache[this.player] = this
        Menu.isOpen.add(this.player)
        if (contents.size != 0) {
            this.inventory.contents = this.contents[0]
        }
        this.player.openInventory(this.inventory)
    }



    fun sound(name: String, volume: Float, potch: Float) {
        val sound: XSound = try {
            XSound.valueOf(name)
        } catch (e: Throwable) {
            GeekMail.say("未知音效: $name")
            return
        }
        sound.play(player, volume, potch)
    }

}