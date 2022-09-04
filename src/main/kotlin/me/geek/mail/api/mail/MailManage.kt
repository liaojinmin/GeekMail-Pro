package me.geek.mail.api.mail


import me.geek.mail.GeekMail.debug

import me.geek.mail.GeekMail.say
import taboolib.common.platform.function.adaptPlayer

import me.geek.mail.GeekMail
import me.geek.mail.Configuration.LangManager
import taboolib.module.chat.TellrawJson
import me.geek.mail.Configuration.ConfigManager
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.jetbrains.annotations.NotNull
import taboolib.library.reflex.Reflex.Companion.invokeConstructor
import taboolib.library.xseries.XSound
import java.io.IOException
import java.lang.IllegalArgumentException
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.HashMap

/**
 * 作者: 老廖
 * 时间: 2022/7/29d
 */
object MailManage {
    private val senderCache: MutableMap<UUID, MutableList<MailSub>> = ConcurrentHashMap()
    private val targetCache: MutableMap<UUID, MutableList<MailSub>> = ConcurrentHashMap()
    private val MailData: MutableMap<String, MailSub> = HashMap()

    fun register(@NotNull mail: MailSub) {
      MailData[mail.name] = mail
    }
    fun getMailData(mailType: String) : MailSub? =
        MailData[mailType]
    @JvmStatic
    fun getMailDataMap(): MutableMap<String, MailSub> {
        return MailData
    }
    @JvmStatic
    fun buildMailClass(mailID: UUID, mailType: String, title: String, text: String, sender: UUID, target: UUID,
                       state: String , args: String, item: Array<ItemStack>, cmd: List<String>, time: Array<String>): MailSub? {
        debug("邮件ID: $mailID")
        debug("邮件种类: $mailType")
        debug("邮件标题: $title")
        debug("邮件文本: $text")
        debug("邮件发送者: $sender")
        debug("邮件接收者: $target")
        debug("已注册邮件查找: ${MailData[mailType]}")
        return MailData[mailType]?.javaClass?.invokeConstructor(
            mailID, title, text, sender, target, state ,args, item, cmd, time
        )
    }

    /**
     * 此方法会先判断缓存中是否存在对应 目标UID 的数据 再进行存入
     *
     * @param senderUuid 发送者ID
     * @param mail 邮件
     */
    fun addSenderCache(senderUuid: UUID, mail: MailSub) {
        if (senderCache.containsKey(senderUuid)) {
            senderCache.forEach { (key: UUID, value: MutableList<MailSub>) ->
                if (key == senderUuid) {
                    value.add(mail)
                    return
                }
            }
        } else {
            val mail1: MutableList<MailSub> = ArrayList()
            mail1.add(mail)
            senderCache[senderUuid] = mail1
        }
    }

    /**
     * 此方法会先判断缓存中是否存在对应 目标UID 的数据 再进行存入
     *
     * @param targetUuid 目标ID
     * @param mail 邮件
     */
    @JvmStatic
    fun addTargetCache(targetUuid: UUID, mail: MailSub) {
        if (targetCache.containsKey(targetUuid)) {
            debug("addTargetCache-已存在缓存-UUID: " + targetUuid + " 邮件ID：" + mail.mailID)
            targetCache.forEach { (key: UUID, value: MutableList<MailSub>) ->
                if (key == targetUuid) {
                    value.add(mail)
                    debug("addTargetCache-已存在缓存.新增-UUID: " + targetUuid + " 邮件ID：" + mail.mailID)
                }
            }
        } else {
            debug("addTargetCache-不在缓存-UUID: " + targetUuid + " 邮件ID：" + mail.mailID)
            val mail1: MutableList<MailSub> = ArrayList()
            mail1.add(mail)
            targetCache[targetUuid] = mail1
            debug("addTargetCache-不在缓存.新增-UUID: " + targetUuid + " 邮件ID：" + mail.mailID)
        }
    }

    // 直接替换缓存中的数据
    fun UpTargetCache(targetUuid: UUID, mail: MutableList<MailSub>) {
        targetCache[targetUuid] = mail
    }

    fun remTargetCache(targetUuid: UUID) {
        targetCache.remove(targetUuid)
    }

    fun remIndexTofTarget(targetUuid: UUID, mailID: UUID) {
        if (targetCache.containsKey(targetUuid)) {
            targetCache.forEach { (key: UUID, value: MutableList<MailSub>) ->
                if (key == targetUuid) {
                    value.removeIf { it.mailID == mailID }
                }
            }
        } else {
            say("缓存 null 异常")
        }
    }


    fun getTargetCache(uuid: UUID): MutableList<MailSub> {
        if (targetCache.containsKey(uuid)) {
            return ArrayList(targetCache[uuid]!!)
        }
        return ArrayList()
    }

    fun hasTargetCache(uuid: UUID): Boolean {
        return targetCache.containsKey(uuid)
    }

    @JvmStatic
    fun sendMailMessage(title: String, text: String, vararg player: Player?) {
        try {
            if (player[0] != null) {
                for (msg in LangManager.SENDER_MSG) {
                    if (msg.contains("[target]")) {
                        if (player[1] == null) {
                            player[0]?.sendMessage(msg.replace("[target]", ""))
                        } else {
                            player[0]!!.sendMessage(msg.replace("[target]", player[1]!!.name))
                        }
                    } else {
                        player[0]!!.sendMessage(msg)
                    }
                    Sound(player[0]!!, "BLOCK_NOTE_BLOCK_HARP", 1f, 1f)
                }
            }
            if (player[1] != null) {
                val json = TellrawJson()
                val proxyPlayer = adaptPlayer(player[1]!!)
                for (msg in LangManager.TARGET_MSG) {
                    if (msg.contains("[title]")) {
                        json.append(msg.replace("[title]", title +"\n"))
                            .hoverText(text+"\n")
                            .runCommand("/" + GeekMail.menu.cmd)
                    } else {
                        json.append(msg+"\n")
                    }
                }
                /**
                 * if (MinecraftVersion.INSTANCE.getMajorLegacy() >= 11300) {
                 * NMSKt.sendToast(player[1], Material.BOOK,"你有新的邮件待查看！", ToastFrame.TASK, ToastBackground.END);
                 * } */
                proxyPlayer.playSound(proxyPlayer.location, "BLOCK_NOTE_BLOCK_HARP", 1f, 1f)
                proxyPlayer.sendRawMessage(json.toRawMessage())
            }
        } catch (ignored: IllegalArgumentException) { }
    }

    fun createBlock(location: String?) {
        try {
            val data: FileConfiguration = YamlConfiguration.loadConfiguration(ConfigManager.getYml())
            data["Block"] = location
            data.save(ConfigManager.getYml())
            ConfigManager.location = location
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun Sound(player: Player, name: String, volume: Float, potch: Float) {
        val sound: XSound = try {
            XSound.valueOf(name)
        } catch (e: Throwable) {
            say("未知音效: $name")
            return
        }
        sound.play(player, volume, potch)
    }
}