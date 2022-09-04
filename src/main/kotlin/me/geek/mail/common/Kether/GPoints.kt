package me.geek.mail.common.Kether


import me.geek.mail.common.Kether.sub.KetherSub
import me.geek.mail.api.hook.hookPlugin
import taboolib.library.kether.ParsedAction
import taboolib.library.kether.QuestContext
import taboolib.module.kether.KetherParser
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture

/**
 * 作者: 老廖
 * 时间: 2022/8/8
 *
 **/
class GPoints(private val  action: String, private val context: ParsedAction<*>): KetherSub<Boolean>() {
    override fun run(frame: QuestContext.Frame): CompletableFuture<Boolean> {

        return frame.newFrame(context).run<Any>().thenApply {
            val player = getPlayer(frame).uniqueId
            var a = false
            val points = it.toString().toInt()
            when (action) {
                "give" -> hookPlugin.points.give(player, points)
                "take" -> hookPlugin.points.take(player, points)
                "has" -> a = hookPlugin.points.look(player) >= points
                "hasTake" -> {
                    if (hookPlugin.points.look(player) >= points) {
                        a = true
                        hookPlugin.points.take(player, points)
                    }
                }
            }
            a
        }
    }
    companion object {
        /**
         * Money 100 take
         */
        @KetherParser(value = ["Points"], namespace = "GeekMail", shared = true)
        fun parser() = scriptParser {
            GPoints(it.nextToken(), it.nextAction<Any>())
        }
    }
}