package me.geek.mail.api.hook.impl

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import me.geek.mail.GeekMail
import me.geek.mail.api.data.SqlManage.getData
import org.bukkit.entity.Player

class Placeholder: PlaceholderExpansion() {

    override fun onPlaceholderRequest(player: Player, params: String): String {
        if (params.contains("mail")) {
            return player.getData().mail
        }
        return "null"
    }

    override fun getIdentifier(): String {
        return "gkm"
    }

    override fun getAuthor(): String {
        return "老廖"
    }

    override fun getVersion(): String {
        return GeekMail.VERSION.toString()
    }
}