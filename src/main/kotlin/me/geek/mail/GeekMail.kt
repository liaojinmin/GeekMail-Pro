package me.geek.mail


import me.geek.mail.api.hook.HookPlugin
import me.geek.mail.api.mail.MailManage
import me.geek.mail.common.data.Database
import me.geek.mail.common.customevent.Event
import me.geek.mail.common.data.Task
import me.geek.mail.common.menu.Menu
import me.geek.mail.common.template.Template
import me.geek.mail.modules.*
import me.geek.mail.modules.settings.SetTings
import me.geek.mail.utils.colorify
import org.bukkit.Bukkit
import taboolib.common.env.RuntimeDependencies
import taboolib.common.env.RuntimeDependency
import taboolib.common.platform.Platform
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.console
import taboolib.module.configuration.Config
import taboolib.module.configuration.ConfigFile
import taboolib.module.metrics.Metrics
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

    const val VERSION = 2.05

    val BukkitVersion by lazy { Bukkit.getVersion().substringAfter("MC:").filter { it.isDigit() }.toInt() }

    var plugin_status: Boolean = false // 插件状态

    val DataManage by lazy { Database() } // 数据库管理器


    override fun onLoad() {
        Metrics(16437, VERSION.toString(), Platform.BUKKIT)
        console().sendMessage("")
        console().sendMessage("正在加载 §3§lGeekMail  §f...  §8" + Bukkit.getVersion())
        console().sendMessage("")
    }

    override fun onEnable() {
        runLogo()

        config.onReload { SetTings.onLoadSetTings() }
        SetTings.onLoadSetTings() // 插件配置加载


        Event.onloadEventPack() // 自定义事件加载

        Template.onLoad() // 邮件模板加载

        DataManage.start() // 启动数据库

        HookPlugin.onHook() // 挂钩软依赖



        register() // 注册邮件类型
        plugin_status = true
        if (SetTings.UseExpiry) Task()
    }


    override fun onDisable() {
        plugin_status = false

        Menu.closeGui()

        DataManage.closeData()
    }




    @JvmStatic
    fun say(msg: String) {
        if (BukkitVersion >= 1160)
            console().sendMessage("&8[<g#2:#FFB5C5:#EE0000>GeekMail&8] &7$msg".colorify())
        else
            console().sendMessage("§8[§6GeekMail§8] ${msg.replace("&", "§")}")
    }
    @JvmStatic
    fun debug(msg: String) {
        if(SetTings.DeBug) {
            if (BukkitVersion >= 1160)
                console().sendMessage("&8[<g#2:#FFB5C5:#EE0000>GeekMail&8] &cDeBug &8| &7$msg".colorify())
            else
                console().sendMessage("§8[§6GeekMail§8] ${msg.replace("&", "§")}")
        }
    }


    private fun register() {
        measureTimeMillis {
            MailManage.register(Mail_Exp())
            MailManage.register(Mail_Money())
            MailManage.register(Mail_Points())
            MailManage.register(Mail_Text())
            MailManage.register(Mail_Item())
            MailManage.register(Mail_Cmd())
            MailManage.register(Mail_Normal())
        }.also {
            say("&7已注册 &f${MailManage.getMailDataMap().size} &7种邮件类型... §8(耗时 $it Ms)")
        }
    }

    private fun runLogo() {
        console().sendMessage(" ________               __      _____         .__.__           __________ ")
        console().sendMessage(" /  _____/  ____   ____ |  | __ /     \\ _____  |__|  |          \\______   \\______")
        console().sendMessage("/   \\  ____/ __ \\_/ __ \\|  |/ //  \\ /  \\\\__  \\ |  |  |    ______ |     ___/\\_  __ \\/  _ \\")
        console().sendMessage("\\    \\_\\  \\  ___/\\  ___/|    </    Y    \\/ __ \\|  |  |__ /_____/ |    |     |  | \\(  <_> )")
        console().sendMessage(" \\______  /\\___  >\\___  >__|_ \\____|__  (____  /__|____/         |____|     |__|   \\____/ ")
        console().sendMessage("        \\/     \\/     \\/     \\/       \\/     \\/   ")
        console().sendMessage("")
        console().sendMessage("       §aGeekMail§8-§6Pro  §bv$VERSION §7by §awww.geekcraft.ink")
        console().sendMessage("       §8适用于Bukkit: §71.12.2-1.19.2 §8当前: §7 ${Bukkit.getServer().version}")
        console().sendMessage("")
    }
}