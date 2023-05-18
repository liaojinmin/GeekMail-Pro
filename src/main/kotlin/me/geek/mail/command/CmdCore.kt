package me.geek.mail.command



import me.geek.mail.GeekMail
import me.geek.mail.api.data.SqlManage
import me.geek.mail.command.admin.*
import me.geek.mail.command.player.CmdBind
import me.geek.mail.command.player.CmdMail
import me.geek.mail.command.player.CmdPack
import me.geek.mail.common.market.Item
import me.geek.mail.common.market.Market
import me.geek.mail.scheduler.migrator.Migrator
import me.geek.mail.scheduler.redis.RedisMessageType
import me.geek.mail.settings.SetTings
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.command.*
import taboolib.common.platform.function.adaptCommandSender
import taboolib.common.platform.function.submitAsync
import taboolib.module.chat.TellrawJson
import taboolib.module.kether.isInt
import taboolib.module.lang.sendLang
import taboolib.module.nms.getName
import taboolib.platform.util.sendLang
import java.util.*
import kotlin.math.abs

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

    @CommandBody(permission = "mail.command.admin")
    val store = CmdStore.command



    @CommandBody(permission = "mail.command.pack")
    val pack = CmdPack.command

    @CommandBody(permissionDefault = PermissionDefault.TRUE)
    val mail = CmdMail.command

    @CommandBody(permissionDefault = PermissionDefault.TRUE)
    val bind = CmdBind.command



    @CommandBody
    val main = mainCommand {
        execute { sender, _, _ ->
            createHelp(sender)
        }
    }

    @CommandBody(permission = "mail.command.migrator")
    val migrator = subCommand {
        execute<CommandSender> { sender, _, _ ->
            sender.sendMessage("迁移器准备中...")
            Migrator("Migrator").start()
        }
    }

    @CommandBody
    val sell = subCommand {
        dynamic("金币价格") {
            suggestion<CommandSender>(uncheck = true) { _, _ ->
                listOf("金币价格")
            }
            execute<Player> { sender, context, _ ->
                if (context["金币价格"].isInt()) {
                    if (context["金币价格"].toInt() > 0) {
                        val item = sender.inventory.itemInMainHand.clone()
                        if (item.type != Material.AIR) {
                            val a = Market.getPlayerAllMarket(sender).size
                            val b = SetTings.market.getPlayerPutSize(sender)
                            if (a > b) {
                                sender.sendLang("玩家-市场上架-可上架数量不足")
                                return@execute
                            }
                            sender.inventory.setItemInMainHand(null)
                            sender.put(item, context["金币价格"].toInt(), 0)
                        } else {
                            sender.sendLang("玩家-市场上架-失败")
                        }
                    } else {
                        sender.sendLang("玩家-市场上架-为零")
                    }
                } else {
                    sender.sendLang("玩家-市场上架-NotInt",
                        if (context.args()[1].isInt()) "" else context.args()[1],
                        if (context.args()[2].isInt()) "" else context.args()[2])
                }
            }
            dynamic("钻币价格") {
                suggestion<CommandSender>(uncheck = true) { _, _ ->
                    listOf("钻币价格")
                }
                execute<Player> { sender, context, _ ->
                    if (context["金币价格"].isInt() && context["钻币价格"].isInt()) {
                        if (context["金币价格"].toInt() > 0 || context["钻币价格"].toInt() > 0) {
                            val money = abs(context["金币价格"].toInt())
                            val points = abs(context["钻币价格"].toInt())
                            val item = sender.inventory.itemInMainHand.clone()
                            if (item.type != Material.AIR) {
                                val a = Market.getPlayerAllMarket(sender).size
                                val b = SetTings.market.getPlayerPutSize(sender)
                                if (a >= b) {
                                    sender.sendLang("玩家-市场上架-可上架数量不足")
                                    return@execute
                                }
                                sender.inventory.setItemInMainHand(null)
                                sender.put(item, money, points)
                            } else {
                                sender.sendLang("玩家-市场上架-失败")
                            }
                        } else {
                            sender.sendLang("玩家-市场上架-为零")
                        }
                    } else {
                        sender.sendLang(
                            "玩家-市场上架-NotInt",
                            if (context.args()[1].isInt()) "" else context.args()[1],
                            if (context.args()[2].isInt()) "" else context.args()[2]
                        )
                    }
                }
            }
        }
    }
    private fun Player.put(item: ItemStack, money: Int, points: Int = 0) {

        val pack = Item(UUID.randomUUID(), uniqueId, System.currentTimeMillis(),  points, money.toDouble(), item)
        SqlManage.RedisScheduler?.let {
            submitAsync {
                it.setMarketData(pack)
                it.sendMarketPublish(
                    Bukkit.getPort().toString(),
                    RedisMessageType.MARKET_ADD,
                    pack.packUid.toString()
                )
            }
        }
        pack.addToMarket()
        sendLang("玩家-市场上架-成功", item.getName())
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