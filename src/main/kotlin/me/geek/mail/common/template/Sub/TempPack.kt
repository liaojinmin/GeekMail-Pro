package me.geek.mail.common.template.Sub



/**
 * 作者: 老廖
 * 时间: 2022/8/8
 */
class TempPack(
    override val packID: String,
    override val condition: String,
    override val action: String,
    override val deny: String,
    override val title: String,
    override val text: String,
    override val type: String,
    override val additional: String,
    override val itemStacks: String?,
    override val command: String?
) : Temp