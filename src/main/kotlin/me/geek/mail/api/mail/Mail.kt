package me.geek.mail.api.mail

import me.geek.mail.modules.settings.SetTings
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

/**
 * 作者: 老廖
 * 时间: 2022/7/28
 */
interface Mail {
    /**
     * 扩展邮件的名字
     */
    val name: String
    /**
     * 邮件唯一UID 不可重复
     */
    val mailID: UUID

    /**
     * 邮件种类
     */
    val mailType: String
    get() = "未知类型"

    /**
     * 邮件显示图标
     */
    val mailIcon: String
    get() = "BOOK"

    /**
     * 邮件标题
     */
    val title: String

    /**
     * 邮件文本
     */
    val text: String

    /**
     * 邮件发送者
     */
    val sender: UUID
    get() = SetTings.Console

    /**
     * 邮件接收目标
     */
    val target: UUID
        get() = SetTings.Console

    /**
     * 邮件状态
     */
    var state: MailState

    /**
     * 邮件发送时间
     */
    val senderTime: Long

    /**
     * 附件领取时间
     */
    var getTime: Long

    /**
     * 附件展示信息
     */
    val appendixInfo: String

    /**
     * 识别权限，在玩家使用指令发送邮件时鉴权
     * 默认: mail.global
     */
    val permission: String
    get() = "mail.global"

    /**
     * 邮件的其它附件
     */
    var additional: String

    /**
     * 邮件物品附件
     */
    val itemStacks: Array<ItemStack>?

    /**
     * 序列化参数
     */
    var itemStackString: String


    /**
     * 邮件指令附件
     */
    val command: List<String>?


    /**
     * 邮件发送方法-默认即可
     */
    fun sendMail()
    /**
     * 发送跨服邮件
     */
    fun sendCrossMail()
    /**
     * 发送全体邮件
     */
    fun sendGlobalMail()

    /**
     * 附件给予方式，每种邮件类型必须重写
     */
    fun giveAppendix(): Boolean

    /**
     * 邮件发送而外条件，玩家使用指令发送时触发
     */
    fun condition(player: Player, appendix: String): Boolean
    /**
     * 解析附件信息
     */
    fun runAppendixInfo()



}