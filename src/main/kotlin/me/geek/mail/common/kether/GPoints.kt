package me.geek.mail.common.kether


import me.geek.mail.common.kether.sub.KetherSub
import me.geek.mail.api.hook.HookPlugin
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
                "give" -> HookPlugin.points.givePoints(player, points)
                "take" -> HookPlugin.points.takePoints(player, points)
                "has" -> a = HookPlugin.points.hasPoints(player, points)
                "hasTake" -> a= HookPlugin.points.hasTakePoints(player, points)
            }
            a
        }
    }
    companion object {
        /**
         * Points 100 take
         */
        @KetherParser(value = ["Points"], namespace = "GeekMail", shared = true)
        fun parser() = scriptParser {
            GPoints(it.nextToken(), it.nextAction<Any>())
        }
    }
}