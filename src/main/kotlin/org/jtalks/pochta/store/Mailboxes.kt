package org.jtalks.pochta.store

import org.jtalks.pochta.util.Context
import org.jtalks.pochta.config.ConfigProvider
import java.util.LinkedHashMap
import org.springframework.stereotype.Component
import org.springframework.beans.factory.annotation.Autowired
import org.jtalks.pochta.config.Config

/**
 * Mailbox is an incoming mail in-memory storage. Each mailbox has
 * a limit on how many mails it can hold. When overflowed mailbox
 * acts like FIFO-cache: oldest entries are removed first.
 */
Component class Mailboxes [Autowired] (val configProvider: ConfigProvider) : MailStore {

    private val mailboxes: Map<String, Mailbox>;

    {
        val mboxes = LinkedHashMap<String, Mailbox>()
        configProvider.config.mailboxes.forEach {(mbox) ->
            mboxes.put(mbox.password, getMailbox(mbox))
        }
        mailboxes = mboxes
    }

    private fun getMailbox(mbox: Config.MailboxConfig): Mailbox {
        when (mbox.storage) {
            Config.Mailboxes.Storage.MEMORY -> return InMemoryMailbox(mbox)
            Config.Mailboxes.Storage.FS -> return LocalStorageMailbox(mbox)
            else -> throw IllegalArgumentException("Unsupported storage type: ${mbox.storage}")
        }
    }

    override fun byContextPassword(): Mailbox? = mailboxes[Context[Context.PASSWORD]]

    override fun iterator(): Iterator<Mailbox> = mailboxes.values().iterator()
}
