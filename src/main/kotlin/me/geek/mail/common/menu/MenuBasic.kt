package me.geek.mail.common.menu

import me.geek.mail.GeekMail
import me.geek.mail.api.hook.HookPlugin
import me.geek.mail.common.menu.sub.Icon
import me.geek.mail.utils.colorify
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import taboolib.library.xseries.XSound
import taboolib.platform.compat.replacePlaceholder

/**
 * 作者: 老廖
 * 时间: 2023/1/19
 *
 **/
abstract class MenuBasic: MenuHeader {
    abstract fun build(): MenuBasic

    override var page = 0
    override var title: String = "§6GeekMail-Pro"
    override var size: Int = 54

    val contents: MutableList<Array<ItemStack>> = ArrayList()

    var isLock: Boolean = true

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
        this.player.openInventory(this.inventory)
        Menu.SessionCache[this.player] = this
        Menu.isOpen.add(this.player)
        if (contents.size != 0) {
            this.inventory.contents = this.contents[0]
        }
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
    fun buildItem(icon: Icon): ItemStack {
        return try {
            val item: ItemStack =
                if (icon.mats.contains("IA:", ignoreCase = true) && HookPlugin.itemsAdder.isHook) {
                    HookPlugin.itemsAdder.getItem(icon.mats.substring(3))
                } else ItemStack(Material.valueOf(icon.mats))
            val itemMeta = item.itemMeta

            if (itemMeta != null) {
                itemMeta.setDisplayName(icon.name.colorify())
                itemMeta.lore = icon.lore.replacePlaceholder(this.player)
                item.itemMeta = itemMeta
            }
            item
        } catch (ing: IllegalArgumentException) {
            ItemStack(Material.BOOK, 1)
            error("错误的邮件图标配置")
        }
    }

}