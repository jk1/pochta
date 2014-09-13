package org.jtalks.pochta.store

import org.jtalks.pochta.config.Config
import java.util.concurrent.ArrayBlockingQueue

class InMemoryMailbox(val config: Config.MailboxConfig) : Mailbox {

    override var size: Int = 0

    private val mails = ArrayBlockingQueue<Email>(config.size)

    override fun add(message: Email) {
        synchronized(mails) {
            if (mails.remainingCapacity() == 0) mails.take()
            mails.add(message)
            size = mails.size
        }
    }

    override fun byId(id: String) = mails.filter {(mail) -> mail.id == id }.first

    override fun iterator() = mails.iterator()
}
