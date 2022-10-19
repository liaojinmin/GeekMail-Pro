package me.geek.mail.scheduler.sql

import java.sql.SQLException

/**
 * 作者: 老廖
 * 时间: 2022/10/16
 *
 **/
abstract class SqlService : SqlApi {

    fun <T> createTab(func: SqlApi.() -> T) {
        try {
            func(this)
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }
    fun testSql(): Boolean {
        return try {
            getConnection()
            true
        } catch (_: SQLException) {
            false
        }
    }
}