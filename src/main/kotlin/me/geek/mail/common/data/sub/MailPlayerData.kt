package me.geek.mail.common.data.sub

import me.geek.mail.GeekMail
import java.util.UUID

/**
 * 作者: 老廖
 * 时间: 2022/9/10
 **/
class MailPlayerData(
    val name: String,
    val uuid: UUID,
    var mail: String,
    var OneJoin: Boolean = true,
) {
    fun reset() {
        this.mail = ""
        this.OneJoin = true
    }



    companion object {

        fun MailPlayerData.update() {
            GeekMail.DataManage.update(this)
        }

        fun defaultsData(name: String, uuid: UUID): MailPlayerData {
            return MailPlayerData(name, uuid, "",true)
        }
    }
}