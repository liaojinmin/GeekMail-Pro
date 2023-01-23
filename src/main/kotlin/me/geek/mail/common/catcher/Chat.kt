package me.geek.mail.common.catcher


import me.geek.mail.api.data.SqlManage.getData
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
            player.getData().mail = msg
            player.sendMessage("§a绑定成功.")
            } else player.sendMessage("§c错误的邮箱格式.")
        }

    override fun start() {
        super.start()
        player.sendLang("玩家-输入捕获")
    }
}