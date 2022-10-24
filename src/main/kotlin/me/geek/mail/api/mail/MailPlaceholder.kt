package me.geek.mail.api.mail

import com.google.gson.annotations.Expose
import java.text.SimpleDateFormat

/**
 * 作者: 老廖
 * 时间: 2022/10/10
 *
 **/
abstract class MailPlaceholder : Mail {

    @Expose
    val format = SimpleDateFormat("yyyy年 MM月 dd日 HH:mm:ss")

    @Expose
    val TYPE = Regex("(\\{|\\[)(type|种类)(}|])") // [type] [种类] {type} {种类}

    @Expose
    val SENDER = Regex("(\\{|\\[)(sender|发送者)(}|])")

    @Expose
    val SERDER_TIME = Regex("(\\{|\\[)(senderTime|发送时间)(}|])")

    @Expose
    val GET_TIME = Regex("(\\{|\\[)(getTime|领取时间)(}|])")

    @Expose
    val TEXT = Regex("(\\{|\\[)(text|文本)(}|])")

    @Expose
    val STATE = Regex("(\\{|\\[)(state|状态)(}|])")

    @Expose
    val ITEM = Regex("(\\{|\\[)(item|附件)(}|])")

    @Expose
    val EXPIRE = Regex("(\\{|\\[)(expire|到期时间)(}|])")


}