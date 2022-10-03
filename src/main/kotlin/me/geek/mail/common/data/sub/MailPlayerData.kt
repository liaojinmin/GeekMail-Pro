package me.geek.mail.common.data.sub

import java.util.UUID

/**
 * 作者: 老廖
 * 时间: 2022/9/10
 **/
class MailPlayerData(
    val name: String,
    val uuid: UUID,
    var mail: String,
    var OneJoin: Boolean = false,
) {
    fun reset() {
        this.mail = ""
        this.OneJoin = false
    }



    companion object {
        fun defaut_Data(name: String, uuid: UUID): MailPlayerData {
            return MailPlayerData(name, uuid, "",true)
        }
    }
}