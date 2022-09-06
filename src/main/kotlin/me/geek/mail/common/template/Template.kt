package me.geek.mail.common.template

import com.google.common.base.Joiner
import me.geek.mail.GeekMail.instance
import me.geek.mail.GeekMail.say
import me.geek.mail.common.template.Sub.Temp
import me.geek.mail.GeekMail
import me.geek.mail.api.utils.HexUtils
import me.geek.mail.common.template.Sub.TempPack
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import taboolib.common.platform.function.releaseResourceFile
import taboolib.module.configuration.SecuredFile
import java.io.File
import java.util.*
import kotlin.system.measureTimeMillis

/**
 * 作者: 老廖
 * 时间: 2022/8/7
 */
object Template {
    private val TEMP_PACK_MAP: MutableMap<String, Temp> = HashMap()

    fun onLoad() {
        val list = mutableListOf<File>()
        measureTimeMillis {
            TEMP_PACK_MAP.clear()
            list.also {
                it.addAll(forFile(saveDefaultMenu))
            }
            var packID: String
            var condition: String
            var action: String
            var deny: String
            var title: String
            var text: String
            var type: String
            var additional: String
            var items: String?
            var command: String?
            list.forEach { file ->
                val var1 = SecuredFile.loadConfiguration(file)
                packID = var1.getString("Template.ID")!!
                condition = var1.getString("Template.Require.condition", "false")!!
                action = var1.getString("Template.Require.action", "null")!!.replace("&", "§")
                deny = var1.getString("Template.Require.deny", "null")!!.replace("&", "§")
                title = HexUtils.colorify(var1.getString("Template.package.title")!!)
                text = HexUtils.colorify(var1.getString("Template.package.text")!!.replace("\n", ""))
                type = var1.getString("Template.package.type")!!.uppercase(Locale.ROOT)
                additional = var1.getString("Template.package.appendix.additional", "0")!!
                items = null
                command = Joiner.on(";").join(var1.getStringList("Template.package.appendix.command"))
                TEMP_PACK_MAP[packID] = TempPack(packID, condition, action, deny, title, text, type, additional, items, command)
            }
        }.also {
            say("§7邮件模板加载完成 §8(耗时 $it ms)")
        }
    }


    val tempPackMap: Map<String, Temp>
        get() = TEMP_PACK_MAP

    fun getTempPack(key: String): Temp {
        return TEMP_PACK_MAP[key]!!
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
    private val saveDefaultMenu by lazy {
        val dir = File(instance.dataFolder, "template")
        if (!dir.exists()) {
            arrayOf(
                "template/def.yml",
                "template/def2.yml",
                "template/def3.yml"
            ).forEach { releaseResourceFile(it, true) }
        }
        dir
    }
}