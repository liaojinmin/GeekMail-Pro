package me.geek.mail.modules


import com.google.gson.annotations.Expose
import me.geek.mail.api.mail.MailSub
import me.geek.mail.modules.settings.SetTings
import org.bukkit.entity.Player
import java.util.UUID

/**
 * 作者: 老廖
 * 时间: 2022/8/6
 */
class Mail_Text(
    override val mailID: UUID,
    override var title: String,
    override var text: String,
    override var sender: UUID,
    override var target: UUID,
    override var state: String = "无",
    override val mailType: String,

    override val additional: String,
    override val appendixInfo: String,
    override val senderTime: String,
    override var getTime: String,

    @Expose
    override val permission: String = "mail.exp.text",

    ) : MailSub() {
    @Expose
    override val mailIcon: String = SetTings.mailIcon.TEXT_MAIL

    constructor() : this(
        mailID = UUID.fromString("00000000-0000-0000-0000-000000000001"),
        mailType = "文本邮件",
        title = "邮件标题",
        text = "邮件文本",
        sender = UUID.fromString("00000000-0000-0000-0000-000000000001"),
        target = UUID.fromString("00000000-0000-0000-0000-000000000001"),
        state = "",
        appendixInfo = "null",
        additional = "0",
        senderTime = "",
        getTime = ""
    )
    constructor(args: Array<String>) : this(
        mailID = UUID.fromString(args[0]),
        mailType = "文本邮件",
        title = args[1],
        text = args[2],
        sender = UUID.fromString(args[3]),
        target = UUID.fromString(args[4]),
        state = "无",
        appendixInfo = "",
        additional = "",
        senderTime = args[7],
        getTime = args[8]
    )


    override fun giveAppendix() {
    }

    override fun condition(player: Player, appendix: String): Boolean {
        return true
    }
}