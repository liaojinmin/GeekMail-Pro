package me.geek.mail.modules.catcher

import me.geek.mail.GeekMail
import org.bukkit.entity.Player

/**
 * 作者: 老廖
 * 时间: 2022/12/21
 *
 **/
abstract class ChatCatCher {
    abstract val player: Player

    val cancel = Regex("""cancel|取消|Cancel""")
    var timeOut: Long = System.currentTimeMillis() + 20000

    abstract fun action(msg: String)
    abstract fun remove()


    open fun start() {
        GeekMail.debug("ChatListener.SessionCache[this.player] = this")
        ChatListener.SessionCache[this.player] = this
    }


}