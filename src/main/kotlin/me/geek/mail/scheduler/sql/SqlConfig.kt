package me.geek.mail.scheduler.sql

import java.io.File

/**
 * 作者: 老廖
 * 时间: 2022/10/16
 *
 **/
data class SqlConfig(
    val use_type: String = "sqlite",
    val mysql: MysqlData = MysqlData(),
    val hikari_settings: HikariData = HikariData(),
) {
    var sqlite: File? = null
}
