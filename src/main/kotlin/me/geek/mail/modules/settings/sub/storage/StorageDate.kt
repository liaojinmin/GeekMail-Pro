package me.geek.mail.modules.settings.sub.storage


/**
 * 作者: 老廖
 * 时间: 2022/10/12
 *
 **/
class StorageDate(
    val use_type: String = "sqlite",
    val mysql: Mysql = Mysql(),
    val hikari_settings: Hikari = Hikari(),
    )