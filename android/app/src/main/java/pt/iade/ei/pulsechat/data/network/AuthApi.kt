package pt.iade.ei.pulsechat.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.OkHttpClient
import org.json.JSONObject

data class LoginResult(
    val token: String? = null,
    val error: String? = null
)

object AuthApi {

    private const val BASE_URL = "http://10.0.2.2:8080"

    private val client = OkHttpClient()

    suspend fun login(
        username: String,
        password: String
    ): LoginResult = withContext(Dispatchers.IO) {

        try {
            val json = JSONObject().apply {
                put("username", username)
                put("password", password)
            }

            val body = json.toString().toRequestBody(
                "application/json; charset=utf-8".toMediaType()
            )

            val request = Request.Builder()
                .url("$BASE_URL/api/auth/login")
                .post(body)
                .build()

            client.newCall(request).execute().use { response ->

                val responseBody = response.body?.string().orEmpty()
                val responseJson = JSONObject(responseBody)

                if (response.isSuccessful) {
                    val token = responseJson.optString("token")

                    if (token.isNotBlank()) {
                        LoginResult(token = token)
                    } else {
                        LoginResult(error = "O servidor não devolveu um token.")
                    }
                } else {
                    LoginResult(
                        error = responseJson.optString(
                            "message",
                            "Erro de autenticação: ${response.code}"
                        )
                    )
                }
            }

        } catch (e: Exception) {
            LoginResult(
                error = e.localizedMessage
                    ?: "Não foi possível contactar o servidor."
            )
        }
    }
}
