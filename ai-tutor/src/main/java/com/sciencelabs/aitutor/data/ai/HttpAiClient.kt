package com.sciencelabs.aitutor.data.ai

import com.sciencelabs.aitutor.data.model.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.util.UUID

/**
 * A minimal OkHttp-based AI client that posts conversation JSON to a configurable endpoint.
 * The expected API contract is simple: POST {messages: [{role, text}, ...]} with Authorization header
 * and a JSON response { "id": "...", "role":"assistant", "text":"..." }.
 *
 * This is intentionally generic so you can point it at an OpenAI-compatible gateway or your own LLM API.
 */
class HttpAiClient(
    private val baseUrl: String,
    private val apiKey: String,
    private val json: Json = Json { encodeDefaults = true; ignoreUnknownKeys = true }
) : AiClient {

    private val client: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
        OkHttpClient.Builder().addInterceptor(logging).build()
    }

    @Serializable
    private data class SendRequest(val messages: List<SerializableMessage>)

    @Serializable
    private data class SerializableMessage(val role: String, val text: String)

    @Serializable
    private data class SendResponse(val id: String? = null, val role: String? = null, val text: String? = null)

    override suspend fun sendMessage(messages: List<Message>, onDelta: (String) -> Unit): Message = withContext(Dispatchers.IO) {
        val req = SendRequest(messages.map { SerializableMessage(it.role, it.text) })
        val bodyJson = json.encodeToString(req)
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = RequestBody.create(mediaType, bodyJson)

        val request = Request.Builder()
            .url(baseUrl)
            .post(body)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .build()

        val response: Response = client.newCall(request).execute()
        val responseBody = response.body?.string() ?: ""

        if (!response.isSuccessful) {
            // Return an error assistant message so the UI can display it.
            return@withContext Message(
                id = UUID.randomUUID().toString(),
                role = "assistant",
                text = "Error: ${response.code} - ${responseBody}"
            )
        }

        val parsed = try {
            json.decodeFromString(SendResponse.serializer(), responseBody)
        } catch (e: Exception) {
            SendResponse(text = responseBody)
        }

        // For this simple client we don't stream tokens. We call onDelta once with the full text.
        val assistantText = parsed.text ?: ""
        onDelta(assistantText)

        Message(
            id = parsed.id ?: UUID.randomUUID().toString(),
            role = parsed.role ?: "assistant",
            text = assistantText
        )
    }
}
