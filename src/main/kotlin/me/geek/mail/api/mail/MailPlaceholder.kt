package me.geek.mail.api.mail

import java.text.SimpleDateFormat

/**
 * 作者: 老廖
 * 时间: 2022/10/10
 *
 **/
abstract class MailPlaceholder : Mail {

    val format = SimpleDateFormat("yyyy年 MM月 dd日 HH:mm:ss")

    val TYPE = Regex("(\\{|\\[)(type|种类)(}|])")

    val SENDER = Regex("(\\{|\\[)(sender|发送者)(}|])")

    val SERDER_TIME = Regex("(\\{|\\[)(senderTime|发送时间)(}|])")

    val GET_TIME = Regex("(\\{|\\[)(getTime|领取时间)(}|])")

    val TEXT = Regex("(\\{|\\[)(text|文本)(}|])")

    val STATE = Regex("(\\{|\\[)(state|状态)(}|])")

    val ITEM = Regex("(\\{|\\[)(item|附件)(}|])")

    val EXPIRE = Regex("(\\{|\\[)(expire|到期时间)(}|])")


}