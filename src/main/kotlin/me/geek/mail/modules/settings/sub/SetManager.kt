package me.geek.mail.modules.settings.sub

import me.geek.mail.modules.settings.sub.smtp.SmtpData
import me.geek.mail.modules.settings.sub.storage.StorageDate

/**
 * 作者: 老廖
 * 时间: 2022/10/12
 *
 **/
data class SetManager(
    val storageDate: StorageDate,
    val SmtpData: SmtpData
)