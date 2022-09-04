package me.geek.mail

import me.geek.mail.Configuration.ConfigManager
import me.geek.mail.Configuration.LangManager
import me.geek.mail.Modules.*
import me.geek.mail.api.utils.ChineseMaterial.MaterialChinese
import me.geek.mail.common.DataBase.DataManage
import me.geek.mail.common.Menu.Menu
import me.geek.mail.common.Template.Template
import me.geek.mail.api.mail.MailManage
import me.geek.mail.api.utils.HexUtils.colorify
import me.geek.mail.api.hook.hookPlugin
import org.bukkit.Bukkit
import taboolib.common.env.RuntimeDependencies
import taboolib.common.env.RuntimeDependency
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.console
import taboolib.platform.BukkitPlugin
import java.util.*


@RuntimeDependencies(
    RuntimeDependency(
        value = "!com.zaxxer:HikariCP:4.0.3",
        relocate = ["!com.zaxxer.hikari", "!com.zaxxer.hikari_4_0_3_mail"],
        ),
)
object GeekMail : Plugin() {

    val instance by lazy { BukkitPlugin.getInstance() }
    const val VERSION = 1.2
    val BukkitVersion = Bukkit.getVersion().substringAfter("MC:").filter { it.isDigit() }.toInt()
    var plugin_status: Boolean = false
    lateinit var menu: Menu

    override fun onLoad() {
        title("")
        title("正在加载 §3§lGeekMail  §f...  §8" + Bukkit.getVersion())
        title("")
        MaterialChinese()
    }

    override fun onEnable() {
        plugin_status = true
        title("")
        title("     §aGeekMail  §bv$VERSION §7by §awww.geekcraft.ink")
        title("     §8适用于Bukkit: §71.12.2-1.18.2 §8当前: §7" + Bukkit.getName())
        title("")
        ConfigManager.Load() // 加载配置文件
        LangManager.onload() // 加载语言文件
        DataManage.start() // 启动数据库
        register() // 注册邮件类型
        Template.onLoad() // 邮件模板加载

        hookPlugin.onHook()
        if (Bukkit.getPluginManager().getPlugin("ItemsAdder") == null) {
            menu = Menu()
        }
    }


    override fun onDisable() {
        plugin_status = false
        menu.CloseGui()
        DataManage.closeData()
    }




    @JvmStatic
    fun say(msg: String) {
        if (BukkitVersion >= 1160)
            console().sendMessage(colorify("&8[<g#2:#FF00FF:#FFFAFA>GeekMail&8] &7$msg"))
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
                console().sendMessage(colorify("&8[<g#2:#FF00FF:#FFFAFA>GeekMail&8] &cDeBug &8| &7$msg"))
            else
                console().sendMessage("§8[§6GeekMail§8] ${msg.replace("&", "§")}")
        }
    }

    private fun register() {
        MailManage.register(Mail_Exp(
            UUID.fromString("00000000-0000-0000-0000-000000000001"),
            "邮件标题",
            "邮件文本",
            UUID.fromString("00000000-0000-0000-0000-000000000001"),
            UUID.fromString("00000000-0000-0000-0000-000000000001"),
            "未领取",
            "0"))
        MailManage.register(Mail_Money(
            UUID.fromString("00000000-0000-0000-0000-000000000001"),
            "邮件标题",
            "邮件文本",
            UUID.fromString("00000000-0000-0000-0000-000000000001"),
            UUID.fromString("00000000-0000-0000-0000-000000000001"),
            "未领取",
            "0"))
        MailManage.register(Mail_Points(
            UUID.fromString("00000000-0000-0000-0000-000000000001"),
            "邮件标题",
            "邮件文本",
            UUID.fromString("00000000-0000-0000-0000-000000000001"),
            UUID.fromString("00000000-0000-0000-0000-000000000001"),
            "未领取",
            "0"))
        MailManage.register(Mail_Text(
            UUID.fromString("00000000-0000-0000-0000-000000000001"),
            "邮件标题",
            "邮件文本",
            UUID.fromString("00000000-0000-0000-0000-000000000001"),
            UUID.fromString("00000000-0000-0000-0000-000000000001"),
            "未领取",
            "0"))
        MailManage.register(Mail_Item(
            UUID.fromString("00000000-0000-0000-0000-000000000001"),
            "邮件标题",
            "邮件文本",
            UUID.fromString("00000000-0000-0000-0000-000000000001"),
            UUID.fromString("00000000-0000-0000-0000-000000000001"),
            "未领取",
            "0"))
        say("&7已注册 &f${MailManage.getMailDataMap().size} &7种邮件类型...")
    }

}