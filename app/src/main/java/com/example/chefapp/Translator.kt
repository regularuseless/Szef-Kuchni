package com.example.chefapp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import org.json.JSONObject
import java.io.OutputStreamWriter

suspend fun translate(text: String, source: String, target: String): String {
    return withContext(Dispatchers.IO) {
        val url = URL("http://192.168.0.137:5000/translate")
        val jsonRequest = JSONObject().apply {
            put("q", text)
            put("source", source)
            put("target", target)
            put("format", "text") // Niektóre API tego wymagają
        }

        with(url.openConnection() as HttpURLConnection) {
            requestMethod = "POST"
            doOutput = true
            setRequestProperty("Content-Type", "application/json") // Ustaw JSON zamiast form-urlencoded
            setRequestProperty("Accept", "application/json") // Wymuś odpowiedź w JSON

            OutputStreamWriter(outputStream, "UTF-8").use { it.write(jsonRequest.toString()) }

            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw Exception("HTTP ${responseCode}: ${errorStream.bufferedReader().readText()}")
            }

            // Odczytanie odpowiedzi JSON
            val response = inputStream.bufferedReader().use { it.readText() }
            val jsonResponse = JSONObject(response)

            // Zwrócenie tylko przetłumaczonego tekstu
            return@withContext jsonResponse.getString("translatedText")
        }
    }
}
