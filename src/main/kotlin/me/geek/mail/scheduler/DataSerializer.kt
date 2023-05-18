package me.geek.mail.scheduler

import com.google.gson.*
import com.google.gson.annotations.Expose
import me.geek.mail.api.data.PlayerData
import me.geek.mail.api.mail.MailManage
import me.geek.mail.api.mail.MailState
import me.geek.mail.api.mail.MailSub
import me.geek.mail.common.market.Item
import me.geek.mail.utils.deserializeItemStacks
import org.bukkit.Bukkit
import org.xerial.snappy.Snappy
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

/** 反序列化 **/

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
                    "senderTime" to a.get("senderTime").asLong,
                    "getTime" to (a.get("getTime")?.asLong ?: "0"),
                    "additional" to (a.get("additional")?.asString ?: "0"),
                    "itemStacks" to (a.get("itemStackString")?.asString ?: "").deserializeItemStacks(),
                    "command" to (a.get("command")?.let {  elem ->
                        Gson().fromJson(elem, ArrayList<String>()::class.java)
                    } ?: emptyList())
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

fun ByteArray.toPlayerData(): PlayerData {
    val gson = GsonBuilder().setExclusionStrategies(Exclude())
    gson.registerTypeAdapter(MailPlayerData::class.java, UnSerializePlayerData())
    return gson.create().fromJson(String(Snappy.uncompress(this), charset = Charsets.UTF_8), MailPlayerData::class.java)
}

class UnSerializePlayerData: JsonDeserializer<PlayerData> {
    override fun deserialize(json: JsonElement, p1: Type, p2: JsonDeserializationContext?): PlayerData {
        val jsonObject = json.asJsonObject
        val bing = jsonObject.get("mail")?.asString ?: ""
        val uuid = UUID.fromString(jsonObject.get("uuid")?.asString)
        val new = jsonObject.get("newPlayer")?.asBoolean ?: true
        val mailData = mutableListOf<MailSub>().apply {
            jsonObject.get("mailData")?.asJsonArray?.let { mail ->
                if (mail.size() != 0) {
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
                                "additional" to (a.get("additional")?.asString ?: "0"),
                                "itemStacks" to (a.get("itemStackString")?.asString ?: "").deserializeItemStacks(),
                                "command" to (a.get("command")?.let {  elem ->
                                    Gson().fromJson(elem, ArrayList<String>()::class.java)
                                } ?: emptyList())
                            )
                            add(mailSub)
                        }
                    }
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