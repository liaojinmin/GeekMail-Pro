package me.geek.mail.common.data

import com.zaxxer.hikari.HikariDataSource
import me.geek.mail.common.data.sub.DataSub
import me.geek.mail.common.data.sub.action
import me.geek.mail.common.data.sub.use
import me.geek.mail.modules.settings.SetTings
import java.lang.RuntimeException
import java.sql.Connection
import java.sql.SQLException

/**
 * 作者: 老廖
 * 时间: 2022/7/23
 */
class Mysql : DataSub() {
    private var MYSQL: HikariDataSource = HikariDataSource()
    private val set = SetTings.StorageDate

    override val connection: Connection
        get() = MYSQL.connection

    override fun onLoad() {
        val MysqlUrl =
            "jdbc:mysql://" + set.mysql.host + ":" + set.mysql.port + "/" + set.mysql.database + set.mysql.params
        MYSQL.jdbcUrl = MysqlUrl
        MYSQL.username = set.mysql.username
        MYSQL.password = set.mysql.password
        // 设置驱动
        try {
            MYSQL.driverClassName = "com.mysql.cj.jdbc.Driver"
        } catch (e: RuntimeException) {
            MYSQL.driverClassName = "com.mysql.jdbc.Driver"
        } catch (e: NoClassDefFoundError) {
            MYSQL.driverClassName = "com.mysql.jdbc.Driver"
        }
        MYSQL.maximumPoolSize = set.hikari_settings.maximum_pool_size
        MYSQL.minimumIdle = set.hikari_settings.minimum_idle
        MYSQL.maxLifetime = set.hikari_settings.maximum_lifetime.toLong()
        MYSQL.keepaliveTime = set.hikari_settings.keepalive_time.toLong()
        MYSQL.connectionTimeout = set.hikari_settings.connection_timeout.toLong()
        MYSQL.poolName = "GeekMail-MYSQL"
        createMysqlTables()
    }

    override fun onStop() {
        MYSQL.close()
    }

    private fun createMysqlTables() {
        try {
            connection.use {
                this.createStatement().action { statement ->
                    statement.addBatch(
                        "CREATE TABLE IF NOT EXISTS `mail_player_data` (" +
                                " `uuid` CHAR(36) NOT NULL UNIQUE," +
                                " `name` VARCHAR(16) NOT NULL," +
                                " `mail` CHAR(254) NOT NULL," +
                                " `one_join` CHAR(5) NOT NULL," +
                                "PRIMARY KEY (`uuid`)" +
                                ");"
                    )
                    statement.addBatch(
                        "CREATE TABLE IF NOT EXISTS `maildata` (" +
                                " `id` integer NOT NULL AUTO_INCREMENT, " +
                                " `mail_id` CHAR(36) NOT NULL UNIQUE, " +
                                " `state` VARCHAR(256) NOT NULL, " +
                                " `type` text NOT NULL, " +
                                " `sender` CHAR(36) NOT NULL, " +
                                " `target` CHAR(36) NOT NULL, " +
                                " `title` text NOT NULL, " +
                                " `text` longtext NOT NULL, " +
                                " `additional` text NOT NULL, " +
                                " `item` longtext NOT NULL, " +
                                " `commands` longtext NOT NULL, " +
                                " `sendertime` BIGINT(20) NOT NULL, " +
                                " `gettime` BIGINT(20) NOT NULL, " +
                                " PRIMARY KEY (`id`,`target`)" +
                                ");"
                    )
                    statement.executeBatch()
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }
}