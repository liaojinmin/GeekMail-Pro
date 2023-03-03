package me.geek.mail.utils

import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException

/**
 * 作者: 老廖
 * 时间: 2022/7/28
 */


/**
 * 序列化 ItemStack 为字符串 YML 实现
 */
fun itemStackSerialize(itemStack: ItemStack): String {
    val yml = YamlConfiguration()
    yml["GeekStore"] = itemStack
    return yml.saveToString()
}
fun itemStackDeserialize(str: String): ItemStack? {
    val yml = YamlConfiguration()
    val item: ItemStack? = try {
        yml.loadFromString(str)
        yml.getItemStack("GeekStore")
    } catch (ex: InvalidConfigurationException) {
        error("物品库获取错误...")
    }
    return item
}

/**
 * 序列化 ItemStack 为字符串
 */
fun ItemStack.serializeItemStacks(): String {
    val byteOutputStream = ByteArrayOutputStream()
    try {
        BukkitObjectOutputStream(byteOutputStream).use {
            it.writeInt(1)
            it.writeObject(serialize(this))
            return Base64Coder.encodeLines(byteOutputStream.toByteArray())
        }
    } catch (e: IOException) {
        throw IllegalArgumentException("无法序列化物品堆栈数据")
    }
}

/**
 * 序列化 ItemStack 数组 为字符串
 */
fun Array<ItemStack>?.serializeItemStacks(): String {
    if (this == null || this.isEmpty()) {
        return ""
    }
    val byteOutputStream = ByteArrayOutputStream()
    try {
        BukkitObjectOutputStream(byteOutputStream).use {
            it.writeInt(this.size)
            for (items in this) {
                it.writeObject(serialize(items))
            }
            return Base64Coder.encodeLines(byteOutputStream.toByteArray())
        }
    } catch (e: IOException) {
        throw IllegalArgumentException("无法序列化物品堆栈数据")
    }
}
/**
反序列化 获取单个物品stack
 */
fun String.deserializeItemStack(): ItemStack? {
    ByteArrayInputStream(Base64Coder.decodeLines(this)).use {
        BukkitObjectInputStream(it).use { var2 ->
            var2.readInt()
            return deserialize(var2.readObject())
        }
    }
}
/**
 * 反序列化字符串为  ItemStack 数组
 */
fun String.deserializeItemStacks(): Array<ItemStack> {
    if (this == "null" || this.isEmpty()) {
        return emptyArray()
    }
    ByteArrayInputStream(Base64Coder.decodeLines(this)).use {
        BukkitObjectInputStream(it).use { var2 ->
            val contents = arrayOfNulls<ItemStack>(var2.readInt())
            for ((index, _) in contents.withIndex()) {
                contents[index] = deserialize(var2.readObject())
            }
            return contents.filterNotNull().toTypedArray()
           }
    }
}



private fun deserialize(item: Any?): ItemStack? {
    return if (item != null) ItemStack.deserialize((item as Map<String, Any>)) else null
}

private fun serialize(item: ItemStack?): Map<String, Any>? {
    return item?.serialize()
}
