package me.geek.mail.modules

import com.google.gson.annotations.Expose
import me.geek.mail.GeekMail
import me.geek.mail.api.hook.HookPlugin
import me.geek.mail.api.mail.AppendixType.*
import me.geek.mail.api.mail.MailSub
import me.geek.mail.modules.settings.SetTings
import me.geek.mail.utils.deserializeItemStacks
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.submitAsync
import taboolib.platform.compat.replacePlaceholder
import taboolib.platform.util.giveItem
import java.util.*

/**
 * 作者: 老廖
 * 时间: 2022/10/7
 *
 **/
class Mail_Normal(
    override val mailID: UUID,
    override val mailType: String,
    override val title: String,
    override val text: String,
    override val sender: UUID,
    override val target: UUID,
    override var state: String,
    override val additional: String?,
    override val senderTime: String,
    override var getTime: String,
    override var appendixInfo: String,
    @Expose
    override var itemStacks: Array<ItemStack>?,
    override var command: List<String>?
) : MailSub() {
    // 0 = 邮件唯一ID ， 1 = 标题 ， 2 = 文本 ， 3 = 发送者 ， 4 = 接收者 ， 5 = 状态 ， 6 = 其它附件 ， 7 = 发送时间 ， 8 = 领取时间 ， 9 = 物品字符串 ， 10 = 指令字符串
    constructor(args: Array<String>) : this(
        UUID.fromString(args[0]),
        "混合邮件",
        title = args[1],
        text = args[2],
        sender = UUID.fromString(args[3]),
        target = UUID.fromString(args[4]),
        state = args[5],
        additional = args[6],
        senderTime = args[7],
        getTime = args[8],
        appendixInfo = "",
        itemStacks = null,
        command = null
    ) {
        if (args.size >= 10) {
            itemStacks = args[9].deserializeItemStacks()
        }
        if (args.size >= 11 && args[10].isNotEmpty()) {
            command = args[10].split(";")
        }

        run(args[6])
    }
    private var money: Double = 0.00
    private var points: Int = 0
    private var exp: Int = 0

    // 筛选数据类型
    private fun run(appendix: String) {
        submitAsync {
            val texts = StringBuilder("")
            appendix.split("@").forEach {
                val data = it.split(":")
                when (valueOf(data[0])) {
                    MONEY -> {
                        money = formatDouble(data[1]).toDouble()
                        texts.append("§f$money ${SetTings.MONEY_MAIL}§7, §f")
                    }
                    POINTS -> {
                        points = data[1].filter { v -> v.isDigit() }.toInt()
                        texts.append("§f$points ${SetTings.POINTS_MAIL}§7, §f")
                    }
                    EXP -> {
                        exp = data[1].filter { v -> v.isDigit() }.toInt()
                        texts.append("§f$exp ${SetTings.EXP_MAIL}§7, §f")
                    }
                }
            }
            if (command != null)  texts.append("${SetTings.CMD_MAIL} §7* §f${command?.size}§7, §f")

            if (itemStacks != null) getItemInfo(texts)
            appendixInfo = texts.toString()
        }
    }


    override fun giveAppendix() {
        if (this.money > 0) {
            HookPlugin.money.giveMoney(Bukkit.getOfflinePlayer(this.target), this.money)
        }
        if (this.points > 0) {
            HookPlugin.points.givePoints(this.target, this.points)
        }
        if (this.exp > 0) {
            Bukkit.getPlayer(this.target)?.giveExp(this.exp)
        }
        this.itemStacks?.asList()?.let {
            Bukkit.getPlayer(this.target)?.giveItem(it)
        }
        this.command?.let { cmd ->
            Bukkit.getPlayer(this.target)?.let {
                cmd.replacePlaceholder(it).forEach { out ->
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), out)
                }
            }
        }

    }

    override fun condition(player: Player, appendix: String): Boolean {
        return false
    }











    constructor() : this(
        UUID.fromString("00000000-0000-0000-0000-000000000001"),
        "混合邮件",
        title = "",
        text = "",
        sender = UUID.fromString("00000000-0000-0000-0000-000000000001"),
        target = UUID.fromString("00000000-0000-0000-0000-000000000001"),
        state = "",
        additional = "",
        senderTime = "",
        getTime = "",
        appendixInfo = "",
        itemStacks = null,
        command = null
    )


}