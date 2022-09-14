package me .geek.mail.modules.settings

import me.geek.mail.GeekMail
import me.geek.mail.GeekMail.config
import me.geek.mail.utils.colorify
import org.bukkit.Bukkit
import org.bukkit.Location
import java.util.*

/**
 * 作者: 老廖
 * 时间: 2022/9/14
 *
 **/
object SetTings {
    var DeBug = false
        private set
    lateinit var DATA_TYPE: String
        private set
    lateinit var MYSQL_HOST: String
        private set
    var MYSQL_PORT = 3306
        private set
    lateinit var MYSQL_DATABASE: String
        private set
    lateinit var MYSQL_USERNAME: String
        private set
    lateinit var MYSQL_PASSWORD: String
        private set
    lateinit var MYSQL_PARAMS: String
        private set
    lateinit var MYSQL_DATA_NAME: String
        private set
    var MAXIMUM_POOL_SIZE = 10
        private set
    var MINIMUM_IDLE = 5
        private set
    var MAXIMUM_LIFETIME = 1800000
        private set
    var KEEPALIVE_TIME = 0
        private set
    var CONNECTION_TIMEOUT = 5000
        private set

    //
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

    //
    var location: Location? = null

    var SMTP_SET = false
        private set

    var Console: UUID = UUID.fromString("00000000-0000-0000-0000-000000000001")
        private set


    fun onLoad() {
        DeBug = config.getBoolean("debug", false);
        //sql
        DATA_TYPE = config.getString("data_storage.use_type") ?: "sqlite"
        MYSQL_HOST = config.getString("data_storage.mysql.host") ?: "127.0.0.1"
        MYSQL_PORT = config.getInt("data_storage.mysql.port")
        MYSQL_DATABASE = config.getString("data_storage.mysql.database") ?: "server_Mail"
        MYSQL_USERNAME = config.getString("data_storage.mysql.username", "root") ?: "root"
        MYSQL_PASSWORD = config.getString("data_storage.mysql.password") ?: "123456"
        MYSQL_PARAMS = config.getString("data_storage.mysql.params") ?: "?autoReconnect=true&useSSL=false"
        MYSQL_DATA_NAME = config.getString("data_storage.mysql.DataName") ?: "player_Mail"
        // hikari
        MAXIMUM_POOL_SIZE = config.getInt("data_storage.hikari_settings.maximum_pool_size")
        MINIMUM_IDLE = config.getInt("data_storage.hikari_settings.minimum_idle")
        MAXIMUM_LIFETIME = config.getInt("data_storage.hikari_settings.maximum_lifetime")
        KEEPALIVE_TIME = config.getInt("data_storage.hikari_settings.keepalive_time")
        CONNECTION_TIMEOUT = config.getInt("data_storage.hikari_settings.connection_timeout")
        // 邮件种类转换语言
        MONEY_MAIL = config.getString("MailType.MONEY_MAIL.tag")?.colorify() ?: "§e金币"
        POINTS_MAIL = config.getString("MailType.POINTS_MAIL.tag")?.colorify() ?: "§b点券"
        EXP_MAIL = config.getString("MailType.EXP_MAIL.tag")?.colorify() ?: "§a经验"
        TEXT_MAIL = config.getString("MailType.TEXT_MAIL.tag")?.colorify() ?: "§f文本"
        CMD_MAIL = config.getString("MailType.CMD_MAIL.tag")?.colorify() ?: "§c系统"
        ITEM_MAIL = config.getString("MailType.ITEM_MAIL.tag")?.colorify() ?: "§6物品"

        location = config.getString("Block.loc")?.split(",")?.let {
            Location(Bukkit.getWorld(it[0]), it[1].toDouble(), it[2].toDouble(), it[3].toDouble())
        }

        SMTP_SET = config.getBoolean("SmtpSet.start")

    }
}