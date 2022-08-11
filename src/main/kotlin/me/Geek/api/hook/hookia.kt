package me.Geek.api.hook

import dev.lone.itemsadder.api.Events.ItemsAdderFirstLoadEvent
import me.Geek.GeekMail
import me.Geek.GeekMail.say
import me.Geek.Libs.Menu.Menu
import taboolib.common.platform.event.SubscribeEvent

/**
 * 作者: 老廖
 * 时间: 2022/8/11
 *
 **/
object hookia {

    @SubscribeEvent
    fun onHook(e: ItemsAdderFirstLoadEvent) {
        say("&7软依赖 &fItemsAdder &7已兼容.")
        GeekMail.menu = Menu()
    }
}