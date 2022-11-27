package me.geek.mail.modules

import com.google.gson.annotations.Expose
import me.geek.mail.api.hook.HookPlugin
import me.geek.mail.api.mail.MailSub
import me.geek.mail.common.kether.sub.KetherAPI
import me.geek.mail.modules.settings.SetTings
import java.util.UUID
import org.bukkit.Bukkit
import org.bukkit.entity.Player

/**
 * 作者: 老廖
 * 时间: 2022/7/24
 */
class Mail_Money(
    override val mailID: UUID,
    override var title: String,
    override var text: String,
    override var sender: UUID,
    override var target: UUID,
    override var state: String,
    override val mailType: String,
    override var additional: String,
    override var appendixInfo: String,
    override val senderTime: String,
    override var getTime: String,

    @Expose
    override val permission: String = "mail.exp.money",
) : MailSub() {
    @Expose
    override val mailIcon: String = SetTings.mailIcon.MONEY_MAIL

    constructor() : this(
        mailID = UUID.fromString("00000000-0000-0000-0000-000000000001"),
        mailType = "金币邮件",
        title = "邮件标题",
        text = "邮件文本",
        sender = UUID.fromString("00000000-0000-0000-0000-000000000001"),
        target = UUID.fromString("00000000-0000-0000-0000-000000000001"),
        state = "未领取",
        appendixInfo = "null",
        additional = "0",
        senderTime = "",
        getTime = ""
    )
    constructor(args: Array<String>) : this(
        mailID = UUID.fromString(args[0]),
        mailType = "金币邮件",
        title = args[1],
        text = args[2],
        sender = UUID.fromString(args[3]),
        target = UUID.fromString(args[4]),
        state = args[5],
        appendixInfo = "",
        additional = "",
        senderTime = args[7],
        getTime = args[8]
    ) {
        appendixInfo = "${formatDouble(args[6])} ${SetTings.MONEY_MAIL}"
        additional = formatDouble(args[6])
    }



    override fun giveAppendix() {
        HookPlugin.money.giveMoney(Bukkit.getOfflinePlayer(target), this.additional.toDouble());
    }

    override fun condition(player: Player, appendix: String): Boolean {
        val d = KetherAPI.instantKether(player, "Money hasTake ${formatDouble(appendix)}").any as Boolean
        if (!d) player.sendMessage("[!] 你没有足够的金币")
        return d
    }


}
