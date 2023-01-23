package me.geek.mail.api.mail

/**
 * 作者: 老廖
 * 时间: 2023/1/16
 *
 **/
enum class MailState(var state: String) {
    /**
     * 已领取
     */
    Acquired("已提取"),

    /**
     * 未领取
     */
    NotObtained("未提取"),

    /**
     * 纯文本邮件
     */
    Text("纯文本")

}