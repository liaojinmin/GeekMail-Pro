package me.geek.mail.common.customevent

import me.geek.mail.GeekMail
import me.geek.mail.api.mail.MailManage
import me.geek.mail.common.kether.sub.KetherAPI

import me.geek.mail.common.template.Template

import me.geek.mail.utils.deserializeItemStacks
import me.geek.mail.utils.forFile
import org.bukkit.entity.Player
import taboolib.common.platform.function.releaseResourceFile

import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Configuration.Companion.getObject
import taboolib.platform.compat.replacePlaceholder
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import kotlin.system.measureTimeMillis

/**
 * 作者: 老廖
 * 时间: 2022/10/3
 *
 **/
object Event {
    private val EventPackCache = ConcurrentHashMap<String, EventPack>()
    fun get(name: String): EventPack? {
        return EventPackCache[name]
    }
    fun get(): ConcurrentHashMap<String, EventPack> {
        return EventPackCache
    }


    fun onloadEventPack() {
        val list = mutableListOf<File>()
        measureTimeMillis {
            list.addAll(forFile(saveDefaultEvent))
            list.forEach { file ->
                val event = Configuration.loadFromFile(file).getObject<EventPack>("event", false)
                EventPackCache[event.event] = event
            }

        }.also {
            GeekMail.say("§7已加载 &f${list.size} &7个自定义事件... §8(耗时 $it ms)")
        }
    }



    fun checkEventType(eve: String): Boolean {
        EventType.values().forEach {
            if (it.name == eve) {
                return true
            }
        }
        return false
    }


    fun runAction(player: Player, action: MutableList<String>) {
        action.forEach {
            val a = it.split(": ")
            when (a[0]) {
                "sendTemPlate" -> {
                    Template.getAdminPack(a[1])?.let { pack ->
                        MailManage.buildMail(pack.type,
                            "title" to pack.title.replacePlaceholder(player),
                            "text" to pack.text.replacePlaceholder(player),
                            "target" to player.uniqueId,
                            "additional" to pack.additional,
                            "itemStacks" to pack.itemStacks?.deserializeItemStacks(),
                            "command" to pack.command
                        ).sendMail()
                    }
                }
                else -> KetherAPI.instantKether(player, it)
            }
        }
    }




    private val saveDefaultEvent by lazy {
        val dir = File(GeekMail.instance.dataFolder, "event")
        if (!dir.exists()) {
            arrayOf(
                "event/new_player_join.yml",
            ).forEach { releaseResourceFile(it, true) }
        }
        dir
    }
}