package com.servicerca.app.core.email

import android.util.Log
import com.servicerca.app.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

object JavaMailSender {
    private const val TAG = "JavaMailSender"

    suspend fun sendEmail(to: String, subject: String, textBody: String, htmlBody: String? = null): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val props = Properties().apply {
                    put("mail.transport.protocol", "smtp")
                    put("mail.smtp.host", BuildConfig.SMTP_HOST)
                    put("mail.smtp.port", BuildConfig.SMTP_PORT)
                    put("mail.smtp.auth", "true")
                    put("mail.smtp.starttls.enable", "true")
                    put("mail.smtp.ssl.trust", BuildConfig.SMTP_HOST)
                }

                val session = Session.getInstance(props, object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(BuildConfig.SMTP_USER, BuildConfig.SMTP_PASSWORD)
                    }
                })

                val message = MimeMessage(session).apply {
                    setFrom(InternetAddress(BuildConfig.SMTP_FROM))
                    setRecipients(Message.RecipientType.TO, InternetAddress.parse(to))
                    setSubject(subject)
                    if (!htmlBody.isNullOrBlank()) {
                        setContent(htmlBody, "text/html; charset=utf-8")
                    } else {
                        setText(textBody)
                    }
                }

                Transport.send(message)
                Log.d(TAG, "Correo enviado a $to")
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e(TAG, "Error enviando correo a $to", e)
                Result.failure(e)
            }
        }
    }
}

