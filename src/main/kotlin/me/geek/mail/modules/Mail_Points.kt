package me.geek.mail.modules

import me.geek.mail.Configuration.ConfigManager
import me.geek.mail.api.hook.hookPlugin
import me.geek.mail.api.mail.MailSub
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

    ) : MailSub() {

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
        appendixInfo = "${args[6]} ${ConfigManager.POINTS_MAIL}",
        additional = args[6],
        senderTime = args[7],
        getTime = args[8]
    )


    override fun giveAppendix() {
        hookPlugin.points.give(target, additional.toInt())
    }
}