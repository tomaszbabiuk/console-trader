package org.consoletrader.notifications.gmail

import org.consoletrader.common.Task
import java.io.File
import java.util.*
import javax.activation.DataHandler
import javax.activation.FileDataSource
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart


class GmailAlertTask : Task {
    override fun match(paramsRaw: String): Boolean {
        return paramsRaw.startsWith("gmailalert")
    }

    override fun execute(paramsRaw: String) {
        val params = GmailExtendedParams(paramsRaw)

        val props = setupGmailProperties()

        val session = Session.getInstance(props,
                object : javax.mail.Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(params.gmailuser, params.gmailpassword)
                    }
                })
        try {
            val message = MimeMessage(session)
            message.setFrom(InternetAddress(params.gmailuser))
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(params.to))
            message.subject = params.messageOrFile

            val f = File(params.messageOrFile)
            if (f.exists()) {
                println("Sending file [${params.messageOrFile}], from ${params.to}")
                sendFile(f, params, message)
            } else {
                println("Sending message [${params.messageOrFile}], from ${params.to}")
                sendText(message)
            }

            Transport.send(message)
            println("Message sent")

        } catch (e: MessagingException) {
            throw RuntimeException(e)
        }
    }

    private fun setupGmailProperties(): Properties {
        val props = Properties()
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.starttls.enable"] = "true"
        props["mail.smtp.host"] = "smtp.gmail.com"
        props["mail.smtp.port"] = "587"
        return props
    }

    private fun sendText(message: MimeMessage) {
        message.setText("This message has been sent from console-trader. See https://github.com/tomaszbabiuk/console-trader")
    }

    private fun sendFile(f: File, params: GmailExtendedParams, message: MimeMessage) {
        var messageBodyPart = MimeBodyPart()
        messageBodyPart.setText("This message has been sent from console-trader. See https://github.com/tomaszbabiuk/console-trader")

        val multipart = MimeMultipart()
        multipart.addBodyPart(messageBodyPart)
        messageBodyPart = MimeBodyPart()

        val source = FileDataSource(f.absolutePath)
        messageBodyPart.dataHandler = DataHandler(source)
        messageBodyPart.fileName = params.messageOrFile
        multipart.addBodyPart(messageBodyPart)
        message.setContent(multipart)
    }
}