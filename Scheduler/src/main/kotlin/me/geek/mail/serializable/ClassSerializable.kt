package me.geek.mail.serializable

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream


/**
 * 作者: 老廖
 * 时间: 2022/10/15
 *
 **/
object ClassSerializable {
    fun serialize(objs: Any): ByteArray? {
        val b = ByteArrayOutputStream()
        val a = ObjectOutputStream(b)
        a.writeObject(objs)
        return b.toByteArray()
    }
    fun unSerialize(byteArray: ByteArray) : Any {
        val a = ByteArrayInputStream(byteArray)
        val b = ObjectInputStream(a)
        return b.readObject()
    }

}