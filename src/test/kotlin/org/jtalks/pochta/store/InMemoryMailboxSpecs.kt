package org.jtalks.pochta.store

import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals
import org.jtalks.pochta.config.Config
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.jtalks.pochta.config.Config.Mailboxes.Storage

/**
 * @author Artem Khvastunov
 */
class InMemoryMailboxSpecs() : Spek() {{

    given("in memory mailbox with size one") {
        val mailbox = InMemoryMailbox(Config.MailboxConfig(0, "", "", "", 1, Storage.FS)) // TODO How to use mock here?
        on("starting position") {
            val size = mailbox.size
            it("should be empty") {
                assertEquals(0, size)
            }
        }
        on("appending email") {
            mailbox.add(mock(javaClass<Email>()))
            val size = mailbox.size
            it("should change size to one") {
                assertEquals(1, size)
            }
        }
        on("appending another email") {
            val emailToAdd = mock(javaClass<Email>())
            mailbox.add(emailToAdd)
            val size = mailbox.size
            val emailFromMailbox = mailbox.iterator().next()
            it("should still have size one") {
                assertEquals(1, size)
            }
            it("should keep only last one") {
                assertEquals(emailToAdd, emailFromMailbox)
            }
        }
    }
}}
