package org.consoletrader.notifications.gmail

import org.consoletrader.common.Task
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


class GmailAlertTask : Task {
    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("gmailalert")
    }

    override fun execute(paramsRaw: String) {
        val params = GmailExtendedParams(paramsRaw)

        val props = Properties()
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.starttls.enable"] = "true"
        props["mail.smtp.host"] = "smtp.gmail.com"
        props["mail.smtp.port"] = "587"

        val session = Session.getInstance(props,
                object : javax.mail.Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(params.gmailuser, params.gmailpassword)
                    }
                })
        try {
            println("Sending message [${params.message}], from ${params.to}")

            val message = MimeMessage(session)
            message.setFrom(InternetAddress(params.gmailuser))
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(params.to))
            message.subject = params.message
            message.setText("This message has been sent from console-trader. See https://github.com/tomaszbabiuk/console-trader")

            Transport.send(message)

            println("Message sent")

        } catch (e: MessagingException) {
            throw RuntimeException(e)
        }
    }
}