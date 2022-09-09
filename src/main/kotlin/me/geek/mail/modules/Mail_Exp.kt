package me.geek.mail.modules

import me.geek.mail.Configuration.ConfigManager
import me.geek.mail.api.mail.MailSub
import me.geek.mail.common.kether.sub.KetherAPI
import java.util.UUID
import org.bukkit.Bukkit
import org.bukkit.entity.Player

/**
 * 作者: 老廖
 * 时间: 2022/8/6
 */
class Mail_Exp(
    override val mailID: UUID,
    override var title: String,
    override var text: String,
    override var sender: UUID,
    override var target: UUID,
    override var state: String,
    override val mailType: String,
    override val additional: String,
    override val appendixInfo: String,
    override val senderTime: String,
    override var getTime: String,
    override val permission: String = "mail.exp.exp",

    ) : MailSub() {

    constructor() : this(
        mailID = UUID.fromString("00000000-0000-0000-0000-000000000001"),
        mailType = "经验邮件",
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
        mailID = UUID.fromString(args[0]), // 邮件 UUID
        mailType = "经验邮件",
        title = args[1],
        text = args[2],
        sender = UUID.fromString(args[3]),
        target = UUID.fromString(args[4]),
        state = args[5],
        appendixInfo = "${args[6]}  ${ConfigManager.EXP_MAIL}",
        additional = args[6],
        senderTime = args[7],
        getTime = args[8]
    )


    override fun giveAppendix() {
        Bukkit.getPlayer(this.target)?.giveExp(this.additional.toInt())
    }

    override fun condition(player: Player, appendix: String): Boolean {
        val d = KetherAPI.instantKether(player, "Exp hasTake $appendix").any as Boolean
        if (!d) player.sendMessage("[!] 你没有足够的经验值")
        return d
    }
}