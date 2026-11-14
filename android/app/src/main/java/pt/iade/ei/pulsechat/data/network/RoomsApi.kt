package pt.iade.ei.pulsechat.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

data class RoomInfo(
    val id: String,
    val name: String,
    val role: String
)

object RoomsApi {
    private const val BASE_URL = "http://10.0.2.2:8080"
    private val client = OkHttpClient()

    suspend fun getRooms(): Result<List<RoomInfo>> =
        withContext(Dispatchers.IO) {
            try {
                val token = SessionManager.token
                    ?: return@withContext Result.failure(
                        Exception("Sessão não autenticada.")
                    )

                val request = Request.Builder()
                    .url("$BASE_URL/api/rooms")
                    .header("Authorization", "Bearer $token")
                    .get()
                    .build()

                client.newCall(request).execute().use { response ->
                    val body = response.body?.string().orEmpty()

                    if (!response.isSuccessful) {
                        val error = try {
                            JSONObject(body).optString(
                                "error",
                                "Erro ao carregar salas: ${response.code}"
                            )
                        } catch (_: Exception) {
                            "Erro ao carregar salas: ${response.code}"
                        }

                        return@withContext Result.failure(Exception(error))
                    }

                    val array = JSONObject(body).getJSONArray("rooms")
                    val rooms = List(array.length()) { index ->
                        val room = array.getJSONObject(index)
                        RoomInfo(
                            id = room.getString("id"),
                            name = room.getString("name"),
                            role = room.optString("role", "")
                        )
                    }

                    Result.success(rooms)
                }
            } catch (e: Exception) {
                Result.failure(
                    Exception(
                        e.localizedMessage ?: "Não foi possível carregar as salas."
                    )
                )
            }

        }
    suspend fun createRoom(name: String): Result<RoomInfo> =
        withContext(Dispatchers.IO) {
            try {
                val token = SessionManager.token
                    ?: return@withContext Result.failure(
                        Exception("Sessão não autenticada.")
                    )

                val json = JSONObject().apply {
                    put("name", name)
                }

                val body = json.toString().toRequestBody(
                    "application/json; charset=utf-8".toMediaType()
                )

                val request = Request.Builder()
                    .url("$BASE_URL/api/rooms")
                    .header("Authorization", "Bearer $token")
                    .post(body)
                    .build()

                client.newCall(request).execute().use { response ->
                    val responseBody = response.body?.string().orEmpty()

                    if (!response.isSuccessful) {
                        val error = try {
                            JSONObject(responseBody).optString(
                                "error",
                                "Erro ao criar sala: ${response.code}"
                            )
                        } catch (_: Exception) {
                            "Erro ao criar sala: ${response.code}"
                        }

                        return@withContext Result.failure(Exception(error))
                    }

                    val roomJson = JSONObject(responseBody).getJSONObject("room")

                    Result.success(
                        RoomInfo(
                            id = roomJson.getString("id"),
                            name = roomJson.getString("name"),
                            role = "admin"
                        )
                    )
                }
            } catch (e: Exception) {
                Result.failure(
                    Exception(e.localizedMessage ?: "Não foi possível criar a sala.")
                )
            }
        }
}
