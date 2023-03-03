package me.geek.mail.modules

import com.google.gson.annotations.Expose
import me.geek.mail.api.hook.HookPlugin
import me.geek.mail.api.mail.MailSub
import me.geek.mail.settings.SetTings
import me.geek.mail.utils.getEmptySlot
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.platform.compat.replacePlaceholder
import taboolib.platform.util.giveItem
import taboolib.platform.util.sendLang
import java.util.*

/**
 * 作者: 老廖
 * 时间: 2022/10/7
 *
 **/
class Mail_Normal() : MailSub() {
    constructor(
        senders: UUID,
        targets: UUID,
        additional: String,
        itemStacks: Array<ItemStack> = emptyArray(),
        cmd: List<String> = emptyList()
    ) : this() {
        this.sender = senders
        this.target = targets
        this.additional = additional
        this.itemStacks = itemStacks
        this.command = cmd
    }


    override var sender: UUID = super.sender
    override var target: UUID = super.target
    override val mailType: String = "混合邮件"
    override val mailIcon: String = SetTings.mailIcon.ITEM_MAIL
    override val permission: String = "mail.exp.Normal"


    @Expose
    private var money: Double = 0.00
    @Expose
    private var points: Int = 0
    @Expose
    private var exp: Int = 0

    // 筛选数据类型

    override fun runAppendixInfo() {
        val texts = StringBuilder("")
        additional.split("@").forEach {
            val data = it.split(":")
            if (data[0] != "0") {
                val a = data[0].uppercase(Locale.ROOT)
                when (a) {
                    "MONEY" -> {
                        money = formatDouble(data[1]).toDouble()
                        if (money > 0.0) texts.append("§f$money ${SetTings.MONEY_MAIL}§7, §f")
                    }
                    "POINTS" -> {
                        points = data[1].filter { v -> v.isDigit() }.toInt()
                        if (points > 0) texts.append("§f$points ${SetTings.POINTS_MAIL}§7, §f")
                    }
                    "EXP" -> {
                        exp = data[1].filter { v -> v.isDigit() }.toInt()
                        if (exp > 0) texts.append("§f$exp ${SetTings.EXP_MAIL}§7, §f")
                    }
                }
            }
        }
        if (command.isNotEmpty()) texts.append("${SetTings.CMD_MAIL} §7* §f${command?.size}§7, §f")
        if (itemStacks.isNotEmpty()) getItemInfo(texts)
        appendixInfo = texts.toString()
    }


    override fun giveAppendix(): Boolean {
        Bukkit.getPlayer(this.target)?.let {
            val air = it.getEmptySlot()
            if (air >= this.itemStacks.size) {
                it.giveItem(this.itemStacks.asList())
            } else {
                it.sendLang("玩家-没有足够背包格子", this.itemStacks.size-air)
                return false
            }
        } ?: return false

        if (this.money > 0) {
            HookPlugin.money.giveMoney(Bukkit.getOfflinePlayer(this.target), this.money)
        }
        if (this.points > 0) {
            HookPlugin.points.givePoints(this.target, this.points)
        }
        if (this.exp > 0) {
            Bukkit.getPlayer(this.target)?.giveExp(this.exp)
        }

        if (this.command.isNotEmpty()) {
            Bukkit.getPlayer(this.target)?.let {
                this.command.replacePlaceholder(it).forEach { out ->
                    try {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), out)
                    } catch (_: Exception) {}
                }
            }
        }
        return true

    }

    override fun condition(player: Player, appendix: String): Boolean {
        return false
    }



}