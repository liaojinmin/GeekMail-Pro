package me.geek.mail.api.mail.sub

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

/**
 * 作者: 老廖
 * 时间: 2022/7/28
 */
interface Mail : Cloneable {
    val mailID: UUID
    val mailType: String

    val title: String
    val text: String
    val sender: UUID
    val target: UUID
    var state: String
    val senderTime: String
    var getTime: String
    val appendixInfo: String

    val permission: String
      get() = "mail.global"

    val additional: String?
      get() = " "
    val itemStacks: Array<ItemStack>?
      get() = null
    val command: List<String>?
      get() = null



    fun sendMail()
    fun giveAppendix()
    fun condition(player: Player, appendix: String): Boolean

}