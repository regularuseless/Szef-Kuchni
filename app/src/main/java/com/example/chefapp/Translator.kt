package com.example.chefapp
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

fun translateText(inputText: String, direction: Int): String? {
    val client = OkHttpClient()

    // Ustawianie języków źródłowego i docelowego
    val (sourceLang, targetLang) = when (direction) {
        1 -> "en" to "pl" // Z angielskiego na polski
        2 -> "pl" to "en" // Z polskiego na angielski
        else -> throw IllegalArgumentException("Direction must be 1 or 2")
    }

    // Tworzenie treści zapytania POST
    val requestBody = FormBody.Builder()
        .add("q", inputText)
        .add("source", sourceLang)
        .add("target", targetLang)
        .build()

    // Tworzenie żądania HTTP
    val request = Request.Builder()
        .url("http://localhost:5000/translate") // Adres API LibreTranslate
        .post(requestBody)
        .build()

    // Wykonywanie zapytania i obsługa odpowiedzi
    return try {
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("Unexpected code $response")
            }
            // Odczytywanie odpowiedzi JSON
            val responseBody = response.body?.string()
            // Pobieranie przetłumaczonego tekstu (parsowanie JSON-a)
            val json = JSONObject(responseBody)
            json.getString("translatedText")
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}