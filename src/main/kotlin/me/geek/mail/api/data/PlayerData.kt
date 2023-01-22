package me.geek.mail.api.data

import me.geek.mail.api.mail.MailSub
import org.bukkit.entity.Player
import java.util.UUID

/**
 * 作者: 老廖
 * 时间: 2023/1/21
 *
 **/
interface PlayerData {
    /**
     * 玩家对象
     */
    val player: Player

    /**
     * 玩家名称
     */
    val user: String

    /**
     * 玩家uuid
     */
    val uuid: UUID

    /**
     * 玩家绑定的邮箱
     */
    var mail: String

    /**
     * 是不是新玩家
     */
    var newPlayer: Boolean

    /**
     * 玩家的所有邮件
     */
    val mailData: MutableList<MailSub>


    /**
     * 数据序列化
     */
    fun toByteArray(): ByteArray

    /**
     * 将数据转到 JSON
     */
    fun toJsonText(): String
}