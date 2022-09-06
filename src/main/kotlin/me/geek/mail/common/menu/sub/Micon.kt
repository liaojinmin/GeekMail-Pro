package me.geek.mail.common.menu.sub

import me.geek.mail.api.utils.HexUtils
import taboolib.library.configuration.ConfigurationSection
import java.util.*


/**
 * 作者: 老廖
 * 时间: 2022/7/5
 */
class Micon(
    val icon: String,
    val type: IconType,
    val mats: String,
    val data: Int,
    val name: String,
    val lore: List<String>
) {
    constructor(icon: String, obj: ConfigurationSection) : this(
        icon,
        IconType.valueOf(obj.getString("Type", "NORMAL")!!.uppercase(Locale.ROOT)),
        obj.getString("display.mats","PAPER")!!,
        obj.getInt("display.data",0),
        HexUtils.colorify(obj.getString("display.name", " ")!!),
        HexUtils.colorify(obj.getStringList("display.lore").joinToString()).split(", "),
    )
}