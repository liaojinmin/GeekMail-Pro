package me.geek.mail.common.menu

import me.geek.mail.api.hook.hookPlugin.getItemsAdder
import me.geek.mail.common.menu.sub.Session
import me.geek.mail.common.menu.sub.Micon
import me.geek.mail.GeekMail
import me.geek.mail.common.menu.sub.IconType
import me.geek.mail.utils.colorify
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.releaseResourceFile
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.SecuredFile
import taboolib.module.configuration.util.getMap
import java.io.File
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.system.measureTimeMillis

/**
 * 作者: 老廖
 * 时间: 2022/7/23
 */
object Menu {
    private val AIR = ItemStack(Material.AIR)
    // key = 菜单名称 , value = 会话菜单
    private val MenuCache: MutableMap<String, Session> = HashMap()
    // 缓存的菜单打开指令 key = 菜单绑定的命令  value = 菜单名称
    private val MenuCmd: MutableMap<String, String> = HashMap()
    var cmd: String? = null

    @JvmField
    val isOpen: MutableList<Player> = ArrayList()


    /**
     * 为玩家构建指定页数的的界面
     * @param player 目标玩家
     * @param MenuTag 菜单标签
     * @return 返回界面
     */
    @JvmStatic
    fun Build(player: Player?, MenuID: String): Inventory {
        val tag = MenuCache[MenuID]!!
        val item = tag.itemStacks
        val inventory = Bukkit.createInventory(player, tag.size, tag.title)
        if (item.isNotEmpty()) {
            inventory.contents = item
        }
        return inventory
    }

    fun getSession(MenuID: String): Session {
        return MenuCache[MenuID]!!
    }
    fun getMenuCommand(MenuID: String): String? {
        return MenuCmd[MenuID]
    }

    fun closeGui() {
        Bukkit.getOnlinePlayers().forEach { player: Player ->
            if (isOpen.contains(player)) {
                player.closeInventory()
            }
        }
    }

    fun onReload() {
        MenuCache.clear()
        MenuCmd.clear()
        isOpen.clear()
        loadMenu()
    }
    fun loadMenu() {
        val list = mutableListOf<File>()
        measureTimeMillis {
            list.also {
                it.addAll(forFile(saveDefaultMenu))
            }
            val icon = mutableListOf<Micon>()
            var menu: SecuredFile
            var menuTag: String
            var title: String
            var type: String
            var layout: String
            var size: Int
            var bindings: String
            list.forEach { file ->
                icon.clear()
                menu = SecuredFile.loadConfiguration(file)
                menuTag = file.name.substring(0, file.name.indexOf("."))
                title = menu.getString("TITLE")!!.colorify()
                type = menu.getString("TYPE")!!
                layout = menu.getStringList("Layout").toString()
                    .replace("[", "")
                    .replace("]", "")
                    .replace(", ", "")
                size = menu.getStringList("Layout").size * 9
                bindings = menu.getString("Bindings.Commands")!!
                menu.getMap<String, ConfigurationSection>("Icons").forEach { (name, obj) ->
                    icon.add(Micon(name, obj))
                }
                val listIcon = ArrayList(icon)
                MenuCache[menuTag] = Session(menuTag, title, layout, size, bindings, listIcon, type, builds(listIcon, layout, size))
                MenuCmd[bindings] = menuTag
                cmd = bindings
            }
        }.also {
            GeekMail.say("§7菜单界面加载完成 §8(耗时$it ms)");
        }
    }


    private fun builds(var1: List<Micon>, Layout: String, size: Int): Array<ItemStack> {
        val item = mutableListOf<ItemStack>()
        try {
            var index = 0
            while (index < size) {
                if (Layout[index] != ' ') {
                    val IconID = Layout[index].toString()
                    item.add(index, item(IconID, var1))
                } else {
                    item.add(index, AIR)
                }
                index++
            }
        } catch (ignored: StringIndexOutOfBoundsException) {
        }
        return item.toTypedArray()
    }

    private fun item(iconID: String, miconObj: List<Micon>): ItemStack {
        for (icon in miconObj) {
            if (icon.icon == iconID) {

                if (icon.type == IconType.TEXT) {
                    return AIR
                }
                val itemStack = try {
                    if (icon.mats.contains("IA:")) {
                        val meta = icon.mats.split(":".toRegex()).toTypedArray()
                        getItemsAdder(meta[1])
                    } else {
                        ItemStack(Material.valueOf(icon.mats), 1, icon.data.toShort())
                    }
                } catch (ing: IllegalArgumentException) {
                    ItemStack(Material.STONE, 1)
                }
                val itemMeta = itemStack.itemMeta
                if (itemMeta != null) {
                    itemMeta.setDisplayName(icon.name.colorify())
                    if (icon.lore.size == 1 && icon.lore[0].isEmpty()) {
                        itemMeta.lore = null
                    } else {
                        itemMeta.lore = icon.lore
                    }
                    itemStack.itemMeta = itemMeta
                }
                return itemStack
            }
        }
        return AIR
    }

    private fun forFile(file: File): List<File> {
        return mutableListOf<File>().run {
            if (file.isDirectory) {
                file.listFiles()?.forEach {
                    addAll(forFile(it))
                }
            } else if (file.exists() && file.absolutePath.endsWith(".yml")) {
                add(file)
            }
            this
        }
    }

    private val saveDefaultMenu by lazy {
        val dir = File(GeekMail.instance.dataFolder, "menu")
        if (!dir.exists()) {
            arrayOf(
                "menu/def.yml",
            ).forEach { releaseResourceFile(it, true) }
        }
        dir
    }
    fun Player.openMenu(MenuID: String) = MAction(this, getSession(MenuID), Build(this, MenuID))
}