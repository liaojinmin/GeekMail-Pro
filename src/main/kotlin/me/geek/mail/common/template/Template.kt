package me.geek.mail.common.template

import com.google.common.base.Joiner
import ink.ptms.um.Mythic
import ink.ptms.um.impl5.Mythic5
import me.geek.mail.GeekMail
import me.geek.mail.GeekMail.instance
import me.geek.mail.GeekMail.say
import me.geek.mail.api.hook.HookPlugin


import me.geek.mail.common.template.Sub.Temp
import me.geek.mail.common.template.Sub.TempPack
import me.geek.mail.utils.colorify
import me.geek.mail.utils.serializeItemStacks
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.releaseResourceFile
import taboolib.library.xseries.XMaterial
import taboolib.module.configuration.SecuredFile
import taboolib.platform.util.buildItem
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.system.measureTimeMillis

/**
 * 作者: 老廖
 * 时间: 2022/8/7
 */
object Template {

    private val TEMP_PACK_MAP: MutableMap<String, Temp> = HashMap()
    private val SERVER_PACK_MAP: MutableMap<String, Temp> = HashMap()

    fun onLoad() {
            val list = mutableListOf<File>()
            measureTimeMillis {
                TEMP_PACK_MAP.clear()
                SERVER_PACK_MAP.clear()
                list.also {
                    it.addAll(forFile(saveDefaultTemp))
                }
                list.forEach { file ->
                    val var1 = SecuredFile.loadConfiguration(file)
                    val packID: String = var1.getString("Template.ID")!!

                    val condition: String = var1.getString("Template.Require.condition", "false") ?: ""
                    val action: String = var1.getString("Template.Require.action", "null")?.replace("&", "§") ?: ""
                    val deny: String = var1.getString("Template.Require.deny", "null")?.replace("&", "§") ?: ""

                    val title: String = var1.getString("Template.package.title")!!.colorify()
                    val text: String = var1.getString("Template.package.text")!!.colorify().replace("\n", "")
                    val type: String = var1.getString("Template.package.type")!!.uppercase(Locale.ROOT)
                    val additional: String = var1.getString("Template.package.appendix.additional", "0")!!
                    val items: String = buildItemsString(var1.getStringList("Template.package.appendix.items"))
                    val command: String = Joiner.on(";").join(var1.getStringList("Template.package.appendix.command"))
                    if (var1.getBoolean("Template.Server")) {
                        SERVER_PACK_MAP[packID] = TempPack(packID, condition, action, deny, title, text, type, additional, items, command)
                    } else TEMP_PACK_MAP[packID] = TempPack(packID, condition, action, deny, title, text, type, additional, items, command)
                }
            }.also {
                say("§7已加载 &f${list.size} &7个邮件模板... §8(耗时 $it Ms)")
            }
    }


    // 玩家指令展示模板
    val tempPackMap: Map<String, Temp>
        get() = TEMP_PACK_MAP

    // 管理员指令展示模板
    val adminPack by lazy {
        mutableListOf<String>().also { list ->
            list.addAll(SERVER_PACK_MAP.map { it.key })
            list.addAll(TEMP_PACK_MAP.map { it.key })
        }
    }


    fun getTempPack(key: String): Temp {
        return TEMP_PACK_MAP[key]!!
    }

    fun getAdminPack(key: String): Temp? {
        return SERVER_PACK_MAP[key] ?: TEMP_PACK_MAP[key]
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
    private val saveDefaultTemp by lazy {
        val dir = File(instance.dataFolder, "template")
        if (!dir.exists()) {
            arrayOf(
                "template/def.yml",
                "template/def2.yml",
                "template/def3.yml",
                "template/items.yml",
                "template/mail_cmd.yml",
                "template/mail_Normal.yml"
            ).forEach { releaseResourceFile(it, true) }
        }
        dir
    }

    private fun buildItemsString(items: List<String>): String {
        if (items.isNotEmpty()) {
            val item: MutableList<ItemStack> = ArrayList()
            items.forEach { m ->
                m.split(";").forEach {
                    val args = it.split(",")
                    item.add(
                        if (it.contains(mats)) {
                            buildItem(XMaterial.STONE) {
                                args.forEach { a ->
                                    when {
                                        a.contains(mats) -> setMaterial(XMaterial.valueOf(a.replace(mats, "").uppercase()))
                                        a.contains(Name) -> name = a.replace(Name, "").colorify()
                                        a.contains(Lore) -> lore.addAll(a.replace(Lore, "").colorify().split("\n"))
                                        a.contains(data) -> damage = a.replace(data, "").toIntOrNull() ?: 0
                                        a.contains(amt) -> amount = a.replace(amt, "").toIntOrNull() ?: 1
                                        a.contains(mode) -> customModelData = a.replace(mode, "").toIntOrNull() ?: 0
                                    }
                                }
                            }
                        } else when {
                            it.contains(IA) -> {
                                HookPlugin.itemsAdder.getItem(args[0].replace(IA,"")).also { stack ->
                                    stack.amount = args[1].replace(IA,"").toIntOrNull() ?: 1
                                }
                            }
                            it.contains(MM) -> {
                                if (HookPlugin.mythicMobs.isHook) {
                                    HookPlugin.mythicMobs.getItem(args[0].replace(MM,"")).also { stack ->
                                        stack.amount = args[1].replace(amt,"").toIntOrNull() ?: 1
                                    }
                                } else ItemStack(Material.STONE)
                            }
                            else -> { ItemStack(Material.STONE) }
                        }
                    )
                }
            }
            return item.toTypedArray().serializeItemStacks()
        }
        return "null"
    }
    private val IA = Regex("(IA|ia|ItemsAdder):")
    private val MM = Regex("(MM|mm|MythicMobs):")
    private val mats = Regex("(material|mats):")
    private val Name = Regex("name:")
    private val Lore = Regex("lore:")
    private val data = Regex("data:")
    private val amt = Regex("(amount|amt):")
    private val mode = Regex("ModelData:")
}