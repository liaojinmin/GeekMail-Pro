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
class GMoney(private val action: String, private val context: ParsedAction<*>): KetherSub<Boolean>() {

    override fun run(frame: QuestContext.Frame): CompletableFuture<Boolean> {

        return frame.newFrame(context).run<Any>().thenApply {
          //  console().sendMessage("开始判断  $it  -- $action")
            val player = getPlayer(frame)
            var a = false
            val money = it.toString().toDouble()
            when (action) {
                "give" -> HookPlugin.money.giveMoney(player, money)
                "take" -> HookPlugin.money.takeMoney(player, money)
                "has" -> a = HookPlugin.money.hasMoney(player, money)
                "hasTake" -> a = HookPlugin.money.hasTakeMoney(player, money)
            }
            a
        }
    }
    companion object {
        /**
         * Money 100 take
         */
        @KetherParser(value = ["Money"], namespace = "GeekMail", shared = true)
        fun parser() = scriptParser {
            GMoney(it.nextToken(), it.nextAction<Any>())
        }
    }
}