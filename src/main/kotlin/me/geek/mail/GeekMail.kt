package me.geek.mail

import me.geek.mail.Configuration.ConfigManager
import me.geek.mail.modules.*
import me.geek.mail.common.DataBase.DataManage
import me.geek.mail.common.menu.Menu
import me.geek.mail.common.template.Template
import me.geek.mail.api.mail.MailManage
import me.geek.mail.api.hook.hookPlugin
import me.geek.mail.utils.ChineseMaterial
import me.geek.mail.utils.colorify
import org.bukkit.Bukkit
import taboolib.common.env.RuntimeDependencies
import taboolib.common.env.RuntimeDependency
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.console
import taboolib.platform.BukkitPlugin


@RuntimeDependencies(
    RuntimeDependency(value = "!com.zaxxer:HikariCP:4.0.3",
        relocate = ["!com.zaxxer.hikari",
            "!com.zaxxer.hikari_4_0_3_mail"]),
    RuntimeDependency(value = "!javax.mail:mail:1.5.0-b01",
        relocate = ["!javax.mail", "!javax.mail_1_5_0_mail"],
        repository = "https://repo1.maven.org/maven2"
    ),
    RuntimeDependency(value = "!javax.activation:activation:1.1.1",
     //   relocate = ["!javax.activation", "!javax.activation_1_1_1_mail"],
        repository = "https://repo1.maven.org/maven2"
    ),
)
object GeekMail : Plugin() {

    val instance by lazy { BukkitPlugin.getInstance() }
    const val VERSION = 1.2
    val BukkitVersion = Bukkit.getVersion().substringAfter("MC:").filter { it.isDigit() }.toInt()
    var plugin_status: Boolean = false
    val Materials = ChineseMaterial()

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
        ConfigManager.Load() // 加载配置文件
        DataManage.start() // 启动数据库
        register() // 注册邮件类型
        Template.onLoad() // 邮件模板加载

        hookPlugin.onHook()
        if (Bukkit.getPluginManager().getPlugin("ItemsAdder") == null) {
            Menu.loadMenu()
        }
    }


    override fun onDisable() {
        plugin_status = false
        Menu.closeGui()
        DataManage.closeData()
    }




    @JvmStatic
    fun say(msg: String) {
        if (BukkitVersion >= 1160)
            console().sendMessage("&8[<g#2:#FF00FF:#FFFAFA>GeekMail&8] &7$msg".colorify())
        else
            console().sendMessage("§8[§6GeekMail§8] ${msg.replace("&", "§")}")
    }
    fun title(msg: String) {
        console().sendMessage(msg)
    }
    @JvmStatic
    fun debug(msg: String) {
        if(ConfigManager.DeBug) {
            if (BukkitVersion >= 1160)
                console().sendMessage("&8[<g#2:#FF00FF:#FFFAFA>GeekMail&8] &cDeBug &8| &7$msg".colorify())
            else
                console().sendMessage("§8[§6GeekMail§8] ${msg.replace("&", "§")}")
        }
    }

    private fun register() {
        MailManage.register(Mail_Exp())
        MailManage.register(Mail_Money())
        MailManage.register(Mail_Points())
        MailManage.register(Mail_Text())
        MailManage.register(Mail_Item())
        MailManage.register(Mail_Cmd())
        say("&7已注册 &f${MailManage.getMailDataMap().size} &7种邮件类型...")
    }

}