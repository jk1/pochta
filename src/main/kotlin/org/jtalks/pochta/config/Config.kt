package org.jtalks.pochta.config

import java.util.Properties
import java.util.ArrayList
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Date

/**
 *
 */
public class Config(props: Properties) {

    val smtp: Smtp = Smtp(props)
    val http: Http = Http(props)
    val log: Log = Log(props)
    val mailboxes: Mailboxes = Mailboxes(props)
    val initTime: Date = Date()

    class Http(props: Properties) {
        public val port: Int = props.getInt("jtalks.pochta.http.port")
        public val threads: Int = props.getInt("jtalks.pochta.http.threads")
    }

    class Smtp(props: Properties) {

        public enum class AuthType {
            DISABLED
            SUPPORTED
            ENFORCED
        }

        public enum class TransportSecurity {
            PLAINTEXT
            STARTTLS_SUPPORTED
            STARTTLS_ENFORCED
            SSL
        }

        public val connectionTimeout: Int = 60000 // 1 minute
        public val port: Int = props.getInt("jtalks.pochta.smtp.port")
        public val threads: Int = props.getInt("jtalks.pochta.smtp.threads")
        public val authType: AuthType = AuthType.ENFORCED
        public val transportSecurity: TransportSecurity = TransportSecurity.PLAINTEXT
    }

    class Log(props: Properties){
        public val enabled: Boolean = props.getBoolean("jtalks.pochta.log.enabled");
        public val verbose: Boolean = props.getBoolean("jtalks.pochta.log.verbose");
        public val consoleEnabled: Boolean = props.getBoolean("jtalks.pochta.log.sysout");
        public val logSmtpSessions: Boolean = props.getBoolean("jtalks.pochta.log.smtpSessions");
    }

    class MailboxConfig(val id: Int, val name : String, val login: String, val password: String, val size: Int,
                        val storage: Mailboxes.Storage) {
        val loginEscaped = URLEncoder.encode(login, StandardCharsets.UTF_8.toString())
    }

    class Mailboxes(props: Properties) : Iterable<MailboxConfig> {

        public enum class Storage {
            MEMORY
            FS
        }

        private val mailboxes = ArrayList<MailboxConfig>();

        {
            var i = 1;
            props.getString("jtalks.pochta.mailboxes").split(" ").forEach {(mbox) ->
                val login = props.getString("jtalks.pochta.mailbox.$mbox.login")
                val password = props.getString("jtalks.pochta.mailbox.$mbox.password")
                val size = props.getString("jtalks.pochta.mailbox.$mbox.size")
                val storage = props.getString("jtalks.pochta.mailbox.$mbox.storage")
                mailboxes.add(MailboxConfig(i++, mbox, login, password, Integer.parseInt(size),
                        Storage.valueOf(storage.toUpperCase())))
            }
        }


        override fun iterator(): Iterator<MailboxConfig> = mailboxes.iterator()
    }
}