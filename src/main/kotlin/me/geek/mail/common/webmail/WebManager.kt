package me.geek.mail.common.webmail

import me.geek.mail.GeekMail
import me.geek.mail.api.mail.event.MailSenderEvent
import me.geek.mail.api.mail.event.WebMailSenderEvent
import me.geek.mail.common.data.sub.MailPlayerData
import me.geek.mail.common.webmail.sub.SubWebMail
import org.bukkit.Bukkit
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.console
import taboolib.common.platform.function.submitAsync
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress

/**
 * 作者: 老廖
 * 时间: 2022/9/6
 *
 **/
class WebManager : SubWebMail() {

    private val html = """<div style="background: #F0F2F5; padding: 35px; font-size: 14px;">
			<div style="width: 750px; margin: 0 auto; background: #1C1C1C no-repeat center; background-size: cover;">
				<div style="padding: 0 15px; padding-bottom: 20px;">
					<div style="height: 80px; display: flex; justify-content: space-between; position: relative;">
						<div style="margin-bottom: 20px;">
							<img style="margin-top: 20px; width: 250px; height: 60px;" src="https://web-1301331373.cos.ap-guangzhou.myqcloud.com/%E9%82%AE%E4%BB%B6logo.png" height="50" border="0"></a>
						</div>
						<div>
							<div style="font-size: 20px; color: #F0FFFF; margin-top: 32px;">玩家名称：{name} </div>
						</div>
					</div>
					<div style="background: #ffffff; padding: 0 15px; padding-bottom: 50px; margin-top: 18px;">
						<br/>
						<div>
							<h3>内容信息:</h3>
							<table style="width: 100%; border-collapse: collapse; border:none;">
								<tbody>
									<tr style="height: 40px;">
										<td style="border: 1px solid #DBDBDB; font-size: 14px; font-weight: normal; text-align: left; padding-left: 14px;">{title}</td>
									</tr>
								</tbody>
							</table>

							<h3>内容信息:</h3>
							<table style="width: 100%; border-collapse: collapse; border:none;">
								<tbody>
								<tr style="height: 40px;">
									<td style="border: 1px solid #DBDBDB; font-size: 14px; font-weight: normal; text-align: left; padding-left: 14px;">{text}</td>
								</tr>
								</tbody>
							</table>

							<h3>附件内容:</h3>
							<table style="width: 100%; border-collapse: collapse; border:none;">
								<tbody>
								<tr style="height: 40px;">
									<td style="border: 1px solid #DBDBDB; font-size: 14px; font-weight: normal; text-align: left; padding-left: 14px;">{app}</td>
								</tr>
								</tbody>
							</table>
						</div>

						<div style="margin-top: 30px"><span style="font-size: 20px; color: #e01010; font-weight: bold;">温馨提示:</span>
							<div style="line-height: 24px; margin-top: 10px;">
								<div>这只是一封提示邮件，如果你想领取你的附件，请加入游戏领取。</div>
							</div>
						</div>
					</div>
					<!-- 底部 -->
					<table class="responsive-table" style="text-size-adjust: 100%; margin-top: 16px; border-collapse: collapse !important; width: 100%;" border="0" cellspacing="0" cellpadding="0">
						<tbody>
						<tr>
							<td style="font-size: 12px; -webkit-font-smoothing: subpixel-antialiased; text-size-adjust: 100%;" align="center" valign="top">
								<p style="line-height: 20.4px; text-size-adjust: 100%; font-family: 'Microsoft YaHei'!important; padding: 0px !important; margin: 0px !important; color: #7e8890 !important;">
									<span class="appleLinks"> Copyright © 2022 GeekCraft-极客工作组. 保留所有权利。</span></p>
							</td>
						</tr>
						<tr style="padding: 0px; margin: 0px; font-size: 0px; line-height: 0;">
							<td style="font-size: 12px; -webkit-font-smoothing: subpixel-antialiased; text-size-adjust: 100%;">&nbsp;</td>
						</tr>
						<tr>
							<td style="font-size: 12px; -webkit-font-smoothing: subpixel-antialiased; text-size-adjust: 100%;" align="center" valign="top">
								<p style="line-height: 20.4px; text-size-adjust: 100%; font-family: 'Microsoft YaHei'!important; padding: 0px !important; margin: 0px !important; color: #7e8890 !important;">
									<span class="appleLinks">邮件由插件系统自动发送，请勿直接回复本邮件！</span></p>
							</td>
						</tr>
						</tbody>
					</table>
				</div>
			</div>
		</div>"""

    override fun onSender(title: String, text: String, app: String, targetID: UUID){
        submitAsync {
            var to = ""
            var name = ""
            val data = GeekMail.DataManage.getMailPlayerData(targetID)
            if (data != null) {
                to = data.mail
                name = data.name
            } else GeekMail.DataManage.selectPlayerBindMail(targetID)?.let {
                    name = it[0]
                    to = it[1]
                }
            if (to.isEmpty() || name.isEmpty()) {
                GeekMail.debug("目标玩家: $name 邮箱为空: $to")
                return@submitAsync
            }

            val event = WebMailSenderEvent(to, title, text, app, name)
            event.call()
            if (event.isCancelled) return@submitAsync

            var apps = Regex("""§([0-9]+)§?""").replace(app, "")
            apps = Regex("""§([a-zA-Z]+)§?""").replace(apps, "")
            val out = html.replace("{name}", name).replace("{title}", title).replace("{text}", text).replace("{app}", Regex("""&([a-zA-Z]+)&?""").replace(apps, ""))
            htmlMessage.setRecipient(Message.RecipientType.TO, InternetAddress(to))
            htmlMessage.setContent(out, "text/html;charset=gb2312")
            GeekMail.debug("发送Web邮件提醒,目标玩家: $name")
            Transport.send(htmlMessage)
        }
    }

}
