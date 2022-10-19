package me.geek.mail.common.data

import me.geek.mail.api.mail.MailSub
import java.io.Serializable
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
    var mailData: MutableList<MailSub> = mutableListOf()
): Serializable {


    companion object {

        fun MailPlayerData.update() {
            SqlManage.updatePlayerData(this)
        }

        fun defaultsData(name: String, uuid: UUID): MailPlayerData {
            return MailPlayerData(name, uuid, "",true)
        }


        private const val serialVersionUID = -2022101501991L
    }
}