package me.geek.mail.modules.settings

import me.geek.mail.GeekMail
import me.geek.mail.modules.settings.sub.SetManager
import me.geek.mail.modules.settings.sub.redis.RedisData
import me.geek.mail.modules.settings.sub.smtp.SmtpData
import me.geek.mail.modules.settings.sub.storage.StorageDate
import me.geek.mail.utils.colorify
import org.bukkit.Bukkit
import org.bukkit.Location
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.expansion.geek.Expiry
import taboolib.module.configuration.Config
import taboolib.module.configuration.ConfigFile
import taboolib.module.configuration.Configuration.Companion.getObject
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.system.measureTimeMillis

/**
 * 作者: 老廖
 * 时间: 2022/9/14
 *
 **/
object SetTings {
    private val SetTingsCache = ConcurrentHashMap<String, SetManager>()

    @Config(value = "settings.yml", autoReload = true)
    lateinit var config: ConfigFile
        private set

    @Awake(LifeCycle.ACTIVE)
    fun init() {
        config.onReload { onLoadSetTings() }
    }

    @Synchronized
    fun onLoadSetTings() {
        measureTimeMillis {
            val data = config.getObject<StorageDate>("data_storage", false)
            val smtp = config.getObject<SmtpData>("SmtpSet", false)
            val redis = config.getObject<RedisData>("Redis", false)


            SetTingsCache["config"] = SetManager(data, smtp, redis)
            onLoadConf()
            onLoadType()
        }
    }
    private fun getConfig(): SetManager {
        return SetTingsCache["config"]!!
    }

    val StorageDate by lazy {
        getConfig().storageDate
    }
    val SmtpData by lazy {
        getConfig().SmtpData
    }
    val redisData by lazy {
        getConfig().redisData
    }


    /**
     * config
     */
    var DeBug: Boolean = false

    var SMTP_SET: Boolean = false

    var USE_BUNDLE: Boolean = false

    var UseExpiry: Boolean = false

    var ExpiryAuto: Int = 0


    val ExpiryTime by lazy { Expiry.getExpiryMillis(config.getString("config.Expiry.time") ?: "2d", false) * 1000 }

    val Console: UUID = UUID.fromString("00000000-0000-0000-0000-000000000001")

    private fun onLoadConf() {
        DeBug = config.getBoolean("debug", false)
        SMTP_SET = SmtpData.start
        USE_BUNDLE = if (GeekMail.BukkitVersion >= 1170) config.getBoolean("config.use_bundle") else false
        UseExpiry = config.getBoolean("config.Expiry.use")
        ExpiryAuto = config.getInt("config.Expiry.auto", 600)
    }


    lateinit var MONEY_MAIL: String
        private set
    lateinit var POINTS_MAIL: String
        private set
    lateinit var EXP_MAIL: String
        private set
    lateinit var TEXT_MAIL : String
        private set
    lateinit var CMD_MAIL: String
        private set
    lateinit var ITEM_MAIL: String
        private set

    var location: Location? = null

    private fun onLoadType() {
        // 邮件种类转换语言
        MONEY_MAIL = config.getString("MailType.MONEY_MAIL.tag")?.colorify() ?: "§e金币"
        POINTS_MAIL = config.getString("MailType.POINTS_MAIL.tag")?.colorify() ?: "§b点券"
        EXP_MAIL = config.getString("MailType.EXP_MAIL.tag")?.colorify() ?: "§a经验"
        TEXT_MAIL = config.getString("MailType.TEXT_MAIL.tag")?.colorify() ?: "§f文本"
        CMD_MAIL = config.getString("MailType.CMD_MAIL.tag")?.colorify() ?: "§c系统"
        ITEM_MAIL = config.getString("MailType.ITEM_MAIL.tag")?.colorify() ?: "§6物品"

        location = config.getString("Block.loc")?.split(",")?.let {
            Location(Bukkit.getWorld(it[0]), it[1].toDouble(), it[2].toDouble(), it[3].toDouble()) }
    }

}