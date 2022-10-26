package me.geek.mail.command



import me.geek.mail.GeekMail

import me.geek.mail.command.admin.*
import me.geek.mail.command.player.*
import me.geek.mail.common.market.Item
import me.geek.mail.scheduler.redis.RedisMessageType
import org.bukkit.Bukkit
import org.bukkit.Material

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

import taboolib.common.platform.command.*
import taboolib.common.platform.function.adaptCommandSender
import taboolib.common.platform.function.submitAsync

import taboolib.module.chat.TellrawJson
import taboolib.module.kether.isInt
import taboolib.module.lang.sendLang
import taboolib.platform.util.takeItem
import java.util.*

@CommandHeader(name = "GeekMail", aliases = ["gkm"], permissionDefault = PermissionDefault.TRUE )
object CmdCore {


    @CommandBody(permission = "mail.command.admin", optional = true)
    val send = CmdSend.command

    @CommandBody(permission = "mail.command.admin")
    val sendpack = CmdSendPack.command

    @CommandBody(permission = "mail.command.admin")
    val reload = CmdReload.command

    @CommandBody(permission = "mail.command.admin")
    val setblock = CmdSetBlock.command

    @CommandBody(permission = "mail.command.admin")
    val global = CmdSendGlobal.command


    @CommandBody(permission = "mail.command.pack")
    val pack = CmdPack.command

    @CommandBody(permissionDefault = PermissionDefault.TRUE)
    val mail = CmdMail.command


    @CommandBody
    val main = mainCommand {
        execute { sender, _, _ ->
            createHelp(sender)
        }
    }

    @CommandBody
    val sell = subCommand {
        dynamic("金币价格") {
            suggestion<CommandSender>(uncheck = true) { _, _ ->
                listOf("金币价格")
            }
            dynamic("钻币价格") {
                suggestion<CommandSender>(uncheck = true) { _, _ ->
                    listOf("钻币价格")
                }
                execute<Player> { sender, context, _ ->
                    if (context.args()[1].isInt() && context.args()[1].isInt()) {
                        if (context.args()[1] != "0" || context.args()[2] != "0") {
                            val money = context.args()[1].toDouble()
                            val points = context.args()[2].toInt()
                            val item = sender.inventory.itemInMainHand.clone()
                            sender.inventory.setItemInMainHand(null)
                            val pack = Item(UUID.randomUUID(), sender.uniqueId, System.currentTimeMillis().toString(), "0", points, money, item)
                            GeekMail.dataScheduler?.let {
                                submitAsync {
                                    it.setMarketData(pack)
                                    it.sendMarketPublish(Bukkit.getPort().toString(), RedisMessageType.MARKET_ADD, pack.packUid.toString())
                                }
                            }
                            pack.addToMarket()
                            sender.sendMessage("§a你的商品已成功发布只市场")
                        } else sender.sendMessage("§c你不能将两种货币都设置为 0")
                    } else sender.sendMessage("§c你输入的不是数字，如果你不想使用两种货币，请将其中一个设置为 0")
                }
            }
        }
    }

    private fun createHelp(sender: CommandSender) {
        val s = adaptCommandSender(sender)
        s.sendMessage("")
        TellrawJson()
            .append("  ").append("§f§lGeekMail§8-§6Pro")
            .hoverText("§7现代化高级邮件系统插件 By GeekCraft.ink")
            .append(" ").append("§f${GeekMail.VERSION} §e付费版")
            .hoverText("""
                §7插件版本: §f${GeekMail.VERSION}
            """.trimIndent()).sendTo(s)
        s.sendMessage("")
        s.sendMessage("  §7指令: §f/gkm §8[...]")
        if (sender.hasPermission("mail.command.admin")) {
            s.sendLang("CMD-HELP-ADMIN")
        }
        s.sendLang("CMD-HELP-PLAYER")

    }
}