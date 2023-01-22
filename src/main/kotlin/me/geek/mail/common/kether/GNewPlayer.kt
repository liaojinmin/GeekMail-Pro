package me.geek.mail.common.kether

import me.geek.mail.GeekMail
import me.geek.mail.api.data.SqlManage.getData
import me.geek.mail.api.mail.MailManage
import me.geek.mail.common.kether.sub.KetherSub
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.library.kether.QuestContext
import taboolib.module.kether.KetherParser
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture

/**
 * 作者: 老廖
 * 时间: 2022/10/11
 *
 **/
class GNewPlayer(private val context: ParsedAction<*>): KetherSub<Boolean>() {

    override fun run(frame: QuestContext.Frame): CompletableFuture<Boolean> {
        return frame.newFrame(context).run<Any>().thenApply {
            if (it != "one") {
                GeekMail.say("NewPlayer 条件错误，缺少执行体 &f noe")
                false
            } else {
                GeekMail.debug("NewPlayer 条件正确")
                getPlayer(frame).getData().newPlayer
            }
        }
    }


    companion object {
        /**
         * NewPlayer one
         */
        @KetherParser(value = ["NewPlayer"], namespace = "GeekMail", shared = true)
        fun parser() = scriptParser {
            GNewPlayer(it.next(ArgTypes.ACTION))
        }
    }

}