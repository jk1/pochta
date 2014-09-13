package org.jtalks.pochta.store

import org.jtalks.pochta.config.Config
import java.io.File
import java.io.FileOutputStream
import java.util.Date
import java.util.ArrayList
import javax.mail.internet.MimeMessage
import javax.mail.Session
import java.util.Properties
import java.io.FileInputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import org.apache.commons.io.IOUtils

/**
 * @author Artem Khvastunov
 */
class LocalStorageMailbox(val config: Config.MailboxConfig) : Mailbox {

    private val meta = File("${System.getProperty("user.home")}/.pochta/mailboxes/${config.name}/");
    private val storage = File("${System.getProperty("user.home")}/.pochta/mailboxes/${config.name}.storage/");

    {
        meta.mkdirs()
        storage.mkdirs()
        cleanStorage()
    }

    override var size: Int = meta.list()?.size ?: 0 // TODO How to avoid unnecessary initialization?
        get() = meta.list()?.size ?: 0

    override fun add(message: Email) {
        synchronized(storage) {
            cleanStorage()
            writeMeta(message)
            writeMessage(message)
        }
    }

    private fun cleanStorage() {
        while (size > 0 && size >= config.size) {
            val metadata = meta.getFirstModified()!!
            val id = metadata.name
            metadata.delete()
            storage.file("${id}").delete()
        }
    }

    private fun writeMeta(message: Email) {
        val meta = meta.file("${message.id}")
        val fos = FileOutputStream(meta)
        try {
            IOUtils.write(message.getRawConfig(), fos, StandardCharsets.UTF_8.toString())
        } finally {
            IOUtils.closeQuietly(fos)
        }
    }

    private fun writeMessage(message: Email) {
        val msg = storage.file("${message.id}")
        val fos = FileOutputStream(msg)
        try {
            message.message?.writeTo(fos)
        } finally {
            IOUtils.closeQuietly(fos)
        }
    }

    override fun byId(id: String): Email? = LocalStorageEmail(meta.file("${id}"), storage.file("${id}"))

    override fun iterator(): Iterator<Email> {
        val files = meta.listFilesByLasModified() ?: listOf()
        return files.map({ LocalStorageEmail(it, storage.file(it.name)) }).iterator()
    }
}

class LocalStorageEmail(meta: File, storage: File) : Email {

    override var id: String = ""
    override var receivedDate: Date? = null
    override var envelopeFrom: String? = null
    override var envelopeRecipients: MutableList<String> = ArrayList()
    override var ip: String = ""
    override var subject: String? = null
    override var message: MimeMessage? = null

    {
        val reader = BufferedReader(InputStreamReader(FileInputStream(meta), StandardCharsets.UTF_8))
        val storageStream = FileInputStream(storage)
        try {
            for (line in reader.lines()) {
                val params = line.split(":", 2)
                val property = params[0]
                val value = params[1]
                when (property) {
                    ::id.name -> id = value;
                    ::receivedDate.name -> receivedDate = Date(value.toLong());
                    ::envelopeFrom.name -> envelopeFrom = value;
                    ::envelopeRecipients.name -> envelopeRecipients.add(value);
                    ::ip.name -> ip = value;
                    ::subject.name -> subject = value;
                }
            }
            message = MimeMessage(Session.getInstance(Properties()), storageStream)
        } finally {
            IOUtils.closeQuietly(reader)
            IOUtils.closeQuietly(storageStream)
        }
    }
}
