@file:Depends("coreMindustry")
@file:Import("com.sun.mail:javax.mail:1.6.2", mavenDepends = true)
@file:Import("javax.activation:activation:1.1.1", mavenDepends = true)

package mirai

import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress

fun sm(to: String, subject: String, body: String) {
    val properties = Properties()
    properties["mail.smtp.host"] = "smtp.qq.com"
    properties["mail.smtp.port"] = "587"
    properties["mail.smtp.auth"] = "true"
    properties["mail.smtp.starttls.enable"] = "true"

    val session = Session.getInstance(properties, object : Authenticator() {
        override fun getPasswordAuthentication(): PasswordAuthentication {
            return PasswordAuthentication("2504013368@qq.com", "uojkhavfffevdjjg")
        }
    })

    val message = javax.mail.internet.MimeMessage(session)
    message.setFrom(InternetAddress("2504013368@qq.com"))
    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to))
    message.subject = subject
    message.setText(body)

    Transport.send(message)
}