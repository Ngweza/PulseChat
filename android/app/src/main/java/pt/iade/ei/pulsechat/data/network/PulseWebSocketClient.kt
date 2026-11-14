
package pt.iade.ei.pulsechat.data.network

import io.socket.client.IO
import io.socket.client.Socket
import io.socket.engineio.client.transports.WebSocket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject

enum class ConnectionStatus {
    CONNECTED,
    RECONNECTING,
    OFFLINE
}

class PulseWebSocketClient(
    private val serverUrl: String = "http://10.0.2.2:8080"
) {
    private var socket: Socket? = null

    private val _connectionStatus =
        MutableStateFlow(ConnectionStatus.OFFLINE)

    val connectionStatus: StateFlow<ConnectionStatus> =
        _connectionStatus.asStateFlow()

    private val _incomingMessages =
        MutableStateFlow<String?>(null)

    val incomingMessages: StateFlow<String?> =
        _incomingMessages.asStateFlow()

    private val _serverInstance = MutableStateFlow<String?>(null)

    val serverInstance: StateFlow<String?> =
        _serverInstance.asStateFlow()

    fun connect() {
        if (socket?.connected() == true) return

        val token = SessionManager.token

        if (token.isNullOrBlank()) {
            _connectionStatus.value = ConnectionStatus.OFFLINE
            return
        }

        _connectionStatus.value = ConnectionStatus.RECONNECTING

        try {
            val options = IO.Options().apply {
                transports = arrayOf(WebSocket.NAME)
                reconnection = true
                reconnectionAttempts = Int.MAX_VALUE
                reconnectionDelay = 1000
                reconnectionDelayMax = 30000
                auth = mapOf("token" to token)
            }

            socket = IO.socket(serverUrl, options)

            socket?.on(Socket.EVENT_CONNECT) {
                _connectionStatus.value = ConnectionStatus.CONNECTED
            }

            socket?.on(Socket.EVENT_DISCONNECT) {
                _connectionStatus.value = ConnectionStatus.RECONNECTING
            }

            socket?.on("connect_error") { args ->
                _connectionStatus.value = ConnectionStatus.RECONNECTING
            }

            socket?.on("new_message") { args ->
                val message = args.firstOrNull()?.toString()
                if (message != null) {
                    _incomingMessages.value = message
                }
            }
            socket?.on("server_info") { args ->
                val info = args.firstOrNull() as? JSONObject
                _serverInstance.value = info?.optString("instance")
            }

            socket?.connect()

        } catch (e: Exception) {
            _connectionStatus.value = ConnectionStatus.OFFLINE
        }
    }

    fun joinRoom(roomId: String) {
        if (socket?.connected() != true) return

        val payload = JSONObject().apply {
            put("roomId", roomId)
        }

        socket?.emit("join_room", payload)
    }

    fun sendMessage(roomId: String, content: String): Boolean {
        if (socket?.connected() != true) return false

        val payload = JSONObject().apply {
            put("roomId", roomId)
            put("content", content)
        }

        socket?.emit("send_message", payload)
        return true
    }

    fun disconnect() {
        socket?.disconnect()
        socket?.close()
        socket = null

        _connectionStatus.value = ConnectionStatus.OFFLINE
    }
}
object PulseSocketManager {
    val client = PulseWebSocketClient()
}
