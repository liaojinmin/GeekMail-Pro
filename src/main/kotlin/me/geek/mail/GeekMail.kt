package me.geek.mail


import me.geek.mail.api.hook.hookPlugin
import me.geek.mail.api.mail.MailManage
import me.geek.mail.common.data.Database
import me.geek.mail.common.menu.Menu
import me.geek.mail.common.template.Template
import me.geek.mail.modules.*
import me.geek.mail.modules.settings.SetTings
import me.geek.mail.utils.ChineseMaterial
import me.geek.mail.utils.Expiry
import me.geek.mail.utils.colorify
import org.bukkit.Bukkit
import taboolib.common.env.RuntimeDependencies
import taboolib.common.env.RuntimeDependency
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.console
import taboolib.module.configuration.Config
import taboolib.module.configuration.ConfigFile
import taboolib.platform.BukkitPlugin
import kotlin.system.measureTimeMillis


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

    @Config(value = "settings.yml", autoReload = true)
    lateinit var config: ConfigFile
      private set

    val instance by lazy { BukkitPlugin.getInstance() }
    private const val VERSION = 2.01
    val BukkitVersion = Bukkit.getVersion().substringAfter("MC:").filter { it.isDigit() }.toInt()
    var plugin_status: Boolean = false

    val Materials = ChineseMaterial()
   // val DataManage = DataManager()
    val DataManage = Database()
    val expiry = Expiry()

    override fun onLoad() {
        console().sendMessage("")
        console().sendMessage("正在加载 §3§lGeekMail  §f...  §8" + Bukkit.getVersion())
        console().sendMessage("")
    }

    override fun onEnable() {
        title()
        config.onReload { SetTings.onLoad() }
        SetTings.onLoad()
        Template.onLoad() // 邮件模板加载
        DataManage.start() // 启动数据库
        hookPlugin.onHook()
        register() // 注册邮件类型
        if (Bukkit.getPluginManager().getPlugin("ItemsAdder") == null) {
            Menu.loadMenu()
        }
        plugin_status = true
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
    @JvmStatic
    fun debug(msg: String) {
        if(SetTings.DeBug) {
            if (BukkitVersion >= 1160)
                console().sendMessage("&8[<g#2:#FF00FF:#FFFAFA>GeekMail&8] &cDeBug &8| &7$msg".colorify())
            else
                console().sendMessage("§8[§6GeekMail§8] ${msg.replace("&", "§")}")
        }
    }
    private fun title() {
        console().sendMessage("")
        console().sendMessage("       §aGeekMail  §bv$VERSION §7by §awww.geekcraft.ink")
        console().sendMessage("       §8适用于Bukkit: §71.12.2-1.18.2 §8当前: §7")
        console().sendMessage("")
    }

    private fun register() {
        measureTimeMillis {
        MailManage.register(Mail_Exp())
        MailManage.register(Mail_Money())
            if (Bukkit.getServer().pluginManager.getPlugin("PlayerPoints") != null) MailManage.register(Mail_Points())
        MailManage.register(Mail_Text())
        MailManage.register(Mail_Item())
        MailManage.register(Mail_Cmd())
        }.also {
            say("&7已注册 &f${MailManage.getMailDataMap().size} &7种邮件类型... §8(耗时 $it Ms)")
        }
    }

}