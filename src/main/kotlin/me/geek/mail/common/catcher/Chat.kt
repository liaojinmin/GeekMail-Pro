package me.geek.mail.common.catcher


import me.geek.mail.api.data.SqlManage
import me.geek.mail.api.mail.MailManage
import org.bukkit.entity.Player
import taboolib.platform.util.sendLang

/**
 * 作者: 老廖
 * 时间: 2022/9/11
 *
 **/
class Chat(
    override val player: Player
): ChatCatCher() {

    private val reg = Regex("""[a-zA-Z0-9]+([-_.][A-Za-zd]+)*@([a-zA-Z0-9]+[-.])+[A-Za-zd]{2,5}""")
    override fun remove() {

    }
    override fun action(msg: String) {
        if (reg.matches(msg)) {
           // player.getData().mail = msg
            val text = ((Math.random()*9+1)*100000).toInt()
            SqlManage.bindCode[player.uniqueId] = "$text;$msg"

            MailManage.senderBindMail(player.name, text.toString(), msg)
            player.sendLang("玩家-邮箱绑定-确认")
            } else player.sendLang("玩家-邮箱绑定-错误")
    }

    override fun start() {
        super.start()
        player.sendLang("玩家-输入捕获")
    }
}