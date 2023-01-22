package me.geek.mail.common.menu.sub

import me.geek.mail.utils.colorify
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
    val sound: Array<String>
) {
    constructor(icon: Char, obj: ConfigurationSection) : this(
        icon,
        IconType.valueOf(obj.getString("Type", "NORMAL")!!.uppercase(Locale.ROOT)),
        obj.getString("display.mats","PAPER")!!,
        obj.getInt("display.data",0),
        obj.getString("display.name", " ")!!.colorify(),
        obj.getStringList("display.lore").joinToString().colorify().split(", "),
        obj.getString("display.sound")?.split("-")?.toTypedArray() ?: arrayOf("BLOCK_NOTE_BLOCK_BIT","1","1")
    )
}