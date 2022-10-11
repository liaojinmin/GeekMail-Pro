package me.geek.mail.common.customevent

import me.geek.mail.GeekMail
import me.geek.mail.api.mail.MailManage
import me.geek.mail.common.customevent.sub.EventPack
import me.geek.mail.common.customevent.sub.EventType
import me.geek.mail.modules.settings.SetTings
import me.geek.mail.common.template.Template
import org.bukkit.entity.Player
import taboolib.common.platform.function.releaseResourceFile
import taboolib.library.reflex.Reflex.Companion.invokeConstructor
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Configuration.Companion.getObject
import taboolib.platform.compat.replacePlaceholder
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.system.measureTimeMillis

/**
 * 作者: 老廖
 * 时间: 2022/10/3
 *
 **/
object Event {
    private val EventPackCache = ConcurrentHashMap<String, EventPack>()

    fun onloadEventPack() {
        val list = mutableListOf<File>()
        measureTimeMillis {
            list.addAll(forFile(saveDefaultEvent))
            list.forEach { file ->
                val event = Configuration.loadFromFile(file).getObject<EventPack>("event", false)
                EventPackCache[event.id] = event
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

    fun get(): ConcurrentHashMap<String, EventPack> {
     return EventPackCache
    }

    fun runAction(player: Player, action: MutableList<String>) {
        action.forEach {
            val a = it.split(": ")
            when (a[0]) {
                "sendTemPlate" -> {
                    Template.getServerTempPack(a[1])?.let { pack ->
                        MailManage.getMailData(pack.type)?.javaClass?.invokeConstructor(
                            arrayOf(UUID.randomUUID().toString(), pack.title.replacePlaceholder(player), pack.text.replacePlaceholder(player),
                                SetTings.Console.toString(), player.uniqueId.toString(), "未提取",
                                pack.additional, System.currentTimeMillis().toString(), "0", pack.itemStacks, pack.command)
                        )?.sendMail()
                    }
                }
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

    private fun forFile(file: File): List<File> {
        return mutableListOf<File>().run {
            if (file.isDirectory) {
                file.listFiles()?.forEach {
                    addAll(forFile(it))
                }
            } else if (file.exists() && file.absolutePath.endsWith(".yml")) {
                add(file)
            }
            this
        }
    }

}