package me.geek.mail.scheduler.sql

import com.zaxxer.hikari.HikariDataSource
import java.lang.RuntimeException
import java.sql.Connection

/**
 * 作者: 老廖
 * 时间: 2022/10/16
 *
 **/
class Mysql(
    private val config: SqlConfig
): SqlService() {
    override var isActive: Boolean = false

    private val dataSource by lazy {
        HikariDataSource().apply {
            maximumPoolSize = config.hikari_settings.maximum_pool_size
            minimumIdle = config.hikari_settings.minimum_idle
            maxLifetime = config.hikari_settings.maximum_lifetime.toLong()
            keepaliveTime = config.hikari_settings.keepalive_time.toLong()
            connectionTimeout = config.hikari_settings.connection_timeout.toLong()
            poolName = "GeekMail-MYSQL"
        }
    }

    override fun getConnection(): Connection {
        return this.dataSource.connection
    }

    override fun onStart() {
        val url = "jdbc:mysql://${config.mysql.host}:${config.mysql.port}/${config.mysql.database}${config.mysql.params}"
        dataSource.jdbcUrl = url
        dataSource.username = config.mysql.username
        dataSource.password = config.mysql.password
        // 设置驱动
        try {
            dataSource.driverClassName = "com.mysql.cj.jdbc.Driver"
        } catch (e: RuntimeException) {
            dataSource.driverClassName = "com.mysql.jdbc.Driver"
        } catch (e: NoClassDefFoundError) {
            dataSource.driverClassName = "com.mysql.jdbc.Driver"
        }
        isActive = testSql()

    }

    override fun onClose() {
        this.dataSource.close()
    }

}