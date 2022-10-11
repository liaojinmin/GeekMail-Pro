package me.geek.mail.modules.settings

import me.geek.mail.GeekMail
import me.geek.mail.GeekMail.config
import me.geek.mail.modules.settings.sub.SetManager
import me.geek.mail.modules.settings.sub.smtp.SmtpData
import me.geek.mail.modules.settings.sub.storage.StorageDate
import me.geek.mail.utils.colorify
import org.bukkit.Bukkit
import org.bukkit.Location
import taboolib.expansion.geek.Expiry
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

    @Synchronized
    fun onLoadSetTings() {
        measureTimeMillis {
            val data = config.getObject<StorageDate>("data_storage", false)
            val smtp = config.getObject<SmtpData>("SmtpSet", false)
            SetTingsCache["config"] = SetManager(data, smtp)
            onLoadType()
        }
    }
    fun getConfig(): SetManager {
        return SetTingsCache["config"]!!
    }

    val StorageDate by lazy {
        getConfig().storageDate
    }
    val SmtpData by lazy {
        getConfig().SmtpData
    }


    /**
     * config
     */
    val DeBug by lazy { config.getBoolean("debug", false) }

    val SMTP_SET by lazy { SetTingsCache["config"]?.SmtpData?.start ?: false }

    val USE_BUNDLE by lazy { if (GeekMail.BukkitVersion >= 1170) config.getBoolean("config.use_bundle") else false }

    val UseExpiry by lazy { config.getBoolean("config.Expiry.use") }

    val ExpiryTime by lazy { Expiry.getExpiryMillis(config.getString("config.Expiry.time") ?: "2d", false) * 1000 }

    val ExpiryAuto by lazy { config.getInt("config.Expiry.auto", 600) }

    val Console: UUID = UUID.fromString("00000000-0000-0000-0000-000000000001")

    var location: Location? = null



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