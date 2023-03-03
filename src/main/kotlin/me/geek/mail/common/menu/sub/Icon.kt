package me.geek.mail.common.menu.sub

import me.clip.placeholderapi.PlaceholderAPI
import me.geek.mail.utils.colorify
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.library.configuration.ConfigurationSection
import java.util.*


/**
 * 作者: 老廖
 * 时间: 2022/7/5
 */
class Icon(
    val icon: Char,
    val iconType: IconType,
    val mats: String,
    val data: Int,
    val name: String,
    val lore: List<String>,
    val sound: Array<String>,
    val command: List<String>
) {
    constructor(icon: Char, obj: ConfigurationSection) : this(
        icon,
        IconType.valueOf(obj.getString("Type", "NORMAL")!!.uppercase(Locale.ROOT)),
        obj.getString("display.mats","PAPER")!!,
        obj.getInt("display.data",0),
        obj.getString("display.name", " ")!!.colorify(),
        obj.getStringList("display.lore").joinToString().colorify().split(", "),
        obj.getString("display.sound")?.split("-")?.toTypedArray() ?: arrayOf("BLOCK_NOTE_BLOCK_BIT","1","1"),
        obj.getStringList("display.command")
    )

    fun executeCmd(player: Player): Boolean {
        for (cmd in command) {
            val a = cmd.split(": ")
            if (a.size < 2) continue
            if (a[0].equals("console", ignoreCase = true)) {
                Bukkit.dispatchCommand(
                    Bukkit.getConsoleSender(), PlaceholderAPI.setPlaceholders(
                        player,
                        a[1]
                    )
                )
            } else if (a[0].equals("op", ignoreCase = true)) {
                if (player.isOp) {
                    Bukkit.dispatchCommand(player, PlaceholderAPI.setPlaceholders(player, a[1]))
                } else {
                    try {
                        player.isOp = true
                        Bukkit.dispatchCommand(player, PlaceholderAPI.setPlaceholders(player, a[1]))
                    } catch (ignored: Exception) {
                    } finally {
                        player.isOp = false
                    }
                }
            } else if (a[0].equals("player", ignoreCase = true)) {
                Bukkit.dispatchCommand(player, PlaceholderAPI.setPlaceholders(player, a[1]))
            }
        }
        return false
    }
}