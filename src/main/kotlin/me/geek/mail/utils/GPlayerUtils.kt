package me.geek.mail.utils

import me.geek.mail.GeekMail
import org.bukkit.Material
import org.bukkit.entity.Player

/**
 * 作者: 老廖
 * 时间: 2022/11/26
 *
 **/

fun Player.getEmptySlot(hasEquipment: Boolean = true, isItemAmount: Boolean = false): Int {
    var air = 0
    for (itemStack in inventory.contents) {
        if (itemStack == null || itemStack.type == Material.AIR) { air++ }
    }
    if (hasEquipment) {
        if (GeekMail.BukkitVersion > 1120) {
            if (inventory.itemInOffHand.type == Material.AIR) air--
            if (inventory.helmet == null) air--
            if (inventory.chestplate == null) air--
            if (inventory.leggings == null) air--
            if (inventory.boots == null) air--
        }
    }
    return if (isItemAmount) air * 64 else air
}