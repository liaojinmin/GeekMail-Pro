package me.geek.mail.api.hook.impl

import me.geek.mail.GeekMail
import org.black_ixx.playerpoints.PlayerPoints
import org.black_ixx.playerpoints.PlayerPointsAPI
import org.bukkit.Bukkit
import java.util.*

/**
 * 作者: 老廖
 * 时间: 2022/10/9
 *
 **/
class Points {
    private var points: PlayerPointsAPI? = null

    init {
        hook()
    }

    private fun hook() {
        val plugin = Bukkit.getServer().pluginManager.getPlugin("PlayerPoints")
        if (plugin != null) {
            GeekMail.say("&7软依赖 &fPlayerPoints &7已兼容.")
            points = (plugin as PlayerPoints).api
        }
    }

    fun givePoints(player: UUID, amt: Int) {
        points?.give(player, amt) ?: GeekMail.say("&c点券 &fPlayerPoints &c未安装，请检查点券插件！")
    }

    fun takePoints(player: UUID, amt: Int) {
        points?.take(player, amt) ?: GeekMail.say("&c点券 &fPlayerPoints &c未安装，请检查点券插件！")
    }

    fun hasPoints(player: UUID, amt: Int): Boolean {
        val a = points?.look(player) ?: 0.also {
            GeekMail.say("&c点券 &fPlayerPoints &c未安装，请检查点券插件！")
            return false
        }
        return a >= amt
    }
    fun hasTakePoints(player: UUID, amt: Int): Boolean {
        return if (hasPoints(player, amt)) {
            takePoints(player, amt)
            true
        } else false
    }

}