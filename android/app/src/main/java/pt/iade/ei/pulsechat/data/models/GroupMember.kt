package pt.iade.ei.pulsechat.data.models

data class GroupMember(
    val user: Contact,
    val isAdmin: Boolean = false
)
