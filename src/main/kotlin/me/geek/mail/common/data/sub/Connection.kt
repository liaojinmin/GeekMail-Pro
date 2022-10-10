package me.geek.mail.common.data.sub

import java.sql.Connection
import java.sql.ResultSet
import java.sql.PreparedStatement
import java.sql.Statement


/**
 * 作者: 老廖
 * 时间: 2022/9/14
 *
 **/
fun <T> ResultSet.get(func: ResultSet.() -> T): T {
    return try {
        func(this)
    } catch (ex: Exception) {
        throw ex
    } finally {
        close()
    }
}

fun <T> Connection.use(func: Connection.() -> T): T {
    return try {
        func(this)
    } catch (ex: Exception) {
        throw ex
    } finally {
        close()
    }
}

fun <T: Statement, R> T.action(func: Statement.(T) -> R) {
    try {
        func(this)
    } catch (ex: Exception) {
        throw ex
    } finally {
        close()
    }
}

fun <T: PreparedStatement, R> T.actions(func: PreparedStatement.(T) -> R) {
    try {
        func(this)
    } catch (ex: Exception) {
        throw ex
    } finally {
        close()
    }
}