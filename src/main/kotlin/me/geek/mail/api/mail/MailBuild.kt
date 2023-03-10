package me.geek.mail.api.mail

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.library.reflex.Reflex.Companion.setProperty
import java.util.*

/**
 * 作者: 老廖
 * 时间: 2023/1/23
 *
 **/
class MailBuild(
    val mailType: String,
    val player: Player?,
    val target: UUID
) {
    var title: String = ""
    var text: String = ""
    var additional: String = "0"
    var item: Array<ItemStack>? = emptyArray()
    var command: List<String>? = emptyList()


    private val mail: MailSub = MailManage.getMailClass(mailType) ?: error("MailBuild() 提供了错误的类型参数，请联系相应开发者。。。")

    fun build(builder: MailBuild.() -> Unit = {}): MailBuild {
        builder(this)
        return this
    }
    fun run(): MailSub {
        mail.title = this.title
        mail.text = this.text
        if (player != null) mail.setProperty("sender", player.uniqueId)
        mail.setProperty("target", this.target)
        mail.additional = this.additional
        mail.setProperty("itemStacks", this.item)
        mail.setProperty("command", this.command)
        return mail
    }
    fun sender() {
        run().sendMail()
    }

    fun setTitles(title: String): MailBuild {
        this.title = title
        return this
    }
    fun setTexts(text: String): MailBuild {
        this.text = text
        return this
    }
    fun setAdditional(additional: String): MailBuild {
        this.additional = additional
        return this
    }
    fun setItems(item: Array<ItemStack>): MailBuild {
        this.item = item
        return this
    }
    fun setCommands(cmd: List<String>): MailBuild {
        this.command = cmd
        return this
    }

}