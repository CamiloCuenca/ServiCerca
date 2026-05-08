package com.servicerca.app.core.cloudinary

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL

object CloudinaryUploader {

    suspend fun uploadImage(
        imageBytes: ByteArray,
        cloudName: String,
        uploadPreset: String
    ): Result<String> = withContext(Dispatchers.IO) {
        if (cloudName.isBlank() || uploadPreset.isBlank()) {
            return@withContext Result.failure(Exception("Cloudinary no configurado (cloud name o upload preset vacío)"))
        }
        try {
            val boundary = "FormBoundary${System.currentTimeMillis()}"
            val connection = (URL("https://api.cloudinary.com/v1_1/$cloudName/image/upload")
                .openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                doOutput = true
                connectTimeout = 30_000
                readTimeout = 60_000
                setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
            }

            DataOutputStream(connection.outputStream).use { out ->
                out.writeBytes("--$boundary\r\n")
                out.writeBytes("Content-Disposition: form-data; name=\"upload_preset\"\r\n\r\n")
                out.writeBytes("$uploadPreset\r\n")

                out.writeBytes("--$boundary\r\n")
                out.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"profile.jpg\"\r\n")
                out.writeBytes("Content-Type: image/jpeg\r\n\r\n")
                out.write(imageBytes)
                out.writeBytes("\r\n")
                out.writeBytes("--$boundary--\r\n")
                out.flush()
            }

            val code = connection.responseCode
            val body = if (code == HttpURLConnection.HTTP_OK) {
                connection.inputStream.bufferedReader().readText()
            } else {
                connection.errorStream?.bufferedReader()?.readText() ?: "Error desconocido"
            }
            connection.disconnect()

            if (code == HttpURLConnection.HTTP_OK) {
                val secureUrl = JSONObject(body).getString("secure_url")
                Log.d("CloudinaryUploader", "Imagen subida: $secureUrl")
                Result.success(secureUrl)
            } else {
                Log.e("CloudinaryUploader", "Error $code: $body")
                Result.failure(Exception("Error al subir imagen ($code)"))
            }
        } catch (e: Exception) {
            Log.e("CloudinaryUploader", "Excepción al subir imagen", e)
            Result.failure(e)
        }
    }
}
