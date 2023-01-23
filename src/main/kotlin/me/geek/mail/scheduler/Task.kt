package me.geek.mail.scheduler

import me.geek.mail.GeekMail
import me.geek.mail.api.data.SqlManage
import me.geek.mail.common.settings.SetTings
import me.geek.mail.scheduler.sql.actions
import me.geek.mail.scheduler.sql.use
import taboolib.common.platform.function.submitAsync
import kotlin.system.measureTimeMillis

/**
 * 作者: 老廖
 * 时间: 2022/10/12
 *
 **/
class Task {
    init {
        runExpIryTask()
    }

    @Synchronized
    fun runExpIryTask() {
        submitAsync(delay = SetTings.ExpiryAuto.toLong() * 20, period = SetTings.ExpiryAuto.toLong() * 20) {
            var res = 0
            measureTimeMillis {
                SqlManage.getConnection().use {
                    this.prepareStatement("DELETE FROM `maildata` WHERE sender=? AND `sendertime`<=?").actions { s ->
                        s.setString(1, SetTings.Console.toString())
                        s.setString(2, (System.currentTimeMillis() - SetTings.ExpiryTime).toString())
                        res = s.executeUpdate()
                    }
                }
            }.also {
                GeekMail.debug("&7数据维护事务 &8| &f成功删除 $res 条到期邮件数据... §8(耗时 $it Ms)")
            }
        }
    }
}