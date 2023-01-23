package me.geek.mail.api.mail

import com.google.gson.annotations.Expose
import me.geek.mail.modules.settings.SetTings
import org.bukkit.Bukkit
import org.jetbrains.annotations.NotNull
import taboolib.expansion.geek.Expiry
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

    fun parseMailInfo(@NotNull lore: List<String>): List<String> {
        this.runAppendixInfo()
        val list = mutableListOf<String>()
        lore.forEach {
            when {
                it.contains(TYPE) -> list.add(it.replace(TYPE, this.mailType))
                it.contains(SENDER) -> list.add(it.replace(SENDER, if (this.sender == SetTings.Console) "系统" else Bukkit.getOfflinePlayer(this.sender).name!!))
                it.contains(SERDER_TIME) -> list.add(it.replace(SERDER_TIME, format.format(this.senderTime.toLong())))
                it.contains(GET_TIME) -> list.add(it.replace(GET_TIME, if (getTime < 1000) "未领取" else format.format(this.getTime)))
                it.contains(TEXT) -> list.addAll(it.replace(TEXT, this.text).split(";"))
                it.contains(STATE) -> list.add(it.replace(STATE, this.state.state))
                it.contains(ITEM) -> list.addAll(it.replace(ITEM, this.appendixInfo).split(","))
                it.contains(EXPIRE) -> {
                    if (SetTings.UseExpiry) {
                        list.add(it.replace(EXPIRE, Expiry.getExpiryDate(this.senderTime + SetTings.ExpiryTime, false)))
                    } else list.add(it.replace(EXPIRE, ""))
                }
                else -> list.add(it)
            }
        }
        return list
    }
}