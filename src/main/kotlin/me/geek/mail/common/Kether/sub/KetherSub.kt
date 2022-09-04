package me.geek.mail.common.Kether.sub

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.module.kether.ScriptAction
import taboolib.library.kether.QuestContext

import taboolib.module.kether.ScriptContext
import java.lang.RuntimeException

/**
 * 作者: 老廖
 * 时间: 2022/8/8
 */
abstract class KetherSub<T> : ScriptAction<T>() {

    fun getPlayer(text: QuestContext.Frame): Player {
        val proxiedCommandSender = (text.context() as ScriptContext).sender
            ?: throw RuntimeException("错误 - 无玩家")
        return Bukkit.getPlayer(proxiedCommandSender.name)!!
    }
}