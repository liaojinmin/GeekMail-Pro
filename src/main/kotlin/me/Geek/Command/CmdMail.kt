package me.Geek.Command


import com.google.common.base.Joiner
import me.Geek.Configuration.ConfigManager
import me.Geek.Configuration.LangManager.demMail
import me.Geek.GeekMail
import me.Geek.Libs.Menu.MItem
import me.Geek.Modules.MailExp
import me.Geek.Modules.MailMoney
import me.Geek.Modules.MailPoints
import me.Geek.Modules.MailText
import me.Geek.api.hook.hookPlugin
import me.Geek.api.mail.MailType
import me.Geek.api.mail.MailType.*
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand
import java.util.*
import java.util.regex.Pattern
import kotlin.math.abs
import kotlin.math.roundToInt


/**
 * 作者: 老廖
 * 时间: 2022/8/7
 *
 **/
object CmdMail: CmdExp {
    override val command = subCommand {
        dynamic("邮件种类") {
            suggestion<CommandSender> { _, _ ->
                values().map { it.name }
            }
            dynamic("目标玩家") {
                suggestion<CommandSender> { _, _ ->
                    Bukkit.getOnlinePlayers().map { it.name }
                }
                dynamic("标题") {
                    suggestion<CommandSender>(uncheck = true) { _, _ ->
                        listOf("[邮件标题]")
                    }
                    dynamic("内容") {
                        suggestion<CommandSender>(uncheck = true) { _, _ ->
                            listOf("[邮件内容]")
                        }
                            execute<Player> { sender, context, _ ->
                                val value = Joiner.on(",").join(context.args()).replace("&", "§").split(",")
                                val type = valueOf(value[1])
                                val uuid: UUID = sender.uniqueId
                                val all = value[4].split(" ")
                                val vars = if (all.size >= 2) all[1] else " "
                                if (hasPerm(sender, type, vars)) {
                                    val target = Bukkit.getOfflinePlayer(value[2]).uniqueId
                                    val title = value[3]
                                    val text = all[0]
                                    when (type) {
                                        MONEY_MAIL -> {
                                            MailMoney(
                                                UUID.randomUUID(), uuid, target, title, text, add(all[1])
                                            ).SendMail()
                                            return@execute
                                        }
                                        POINTS_MAIL -> {
                                            MailPoints(
                                                UUID.randomUUID(), uuid, target, title, text,
                                                all[1].filter { it.isDigit() }.toInt()
                                            ).SendMail()
                                            return@execute
                                        }
                                        EXP_MAIL -> {
                                            MailExp(
                                                UUID.randomUUID(), uuid, target, title, text, all[1].filter { it.isDigit() }.toInt()
                                            ).SendMail()
                                            return@execute

                                        }
                                        TEXT_MAIL -> {
                                            MailText(
                                                UUID.randomUUID(), uuid, target, title, text,
                                            ).SendMail()
                                            return@execute
                                        }
                                        ITEM_MAIL -> {
                                            MItem(
                                                sender, uuid, target, title, text,
                                            )
                                            return@execute
                                        }
                                    }
                                }
                        }
                    }
                }
            }
        }
    }
    private fun hasPerm(player: Player, type: MailType, app: String): Boolean {
        var a = false
        when (type) {
            MONEY_MAIL -> {
                if (player.hasPermission("mail.send.money")) {
                    val money = abs(app.toDouble())
                    if (hookPlugin.money.has(player, money)) {
                        hookPlugin.money.withdrawPlayer(player, money)
                        a = true
                    } else {
                        for (out in demMail) {
                            player.sendMessage(out.replace("{0}", ConfigManager.MONEY_MAIL))
                        }
                    }
                } else {
                    GeekMail.say("&4玩家 &f${player.name} &4执行命令缺少权限&f mail.send.money")
                }
                return a
            }
            POINTS_MAIL -> {
                if (player.hasPermission("mail.send.points")) {
                    val points = abs(app.toInt())
                    if (hookPlugin.points.look(player.uniqueId) >= points) {
                        hookPlugin.points.take(player.uniqueId,points)
                        a = true
                    } else {
                        for (out in demMail) {
                            player.sendMessage(out.replace("{0}", ConfigManager.POINTS_MAIL))
                        }
                    }
                } else {
                    GeekMail.say("&4玩家 &f${player.name} &4执行命令缺少权限&f mail.send.points")
                }
                return a
            }
            EXP_MAIL -> {
                if (player.hasPermission("mail.send.exp")) {
                    val exp = abs(app.toInt())
                    if (player.totalExperience >= exp) {
                        var experience = (getExperienceAtLevel(player.level) * player.exp).roundToInt()
                        var currentLevel = player.level
                        while (currentLevel > 0) {
                            currentLevel--
                            experience += getExperienceAtLevel(currentLevel)
                        }
                        if (experience < 0) {
                            experience = 0
                        }
                        player.level = 0
                        player.exp = 0.0F
                        player.totalExperience = 0
                        player.giveExp(experience - exp)
                        if (!player.isOnline) {
                            player.saveData()
                        }
                        a = true
                    } else {
                        for (out in demMail) {
                            player.sendMessage(out.replace("{0}", ConfigManager.EXP_MAIL))
                        }
                    }
                } else {
                    GeekMail.say("&4玩家 &f${player.name} &4执行命令缺少权限&f mail.send.exp")
                }
                return a
            }
            TEXT_MAIL -> {
                if (player.hasPermission("mail.send.text")) {
                    a = true
                } else {
                    GeekMail.say("&4玩家 &f${player.name} &4执行命令缺少权限&f mail.send.exp")
                }
                return a
            }

            ITEM_MAIL -> {
                if (player.hasPermission("mail.send.item")) {
                    a = true
                } else {
                    GeekMail.say("&4玩家 &f${player.name} &4执行命令缺少权限&f mail.send.item")
                }
                return a
            }
        }
    }
    private fun add(num1: Any): Double {
        val matcher = Pattern.compile("\\d+\\.?\\d?\\d").matcher(num1.toString())
        var var1 = 0.0
        if (matcher.find()) {
            var1 = matcher.group().toDouble()
        }
        return var1
    }
    private fun getExperienceAtLevel(level: Int): Int {
        if (level <= 15) {
            return (level shl 1) + 7
        }
        return if (level <= 30) {
            level * 5 - 38
        } else level * 9 - 158
    }
}