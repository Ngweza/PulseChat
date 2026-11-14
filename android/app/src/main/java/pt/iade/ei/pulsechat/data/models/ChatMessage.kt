package pt.iade.ei.pulsechat.data.models


data class ChatMessage(
    val id: String,
    val roomId: String,
    val senderId: String,
    val senderName: String,
    val content: String,
    val timestamp: Long,
    val moderationStatus: ModerationStatus,
    val sequenceNumber: Long,
    val deliveryStatus: DeliveryStatus,
    val integrityHash: String? = null
)

enum class DeliveryStatus {
    PENDENTE,
    ENVIADA,
    PROCESSADA,
    ENTREGUE,
    LIDA,
    FALHOU
}

enum class ModerationStatus {
    ACCEPTED,
    PENDING,
    REJECTED,
    BLOCKED,
    FLAGGED
}
