package me.geek.mail.scheduler

import com.google.gson.*
import com.google.gson.annotations.Expose
import me.geek.mail.api.data.PlayerData
import me.geek.mail.api.mail.Mail
import me.geek.mail.api.mail.MailManage
import me.geek.mail.api.mail.MailState
import me.geek.mail.api.mail.MailSub
import me.geek.mail.common.market.Item
import me.geek.mail.utils.deserializeItemStacks
import me.geek.mail.utils.serializeItemStacks
import org.bukkit.Bukkit
import taboolib.common.util.asList
import taboolib.library.reflex.Reflex.Companion.invokeConstructor
import taboolib.library.reflex.Reflex.Companion.setProperty
import java.lang.reflect.Type
import java.util.*


/**
 * 作者: 老廖
 * 时间: 2023/1/21
 *
 **/


fun ByteArray.toMarketData(): Item {
    return GsonBuilder().setExclusionStrategies(Exclude())
        .create().fromJson(String(this, charset = Charsets.UTF_8), Item::class.java)
}
fun Item.toByteArray(): ByteArray {
    return GsonBuilder()
        .setExclusionStrategies(Exclude())
        .create().toJson(this).toByteArray(charset = Charsets.UTF_8)
}

/**  单类邮件序列化 与 反序列化 **/
fun MailSub.toByteArray(): ByteArray {
    if (this.itemStacks != null) {
        this.itemStackString = this.itemStacks.serializeItemStacks()
    }
    val gson = GsonBuilder().setExclusionStrategies(Exclude())
    return gson.create().toJson(this).toByteArray(charset = Charsets.UTF_8)
}
fun ByteArray.toMailSub(): MailSub {
    val gson = GsonBuilder().setExclusionStrategies(Exclude())
    gson.registerTypeAdapter(MailSub::class.java, object : JsonDeserializer<MailSub> {
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext?): MailSub {
            val a = json.asJsonObject
            return MailManage.getMailClass(a.get("name").asString)?.let { mailSub ->
                setFields(mailSub,
                    "mailID" to UUID.fromString(a.get("mailID")!!.asString),
                    "title" to (a.get("title")?.asString ?: ""),
                    "text" to (a.get("text")?.asString ?: ""),
                    "sender" to UUID.fromString(a.get("sender").asString),
                    "target" to UUID.fromString(a.get("target").asString),
                    "state" to MailState.valueOf(a.get("state").asString),
                    "senderTime" to a.get("senderTime").asString,
                    "getTime" to (a.get("getTime")?.asString ?: "0"),
                    "appendixInfo" to a.get("appendixInfo").asString,
                    "additional" to (a.get("additional")?.asString ?: ""),
                    "itemStacks" to (a.get("itemStackString")?.asString ?: "").deserializeItemStacks(),
                    "command" to (a.get("command")?.asString ?: "").split(";")
                )
                mailSub
            } ?: error("""
                序列化邮件信息出现问题，请检查sql服务器，并报告错误。。。
            """.trimIndent())
        }
    })
    return gson.create().fromJson(String(this, charset = Charsets.UTF_8), MailSub::class.java)
}


/**  玩家数据序列化 与 反序列化 **/
fun PlayerData.toJson(): String {
    return GsonBuilder()
        .setExclusionStrategies(Exclude())
        .create().toJson(this)
}
fun ByteArray.toPlayerData(): PlayerData {
    val gson = GsonBuilder().setExclusionStrategies(Exclude())
    gson.registerTypeAdapter(MailPlayerData::class.java, UnSerializePlayerData())
    return gson.create().fromJson(String(this, charset = Charsets.UTF_8), MailPlayerData::class.java)
}

class UnSerializePlayerData: JsonDeserializer<PlayerData> {
    override fun deserialize(json: JsonElement, p1: Type, p2: JsonDeserializationContext?): PlayerData {
        val jsonObject = json.asJsonObject
        val bing = jsonObject.get("mail")?.asString ?: ""
        val uuid = UUID.fromString(jsonObject.get("uuid")?.asString)
        val new = jsonObject.get("newPlayer")?.asBoolean ?: true
        val mailData = mutableListOf<MailSub>().apply {
            val mail = jsonObject.get("mailData").asJsonArray
            if (!mail.isEmpty) {
                // 循环每一个邮件
                mail.forEach {
                    val a = it.asJsonObject
                    MailManage.getMailClass(a.get("name").asString)?.let { mailSub ->
                        setFields(mailSub,
                            "mailID" to UUID.fromString(a.get("mailID")!!.asString),
                            "title" to (a.get("title")?.asString ?: ""),
                            "text" to (a.get("text")?.asString ?: ""),
                            "sender" to UUID.fromString(a.get("sender").asString),
                            "target" to UUID.fromString(a.get("target").asString),
                            "state" to MailState.valueOf(a.get("state").asString),
                            "senderTime" to a.get("senderTime").asLong,
                            "getTime" to (a.get("getTime")?.asLong ?: "0"),
                            "appendixInfo" to a.get("appendixInfo").asString,
                            "additional" to (a.get("additional")?.asString ?: "0"),
                            "itemStacks" to (a.get("itemStackString")?.asString ?: "").deserializeItemStacks(),
                            "command" to (a.get("command")?.asList())
                            )
                        add(mailSub)
                    }
                    //val mailID = UUID.fromString(a.get("mailID")!!.asString)
                    //# val mailType = a.get("mailType").asString
                    //# val mailIcon
                    //val title = a.get("title")?.asString ?: ""
                    //val text = a.get("text")?.asString ?: ""
                    //val sender = UUID.fromString(a.get("sender").asString)
                    //val target = UUID.fromString(a.get("target").asString)
                    //val state = MailState.valueOf(a.get("state").asString)
                    //val senderTime = a.get("senderTime").asString
                    //val getTime = a.get("getTime")?.asString ?: "0"
                    //val appendixInfo = a.get("appendixInfo").asString
                    //# val permission = a.get("permission").asString
                    //val additional = a.get("additional")?.asString ?: ""
                    //val itemStacks = a.get("itemStackString")?.asString ?: ""
                    //val command = a.get("command")?.asString ?: ""
                }
            }
        }
        val player = Bukkit.getPlayer(uuid) ?: error("""
            未找到该玩家，请联系开发者报告此错误
               定位 -> UnSerializePlayerData()
               UUID = "$uuid"
            """.trimIndent())
        return MailPlayerData(
            player,
            bing,
            mailData
        ).apply {
            newPlayer = new
        }
    }
}


class Exclude : ExclusionStrategy {
    override fun shouldSkipField(f: FieldAttributes): Boolean {
        return f.getAnnotation(Expose::class.java) != null
    }

    override fun shouldSkipClass(clazz: Class<*>): Boolean {
        return clazz.getAnnotation(Expose::class.java) != null
    }
}

private fun setFields(any: MailSub, vararg fields: Pair<String, Any?>): MailSub {
    fields.forEach { (key, value) ->
        if (value != null) {
            any.setProperty(key, value)
        }
    }
    return any
}