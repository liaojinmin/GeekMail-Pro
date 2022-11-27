package me.geek.mail.modules

import com.google.gson.annotations.Expose
import me.geek.mail.api.hook.HookPlugin
import me.geek.mail.api.mail.MailSub
import me.geek.mail.common.kether.sub.KetherAPI
import me.geek.mail.modules.settings.SetTings
import org.bukkit.entity.Player
import java.util.UUID

/**
 * 作者: 老廖
 * 时间: 2022/8/6
 */
class Mail_Points(
    override val mailID: UUID,
    override val mailType: String,

    override var title: String,
    override var text: String,
    override var sender: UUID,
    override var target: UUID,
    override var state: String,

    override val appendixInfo: String,
    override val additional: String,
    override val senderTime: String,
    override var getTime: String,

    @Expose
    override val permission: String = "mail.exp.points",
    ) : MailSub() {
    @Expose
    override val mailIcon: String = SetTings.mailIcon.POINTS_MAIL

    constructor() : this(
        mailID = UUID.fromString("00000000-0000-0000-0000-000000000001"),
        mailType = "点券邮件",
        title = "Title",
        text = "Text",
        sender = UUID.fromString("00000000-0000-0000-0000-000000000001"),
        target = UUID.fromString("00000000-0000-0000-0000-000000000001"),
        state = "state",
        appendixInfo = "null",
        additional = "0",
        senderTime = "",
        getTime = ""
    )
    constructor(args: Array<String>) : this(
        mailID = UUID.fromString(args[0]),
        mailType = "点券邮件",
        title = args[1],
        text = args[2],
        sender = UUID.fromString(args[3]),
        target = UUID.fromString(args[4]),
        state = args[5],
        appendixInfo = "${args[6].filter { it.isDigit() }} ${SetTings.POINTS_MAIL}",
        additional = args[6].filter { it.isDigit() },
        senderTime = args[7],
        getTime = args[8]
    )


    override fun giveAppendix() {
        HookPlugin.points.givePoints(target, additional.toInt())
    }

    override fun condition(player: Player, appendix: String): Boolean {
        val d = KetherAPI.instantKether(player, "Points hasTake ${appendix.filter { it.isDigit() }}").any as Boolean
        if (!d) player.sendMessage("[!] 你没有足够的点券")
        return d
    }

}