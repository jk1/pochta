package org.jtalks.pochta.store

import java.util.Date
import javax.mail.internet.MimeMessage
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import java.io.OutputStream
import java.io.PrintWriter
import java.io.OutputStreamWriter

/**
 *
 */
public trait Email {

    val id: String
    val receivedDate: Date?
    val envelopeFrom: String?
    val envelopeRecipients: MutableList<String>
    val ip: String
    val subject: String?
    val message: MimeMessage?

    fun getRawConfig(): String =
            """${::id.name}:${id}
${::receivedDate.name}:${receivedDate?.getTime()}
${::envelopeFrom.name}:${envelopeFrom}
${::envelopeRecipients.name}:${envelopeRecipients}
${::ip.name}:${ip}
${::subject.name}:${subject}
"""

    fun getRawMessage(): String {
        val stream = ByteArrayOutputStream()
        message?.writeTo(stream)
        return String(stream.toByteArray(), StandardCharsets.UTF_8)
    }
}
