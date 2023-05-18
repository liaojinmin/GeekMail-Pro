package me.geek.mail.common.menu

import me.geek.mail.GeekMail
import me.geek.mail.api.hook.HookPlugin
import me.geek.mail.common.menu.action.MailMenu
import me.geek.mail.common.menu.action.MarketManager
import me.geek.mail.common.menu.sub.Icon
import me.geek.mail.common.menu.sub.IconType
import me.geek.mail.common.menu.sub.MenuData
import me.geek.mail.common.menu.sub.MenuType
import me.geek.mail.utils.colorify
import me.geek.mail.utils.forFile
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.releaseResourceFile
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.SecuredFile
import taboolib.module.configuration.util.getMap
import java.io.File
import java.util.*
import kotlin.system.measureTimeMillis

/**
 * 作者: 老廖
 * 时间: 2022/7/23
 */
object Menu {
    private val AIR = ItemStack(Material.AIR)

    // start 已打开的会话界面缓存
    val isOpen: MutableList<Player> = mutableListOf()
    val SessionCache: MutableMap<Player, MenuBasic> = mutableMapOf()
    // end

    /**
     * 菜单配置缓存
     */
    // key = 菜单文件名称 , value = 菜单数据
    private val MenuCache: MutableMap<String, MenuData> = HashMap()
    // 缓存的菜单打开指令 key = 菜单绑定的命令  value = 菜单文件名称
    private val MenuCmd: MutableMap<String, String> = HashMap()
    var mainMenu: String = ""


    fun Player.openMenu(cmd: String): Boolean {
        val data = MenuCmd[cmd] ?: return false
        val menu = MenuCache[data] ?: return false
        this.openMenu(menu)
        playSound(location, Sound.UI_BUTTON_CLICK, 1f, 2f)
        return true
    }

    fun closeMenu(type: MenuType) {
        SessionCache.values.forEach {
            if (it.menuData?.menuType == type) {
                it.player.closeInventory()
            }
        }
    }
    fun getMenuData(type: MenuType): MenuData {
        return MenuCache.values.find { it.menuType == type } ?: error("未找到该类型菜单，请检查代码或类型相关开发者。。。")
    }
    fun Player.openMenu(data: MenuData) {
        when (data.menuType) {
            MenuType.MAIN -> MailMenu(this, data).build()
            MenuType.MARKET -> me.geek.mail.common.menu.action.MarketMenu(this, data).build()
            MenuType.MARKET_BUY -> TODO()
            MenuType.MARKET_MANAGER -> MarketManager(this).build()
        }
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
            list.forEach { file ->
                val icon = mutableListOf<Icon>()
                val menu: SecuredFile = SecuredFile.loadConfiguration(file)
                val menuTag: String = file.name.substring(0, file.name.indexOf("."))
                val title: String = menu.getString("TITLE")!!.colorify()
                val bindings: String = menu.getString("Bindings.Commands") ?: ""
                MenuCmd[bindings] = menuTag
                val type: MenuType = MenuType.valueOf(menu.getString("TYPE")!!.also {
                    if (it.contains("main", ignoreCase = true)) mainMenu = bindings
                }.uppercase(Locale.ROOT))
                val size: Int = menu.getStringList("Layout").size * 9
                val layout: MutableList<Char> = mutableListOf<Char>().apply {
                    menu.getStringList("Layout").forEach {
                        it.indices.forEach { index -> add(it[index]) }
                    }
                }
                menu.getMap<String, ConfigurationSection>("Icons").forEach { (name, obj) ->
                    icon.add(Icon(name[0], obj))
                }
                val items = arrayListOf<ItemStack>()

                val listIcon: MutableMap<Char, Icon> = mutableMapOf<Char, Icon>().apply {
                    layout.forEachIndexed { _, value ->
                        if (value != ' ') {
                            icon.forEach { ic ->
                                if (ic.icon == value) {
                                    items.add(buildItems(ic))
                                    this[value] = ic
                                }
                            }
                        } else items.add(AIR)
                    }
                }
                MenuCache[menuTag] = MenuData(menuTag, type, title, bindings, layout, size, listIcon, items.toTypedArray())
            }
        }.also {
            GeekMail.say("§7菜单界面加载完成... §8(耗时 $it ms)");
        }
    }

    private fun buildItems(icon: Icon): ItemStack {
        return when {
            icon.iconType == IconType.TEXT -> AIR
            icon.iconType == IconType.MARKET_ITEM -> AIR
            icon.mats.contains(ia) -> {
                if (HookPlugin.itemsAdder.isHook) {
                    val meta = icon.mats.split(":")
                    val itemStack = HookPlugin.itemsAdder.getItem(meta[1])
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
                    itemStack
                } else {
                    ItemStack(Material.STONE, 1)
                }
            }
            else -> {
                val itemStack = try {
                    ItemStack(Material.valueOf(icon.mats.uppercase()), 1, icon.data.toShort())
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
                itemStack
            }

        }
    }
    private val ia = Regex("(IA|ia|ItemsAdder):")


    private val saveDefaultMenu by lazy {
        val dir = File(GeekMail.instance.dataFolder, "menu")
        if (!dir.exists()) {
            arrayOf(
                "menu/def.yml",
                "menu/Market.yml",
                "menu/MarketBuy.yml",
            ).forEach { releaseResourceFile(it, true) }
        }
        dir
    }
}