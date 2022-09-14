package me.geek.mail.common.kether



import me.geek.mail.common.kether.sub.KetherSub
import me.geek.mail.utils.Experience
import taboolib.library.kether.ParsedAction
import taboolib.library.kether.QuestContext
import taboolib.module.kether.KetherParser
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture
import kotlin.math.abs

/**
 * 作者: 老廖
 * 时间: 2022/8/8
 *
 **/
class GExp(private val action: String, private val context: ParsedAction<*>): KetherSub<Boolean>() {

    override fun run(frame: QuestContext.Frame): CompletableFuture<Boolean> {

        return frame.newFrame(context).run<Any>().thenApply {
          //  console().sendMessage("开始判断  $it  -- $action")
            val player = getPlayer(frame)
            var a = false
            val exp = abs(it.toString().toInt())
            when (action) {
                "give" -> playerUtils.giveTotalExperience(player, exp)
                "take" -> playerUtils.takeTotalExperience(player, exp)
                "has" -> a = playerUtils.hasTotalExperience(player, exp)
                "hasTake" -> a = playerUtils.hasTotalExperience(player, exp, true)
            }
            a
        }
    }
    companion object {
        private val playerUtils = Experience()
        /**
         * Exp 100 take
         */
        @KetherParser(value = ["Exp"], namespace = "GeekMail", shared = true)
        fun parser() = scriptParser {
            GExp(it.nextToken(), it.nextAction<Any>())
        }
    }
}