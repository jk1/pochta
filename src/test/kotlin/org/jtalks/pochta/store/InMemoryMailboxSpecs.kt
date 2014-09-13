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
            it("should be empty") {
                assertEquals(0, mailbox.size)
            }
        }
        on("appending email") {
            mailbox.add(mock(javaClass<Email>()))
            it("should change size to one") {
                assertEquals(1, mailbox.size)
            }
        }
        on("appending another email") {
            val email = mock(javaClass<Email>())
            mailbox.add(email)
            it("should still have size one") {
                assertEquals(1, mailbox.size)
            }
            it("should keep only last one") {
                assertEquals(email, mailbox.iterator().next())
            }
        }
    }
}}
