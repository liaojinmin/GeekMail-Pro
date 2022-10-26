package me.geek.mail.utils

import com.google.gson.*
import com.google.gson.annotations.Expose
import me.geek.mail.api.mail.MailManage
import me.geek.mail.api.mail.MailSub
import me.geek.mail.common.data.MailPlayerData
import java.lang.reflect.Type
import java.util.*


/**
 * 作者: 老廖
 * 时间: 2022/10/15
 *
 * 这辈子也不想碰序列化
 **/
object ClassSerializable {
    /**
     * Gson 序列化
     * @param objs 可序列化类
     */
    fun gsonSerialize(objs: Any): ByteArray {
        return toJson(objs).toByteArray(charset = Charsets.UTF_8)
    }

    fun gsonUnSerialize(objs: ByteArray, obj: Class<*>): Any {
        val gson = GsonBuilder().setExclusionStrategies(A())
        return gson.create().fromJson(String(objs, charset = Charsets.UTF_8), obj)
    }

    fun gsonUnSerialize(objs: ByteArray, obj: Class<*>, isMailPlayerData: Boolean): Any {
        val gson = GsonBuilder().setExclusionStrategies(A())
        if (isMailPlayerData) {
            gson.registerTypeAdapter(MailPlayerData::class.java, UnSerializeMailPlayerData())
        } else gson.registerTypeAdapter(MailSub::class.java, UnSerializeMail())

        return gson.create().fromJson(String(objs, charset = Charsets.UTF_8), obj)
    }

    private fun toJson(objs: Any): String {
        return GsonBuilder()
            .setExclusionStrategies(A())
            .create().toJson(objs)
    }


    class A : ExclusionStrategy {
        override fun shouldSkipField(f: FieldAttributes): Boolean {
            return f.getAnnotation(Expose::class.java) != null
        }

        override fun shouldSkipClass(clazz: Class<*>): Boolean {
            return clazz.getAnnotation(Expose::class.java) != null
        }

    }

    class UnSerializeMailPlayerData : JsonDeserializer<MailPlayerData> {
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): MailPlayerData {
            val jsonObject = json.asJsonObject
            // 拿到邮件数组
            val mail = jsonObject.get("mailData").asJsonArray
            val mailData = mutableListOf<MailSub>().apply {
                if (!mail.isEmpty) {
                    // 循环每一个邮件
                    mail.forEach {
                        val a = it.asJsonObject
                        val cmd = if (a.get("command") != null) a.get("command").asString else ""
                        val time = if (a.get("getTime") != null) a.get("getTime").asString else "0"
                        val items = if (a.get("itemStackString") != null) a.get("itemStackString").asString else ""
                        val add = if (a.get("additional") != null) a.get("additional").asString else ""
                        MailManage.buildMailClass(a.get("mailID").asString, a.get("name").asString, a.get("title").asString, a.get("text").asString,
                            a.get("sender").asString, a.get("target").asString, a.get("state").asString, add, a.get("senderTime").asString,
                            time, items, cmd,
                        )?.let { it2 ->
                            add(it2)
                        }
                    }
                }
            }
            return MailPlayerData(
                jsonObject.get("name").asString,
                UUID.fromString(jsonObject.get("uuid").asString),
                jsonObject.get("mail").asString,
                jsonObject.get("OneJoin").asBoolean,
                mailData
            )

        }
    }
    class UnSerializeMail : JsonDeserializer<MailSub> {
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): MailSub {
            val a = json.asJsonObject
            val cmd = if (a.get("command") != null) a.get("command").asString else ""
            val time = if (a.get("getTime") != null) a.get("getTime").asString else "0"
            val items = if (a.get("itemStackString") != null) a.get("itemStackString").asString else ""
            val add = if (a.get("additional") != null) a.get("additional").asString else ""
            return MailManage.buildMailClass(a.get("mailID").asString, a.get("name").asString, a.get("title").asString, a.get("text").asString,
                a.get("sender").asString, a.get("target").asString, a.get("state").asString, add, a.get("senderTime").asString,
                time, items, cmd,
            )!!
        }
    }

}
fun Any.classSerializable(): ByteArray {
    return ClassSerializable.gsonSerialize(this)
}

fun ByteArray.classUnSerializable(obj: Class<*>, isMailPlayerData: Boolean) : Any {
    return ClassSerializable.gsonUnSerialize(this, obj, isMailPlayerData)
}
fun ByteArray.classUnSerializable(obj: Class<*>) : Any {
    return ClassSerializable.gsonUnSerialize(this, obj)
}


