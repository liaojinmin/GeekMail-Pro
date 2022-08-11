package me.Geek.Modules

import me.Geek.api.mail.MailSub
import me.Geek.api.mail.MailType
import java.util.*

/**
 * 作者: 老廖
 * 时间: 2022/8/8
 *
 **/
class MailPack: MailSub {
    private val MailID: UUID
    private val Sender: UUID
    private var Target: UUID
    private var Title: String
    private var Text: List<String>
 //   private val Type: MailType
    private var Appendix: String


    constructor(mailID: UUID, sender: UUID, target: UUID, title: String, Text: List<String>, type: String) {
        this.MailID = mailID
        this.Sender = sender
        this.Target = target
        this.Title = title
        this.Text = Text
        this.Appendix = type
        eval()
    }

    override fun getMailID(): UUID {
        return this.MailID
    }

    override fun getMailType(): MailType {
        TODO("Not yet implemented")
    }

    override fun getTitle(): String {
        TODO("Not yet implemented")
    }

    override fun getSender(): UUID {
        TODO("Not yet implemented")
    }

    override fun getTarget(): UUID {
        TODO("Not yet implemented")
    }

    override fun getText(): String {
        TODO("Not yet implemented")
    }

    override fun getState(): String {
        TODO("Not yet implemented")
    }

    override fun setState(state: String?) {
        TODO("Not yet implemented")
    }

    override fun getAppendix(): String {
        TODO("Not yet implemented")
    }

    override fun getType(): String {
        TODO("Not yet implemented")
    }
    override fun setTarget(target: UUID) {
        this.Target = target
    }

    fun eval() {

    }
}