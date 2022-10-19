package me.geek.mail.scheduler.sql

import java.sql.Connection

/**
 * 作者: 老廖
 * 时间: 2022/10/16
 *
 **/
interface SqlApi {

    var isActive: Boolean


    fun getConnection(): Connection

    fun onStart()

    fun onClose()

}