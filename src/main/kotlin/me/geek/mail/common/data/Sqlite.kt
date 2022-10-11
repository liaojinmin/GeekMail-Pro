package me.geek.mail.common.data

import com.zaxxer.hikari.HikariDataSource
import me.geek.mail.GeekMail.instance
import me.geek.mail.common.data.sub.DataSub
import me.geek.mail.common.data.sub.use

import me.geek.mail.modules.settings.SetTings
import java.io.File
import java.sql.Connection

/**
 * 作者: 老廖
 * 时间: 2022/7/23
 */
class Sqlite : DataSub() {

    private var SQLITE: HikariDataSource = HikariDataSource()

    private val set = SetTings.StorageDate
    override fun onLoad() {
        val url = "jdbc:sqlite:" + instance.dataFolder + File.separator + "GeekData.db"
        SQLITE.dataSourceClassName = "org.sqlite.SQLiteDataSource"
        SQLITE.addDataSourceProperty("url", url)
        //附件参数
        SQLITE.maximumPoolSize = set.hikari_settings.maximum_pool_size
        SQLITE.minimumIdle = set.hikari_settings.minimum_idle
        SQLITE.maxLifetime = set.hikari_settings.maximum_lifetime.toLong()
        SQLITE.keepaliveTime = set.hikari_settings.keepalive_time.toLong()
        SQLITE.connectionTimeout = set.hikari_settings.connection_timeout.toLong()
        SQLITE.poolName = "GeekMail-SQLITE"
        createSqliteTables()
    }


    override val connection: Connection
        get() = SQLITE.connection

    override fun onStop() {
        SQLITE.close()
    }

    private fun createSqliteTables() {
        connection.use {
            val statement = this.createStatement()
            statement.addBatch("PRAGMA foreign_keys = ON;")
            statement.addBatch("PRAGMA encoding = 'UTF-8';")
            statement.addBatch(
                "CREATE TABLE IF NOT EXISTS `mail_player_data` (" +
                        " `uuid` CHAR(36) PRIMARY KEY," +
                        " `name` VARCHAR(16) NOT NULL," +
                        " `mail` CHAR(254) NOT NULL," +
                        " `one_join` CHAR(5) NOT NULL" +
                        ");"
            )
            statement.addBatch(
                "CREATE TABLE IF NOT EXISTS `maildata` (" +
                        " `id` integer PRIMARY KEY, " +
                        " `mail_id` CHAR(36) NOT NULL, " +
                        " `state` VARCHAR(256) NOT NULL, " +
                        " `type` text NOT NULL, " +
                        " `sender` CHAR(36) NOT NULL, " +
                        " `target` CHAR(36) NOT NULL, " +
                        " `title` text NOT NULL, " +
                        " `text` longtext NOT NULL, " +
                        " `additional` text NOT NULL, " +
                        " `item` longtext NOT NULL, " +
                        " `commands` longtext NOT NULL," +
                        " `sendertime` BIGINT(20) NOT NULL," +
                        " `gettime` BIGINT(20) NOT NULL);"
            )
            statement.executeBatch()
        }
    }
}

