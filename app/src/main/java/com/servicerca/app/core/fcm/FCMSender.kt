package com.servicerca.app.core.fcm

import android.content.Context
import android.util.Base64
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.security.KeyFactory
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Envía notificaciones push usando la FCM HTTP v1 API directamente desde el APK.
 * Lee las credenciales del archivo assets/service_account.json (descargado desde Firebase Console).
 *
 * SOLO PARA USO EN DESARROLLO / APP DE ESTUDIO.
 * En producción, las credenciales deben estar en un servidor seguro.
 */
@Singleton
class FCMSender @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val serviceAccount: JSONObject by lazy {
        val json = context.assets.open("service_account.json").bufferedReader().readText()
        JSONObject(json)
    }

    private val projectId: String get() = serviceAccount.getString("project_id")
    private val clientEmail: String get() = serviceAccount.getString("client_email")
    private val privateKeyPem: String get() = serviceAccount.getString("private_key")

    suspend fun sendChatNotification(
        recipientToken: String,
        senderName: String,
        messageText: String,
        senderId: String
    ) = withContext(Dispatchers.IO) {
        try {
            val accessToken = getAccessToken()
            val body = buildPayload(
                token = recipientToken,
                title = senderName,
                body = if (messageText.length > 100) messageText.take(97) + "..." else messageText,
                data = mapOf("type" to "chat", "senderId" to senderId),
                channelId = "channel_chat"
            )
            postToFCM(accessToken, body)
        } catch (e: Exception) {
            Log.e("FCMSender", "Error enviando notificación de chat", e)
        }
    }

    suspend fun sendGeneralNotification(
        recipientToken: String,
        title: String,
        body: String,
        type: String = "general",
        alreadySavedInFirestore: Boolean = true
    ) = withContext(Dispatchers.IO) {
        try {
            val accessToken = getAccessToken()
            val data = buildMap {
                put("type", type)
                if (alreadySavedInFirestore) put("noSave", "true")
            }
            val payload = buildPayload(
                token = recipientToken,
                title = title,
                body = body,
                data = data,
                channelId = "channel_general"
            )
            postToFCM(accessToken, payload)
        } catch (e: Exception) {
            Log.e("FCMSender", "Error enviando notificación general", e)
        }
    }

    private fun getAccessToken(): String {
        val now = System.currentTimeMillis() / 1000L
        val headerB64 = base64Url("""{"alg":"RS256","typ":"JWT"}""")
        val claimB64 = base64Url(
            """{"iss":"$clientEmail","scope":"https://www.googleapis.com/auth/firebase.messaging","aud":"https://oauth2.googleapis.com/token","exp":${now + 3600},"iat":$now}"""
        )
        val signInput = "$headerB64.$claimB64"
        val jwt = "$signInput.${signRS256(signInput)}"

        val conn = (URL("https://oauth2.googleapis.com/token").openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            doOutput = true
            setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
        }
        conn.outputStream.use {
            it.write("grant_type=urn:ietf:params:oauth:grant-type:jwt-bearer&assertion=$jwt".toByteArray())
        }

        val code = conn.responseCode
        if (code != 200) {
            val err = conn.errorStream?.bufferedReader()?.readText() ?: "sin detalle"
            throw Exception("Token OAuth2 fallido $code: $err")
        }
        return JSONObject(conn.inputStream.bufferedReader().readText()).getString("access_token")
    }

    private fun signRS256(input: String): String {
        // Limpia el PEM: quita headers y saltos de línea (reales y como \n literal)
        val keyContent = privateKeyPem
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("\\n", "")
            .replace("\n", "")
            .trim()

        val keyBytes = Base64.decode(keyContent, Base64.DEFAULT)
        val privateKey = KeyFactory.getInstance("RSA").generatePrivate(PKCS8EncodedKeySpec(keyBytes))

        return Signature.getInstance("SHA256withRSA").run {
            initSign(privateKey)
            update(input.toByteArray(Charsets.UTF_8))
            Base64.encodeToString(sign(), Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
        }
    }

    private fun base64Url(json: String): String =
        Base64.encodeToString(json.toByteArray(Charsets.UTF_8), Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)

    private fun buildPayload(
        token: String,
        title: String,
        body: String,
        data: Map<String, String>,
        channelId: String
    ): String = JSONObject().put(
        "message", JSONObject()
            .put("token", token)
            // Sin campo "notification": FCM lo trata como data-only y SIEMPRE llama
            // onMessageReceived (foreground, background y app cerrada).
            // title y body van dentro de data para que el Service los lea.
            .put("data", JSONObject().apply {
                data.forEach { (k, v) -> put(k, v) }
                put("title", title)
                put("body", body)
                put("channelId", channelId)
            })
            .put("android", JSONObject()
                .put("priority", "HIGH")
            )
    ).toString()

    private fun postToFCM(accessToken: String, body: String) {
        val conn = (URL("https://fcm.googleapis.com/v1/projects/$projectId/messages:send")
            .openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            doOutput = true
            setRequestProperty("Authorization", "Bearer $accessToken")
            setRequestProperty("Content-Type", "application/json; UTF-8")
        }
        conn.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }

        if (conn.responseCode == 200) {
            Log.d("FCMSender", "Push enviado correctamente")
        } else {
            val err = conn.errorStream?.bufferedReader()?.readText() ?: ""
            Log.e("FCMSender", "FCM error ${conn.responseCode}: $err")
        }
    }
}
