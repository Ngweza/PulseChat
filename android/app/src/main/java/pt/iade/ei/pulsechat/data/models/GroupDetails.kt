package pt.iade.ei.pulsechat.data.models

data class GroupDetails(
    val id: String,
    val name: String,
    val description: String,
    val members: List<GroupMember>
)
