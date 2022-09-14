package me.geek.mail.common.data.sub

import java.sql.Connection

/**
 * 作者: 老廖
 * 时间: 2022/7/23
 */
abstract class DataSub {

    abstract val connection: Connection

    abstract fun onLoad()

    abstract fun onStop()
}