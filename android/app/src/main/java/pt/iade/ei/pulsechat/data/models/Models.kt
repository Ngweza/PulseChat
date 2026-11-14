package pt.iade.ei.pulsechat.data.models

data class Conversation(
    val id: String,
    val targetId: String,
    val title: String,
    val lastMessage: String,
    val lastMessageTime: String,
    val unreadCount: Int = 0,
    val isGroup: Boolean = false,
    val avatarUrl: String? = null
)
