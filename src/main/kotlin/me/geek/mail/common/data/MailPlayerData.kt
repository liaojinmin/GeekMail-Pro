package me.geek.mail.common.data

import me.geek.mail.api.mail.MailSub
import java.util.UUID

/**
 * 作者: 老廖
 * 时间: 2022/9/10
 **/
data class MailPlayerData(
    val name: String,
    val uuid: UUID,
    var mail: String = "",
    var OneJoin: Boolean = true,
    var mailData: MutableList<MailSub> = mutableListOf()
)