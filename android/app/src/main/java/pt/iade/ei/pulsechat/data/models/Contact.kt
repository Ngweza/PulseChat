package pt.iade.ei.pulsechat.data.models

data class Contact(
    val id: String,
    val name: String,
    val username: String,
    val avatarUrl: String? = null,
    val isOnline: Boolean = false,
    val statusMessage: String = "",
    val fullName: String = name,
    val email: String = ""
)
