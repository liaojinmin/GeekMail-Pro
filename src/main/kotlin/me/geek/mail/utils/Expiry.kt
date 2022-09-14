package me.geek.mail.utils

import me.geek.mail.GeekMail
import java.text.SimpleDateFormat

/**
 * 作者: 老廖
 * 时间: 2022/5/21
 *
 **/
class Expiry {

    private val format = SimpleDateFormat("yyyy年 MM月 dd日 HH:mm:ss")
    /**
     * @param start 传入的是否是启动时间
     * 列如：
     * 传入格式 - 1652346294738
     * 获得格式 - 00d 00h 00m 00s
     */
    fun getExpiryDate(millis: Long, start: Boolean): String {
        GeekMail.debug("&8currentTimeMillis: $millis")
        val times = if (start) millis else (millis - System.currentTimeMillis()) / 1000
        val dd = times / 60 / 60 / 24
        val hh = times / 60 / 60 % 24
        val mm = times / 60 % 60
        val ss = times % 60
        if (dd <= 0 && hh <= 0 && mm <= 0) {
            return if(ss <= 0) "null" else ss.toString() + "秒"
        }
        if (dd <= 0 && hh <= 0) {
            return mm.toString() + "分钟 " + ss + "秒"
        }
        if (dd <= 0) {
            return hh.toString() + "小时 " + mm + "分钟 " + ss + "秒"
        }
        return dd.toString() + "天 " + hh + "小时 " + mm + "分钟 " + ss + "秒"
    }
    fun getExpiryFoData(millis: Long): String {
        if (millis < 1000) return "未领取"
        return format.format(millis)
    }
    /**
     * 获取到期时间戳
     * 列如：
     * 传入格式 - 1d1h21m60s
     * 返回当前时间 + 传入时间的时间戳
     * 获得格式 - 1652346294738
     */
    fun setExpiryMillis(timeData: String, expire: Boolean = true): Long {
        val var200 = timeData
            .replace("d","d ")
            .replace("h","h ")
            .replace("m","m ")
            .replace("s","s ")
        val var300 = var200.split(" ")
        var dd: Long = 0
        var hh: Long = 0
        var mm: Long = 0
        var ss: Long = 0
        if (var300[0].contains("d")) {
            dd = var300[0].replace("d", "").toLong()
        } else if (var300[0].contains("h")) {
            hh = var300[0].replace("h", "").toLong()
        } else if (var300[0].contains("m")) {
            mm = var300[0].replace("m", "").toLong()
        } else {
            ss = var300[0].replace("s", "").toLong()
        }
        try {
            if (var300[1].contains("d")) dd = var300[1].replace("d", "").toLong()
            if (var300[1].contains("h")) hh = var300[1].replace("h", "").toLong()
            if (var300[1].contains("m")) mm = var300[1].replace("m", "").toLong()
            if (var300[1].contains("s")) ss = var300[1].replace("s", "").toLong()
        } catch (ignored: IndexOutOfBoundsException) {}
        try {
            if (var300[2].contains("d")) dd = var300[2].replace("d", "").toLong()
            if (var300[2].contains("h")) hh = var300[2].replace("h", "").toLong()
            if (var300[2].contains("m")) mm = var300[2].replace("m", "").toLong()
            if (var300[2].contains("s")) ss = var300[2].replace("s", "").toLong()
        } catch (ignored: IndexOutOfBoundsException) {}
        try {
            if (var300[3].contains("d")) dd = var300[3].replace("d", "").toLong()
            if (var300[3].contains("h")) hh = var300[3].replace("h", "").toLong()
            if (var300[3].contains("m")) mm = var300[3].replace("m", "").toLong()
            if (var300[3].contains("s")) ss = var300[3].replace("s", "").toLong()
        } catch (ignored: IndexOutOfBoundsException) {}
        return if (expire) getExpiry(dd, hh, mm, ss) + System.currentTimeMillis() else getExpiry(dd, hh, mm, ss) / 1000
    }

    private fun getExpiry(dd: Long, hh: Long, mm: Long, ss: Long): Long {
        val D = dd * 24 * 60 * 60
        val H = hh * 60 * 60
        val M = mm * 60
        val var1 = D + H + M + ss
        return (var1.toString() + "000").toLong()
    }
}