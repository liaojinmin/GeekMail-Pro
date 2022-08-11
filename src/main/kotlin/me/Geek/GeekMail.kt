package me.Geek

import me.Geek.Configuration.ConfigManager
import me.Geek.Configuration.LangManager
import me.Geek.Libs.DataBase.DataManage
import me.Geek.Libs.Menu.Menu
import me.Geek.Libs.Template.Template
import me.Geek.Modules.MailManage
import me.Geek.api.Bukkit.MaterialChinese
import me.Geek.api.hook.hookPlugin
import org.bukkit.Bukkit
import taboolib.common.env.RuntimeDependencies
import taboolib.common.env.RuntimeDependency
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.console
import taboolib.module.nms.MinecraftVersion
import taboolib.platform.BukkitPlugin


@RuntimeDependencies(
    RuntimeDependency(
        value = "!com.zaxxer:HikariCP:4.0.3",
        relocate = ["!com.zaxxer.hikari", "!com.zaxxer.hikari_4_0_3"],
        repository = "https://repo.tabooproject.org/repository/releases"),
)
object GeekMail : Plugin() {

    val instance by lazy { BukkitPlugin.getInstance() }
    public const val VERSION = 1.06
    var plugin_status: Boolean = false
    lateinit var menu: Menu
    lateinit var lang: MaterialChinese

    override fun onLoad() {
        title("")
        title("正在加载 §3§lGeekMail  §f...  §8" + Bukkit.getVersion())
        title("")
    }

    override fun onEnable() {
        plugin_status = true
        title("")
        title("     §aGeekMail  §bv$VERSION §7by §awww.geekcraft.ink")
        title("     §8适用于Bukkit: §71.12.2-1.18.2 §8当前: §7" + Bukkit.getName())
        title("")
        ConfigManager.Load()
        LangManager.onload()
        DataManage.start()

        Template.onLoad()
        lang = MaterialChinese()
        if (Bukkit.getPluginManager().getPlugin("ItemsAdder") == null) {
            menu = Menu()
        }
        hookPlugin.onHook()
    }


    override fun onDisable() {
        plugin_status = false
        menu.CloseGui()
        DataManage.closeData()
    }




        @JvmStatic
        fun say(msg: String) {
            console().sendMessage("§8[§6§lGeekMail§8] ${msg.replace("&", "§")}")
        }

        fun title(msg: String) {
            console().sendMessage(msg)
        }

}