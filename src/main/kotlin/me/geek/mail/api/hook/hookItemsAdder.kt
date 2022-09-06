package me.geek.mail.api.hook

import dev.lone.itemsadder.api.Events.ItemsAdderFirstLoadEvent
import me.geek.mail.GeekMail
import me.geek.mail.GeekMail.say
import me.geek.mail.common.menu.Menu
import taboolib.common.platform.event.SubscribeEvent

/**
 * 作者: 老廖
 * 时间: 2022/8/11
 *
 **/
object hookItemsAdder {

    @SubscribeEvent
    fun onHook(e: ItemsAdderFirstLoadEvent) {
        say("&7软依赖 &fItemsAdder &7已兼容.")
        Menu.loadMenu()
    }
}