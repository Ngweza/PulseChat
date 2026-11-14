package pt.iade.ei.pulsechat.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import pt.iade.ei.pulsechat.data.models.ChatMessage
import pt.iade.ei.pulsechat.data.models.DeliveryStatus
import pt.iade.ei.pulsechat.data.models.ModerationStatus

object MessagesApi {
    private const val BASE_URL = "http://10.0.2.2:8080"
    private val client = OkHttpClient()

    suspend fun getMessages(roomId: String): Result<List<JSONObject>> =
        withContext(Dispatchers.IO) {
            try {
                val token = SessionManager.token
                    ?: return@withContext Result.failure(
                        Exception("Sessão não autenticada.")
                    )

                val request = Request.Builder()
                    .url("$BASE_URL/api/messages/$roomId?limit=50")
                    .header("Authorization", "Bearer $token")
                    .get()
                    .build()

                client.newCall(request).execute().use { response ->
                    val body = response.body?.string().orEmpty()

                    if (!response.isSuccessful) {
                        val error = try {
                            JSONObject(body).optString(
                                "error",
                                "Erro ao carregar mensagens: ${response.code}"
                            )
                        } catch (_: Exception) {
                            "Erro ao carregar mensagens: ${response.code}"
                        }

                        return@withContext Result.failure(Exception(error))
                    }

                    val json = JSONObject(body)
                    val array = json.getJSONArray("messages")
                    val messages = List(array.length()) { index ->
                        array.getJSONObject(index)
                    }

                    Result.success(messages)
                }
            } catch (e: Exception) {
                Result.failure(
                    Exception(
                        e.localizedMessage
                            ?: "Não foi possível carregar as mensagens."
                    )
                )
            }
        }
    fun parseMessage(json: JSONObject): ChatMessage {
        return ChatMessage(
            id = json.getString("id"),
            roomId = json.getString("room_id"),
            senderId = json.getString("sender_id"),
            senderName = json.getString("sender_id"),
            content = json.getString("content"),
            timestamp = java.time.Instant
                .parse(json.getString("created_at"))
                .toEpochMilli(),
            moderationStatus = ModerationStatus.ACCEPTED,
            sequenceNumber = 0L,
            deliveryStatus = DeliveryStatus.ENVIADA,
            integrityHash = null
        )
    }
}
