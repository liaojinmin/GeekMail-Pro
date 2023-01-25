package me.geek.mail.scheduler.migrator

import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import me.geek.mail.api.data.PlayerData
import me.geek.mail.api.mail.MailSub
import me.geek.mail.modules.Mail_Item
import me.geek.mail.modules.Mail_Normal
import me.geek.mail.scheduler.Exclude
import me.geek.mail.utils.serializeItemStacks
import org.bukkit.entity.Player
import java.util.*

/**
 * 作者: 老廖
 * 时间: 2023/1/25
 *
 **/
class PData(
    override val user: String,
    override val uuid: UUID,
    override var mail: String,
    override var newPlayer: Boolean,
): PlayerData {

    @Expose
    override val player: Player? = null

    override val mailData: MutableList<MailSub> = mutableListOf()

    override fun toByteArray(): ByteArray {
        return this.toJsonText().toByteArray(charset = Charsets.UTF_8)
    }
    override fun toJsonText(): String {
        this.mailData.forEach {
            when (it) {
                is Mail_Item -> {
                    it.itemStackString = it.itemStacks.serializeItemStacks()
                }
                is Mail_Normal -> {
                    it.itemStacks?.let { i ->
                        if (i.isNotEmpty()) {
                            it.itemStackString = i.serializeItemStacks()
                        }
                    }
                }
            }
        }
        return GsonBuilder()
            .setExclusionStrategies(Exclude())
            .create().toJson(this)
    }
}