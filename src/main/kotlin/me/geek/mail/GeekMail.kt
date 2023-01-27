package me.geek.mail


import me.geek.mail.api.data.SqlManage
import me.geek.mail.api.hook.HookPlugin
import me.geek.mail.api.mail.MailManage
import me.geek.mail.common.customevent.Event
import me.geek.mail.common.market.Market
import me.geek.mail.common.menu.Menu
import me.geek.mail.common.settings.SetTings
import me.geek.mail.common.template.Template
import me.geek.mail.modules.*
import me.geek.mail.utils.colorify
import org.bukkit.Bukkit
import taboolib.common.env.DependencyScope
import taboolib.common.env.RuntimeDependencies
import taboolib.common.env.RuntimeDependency
import taboolib.common.platform.Platform
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.console
import taboolib.module.metrics.Metrics
import taboolib.platform.BukkitPlugin
import test
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
    RuntimeDependency(value = "org.apache.commons:commons-pool2:2.11.1",
        test = "org.apache.commons.pool2.impl.GenericObjectPoolConfig",
        transitive = false, ignoreOptional = true, scopes = [DependencyScope.PROVIDED],
    ),
    RuntimeDependency(value = "redis.clients:jedis:4.2.2",
        test = "redis.clients.jedis.exceptions.JedisException",
        transitive = false, ignoreOptional = true, scopes = [DependencyScope.PROVIDED]
    ),
    RuntimeDependency(value = "!com.google.code.gson:gson:2.9.1",
        relocate = ["!com.google.gson", "!com.google.gson2_9_1"],
    transitive = false, ignoreOptional = true, scopes = [DependencyScope.PROVIDED]
    ),
    RuntimeDependency(value = "org.xerial.snappy:snappy-java:1.1.8.4",
        transitive = true, ignoreOptional = false
       // repository = "https://repo1.maven.org/maven2",
    ),
)
object GeekMail : Plugin() {


    val instance by lazy { BukkitPlugin.getInstance() }


    const val VERSION = 3.0

    val BukkitVersion by lazy { Bukkit.getVersion().substringAfter("MC:").filter { it.isDigit() }.toInt() }

    var plugin_status: Boolean = false // 插件状态



    override fun onLoad() {
        test.main(emptyArray())
        Metrics(16437, VERSION.toString(), Platform.BUKKIT)
        console().sendMessage("")
        console().sendMessage("正在加载 §3§lGeekMail  §f...  §8" + Bukkit.getVersion())
        console().sendMessage("")
    }

    override fun onEnable() {
        runLogo()

        SetTings.onLoadSetTings() // 插件配置加载

        SqlManage.start() // 启动数据库

        Market.loadItem() // 加载市场商品

        Event.onloadEventPack() // 自定义事件加载

        register() // 注册邮件类型

        HookPlugin.onHook() // 挂钩软依赖

    }
    override fun onActive() {

        Template.onLoad()

        plugin_status = true // 更改插件状态
    }


    override fun onDisable() {
        SqlManage.saveAllData()
        plugin_status = false

        Menu.closeGui()

        SqlManage.closeData()
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
            say("&7已注册 &f${MailManage.getMailTypeKeyMap().size} &7种邮件类型... §8(耗时 $it Ms)")
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