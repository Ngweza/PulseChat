package pt.iade.ei.pulsechat.data.models
import pt.iade.ei.pulsechat.data.models.GroupDetails

object SampleData {

    val currentUser = Contact(
        id = "a40d1170-f97a-48b5-a403-68312239c3af",
        name = "Ngweza",
        username = "Ngweza"
    )
    val sampleContacts: List<Contact> = emptyList()

    val sampleMessages = listOf(
        ChatMessage(
            id = "msg_1",
            roomId = "room_1",
            senderId = "user_2",
            senderName = "Ana",
            content = "Olá! Como estás?",
            timestamp = System.currentTimeMillis() - 120000,
            moderationStatus = ModerationStatus.ACCEPTED,
            sequenceNumber = 1L,
            deliveryStatus = DeliveryStatus.ENTREGUE
        ),
        ChatMessage(
            id = "msg_2",
            roomId = "room_1",
            senderId = "user_1",
            senderName = "Utilizador",
            content = "Estou bem, obrigado!",
            timestamp = System.currentTimeMillis() - 60000,
            moderationStatus = ModerationStatus.ACCEPTED,
            sequenceNumber = 2L,
            deliveryStatus = DeliveryStatus.ENVIADA
        )
    )
    val sampleGroupDetails = GroupDetails(
        id = "group_1",
        name = "Grupo de demonstração",
        description = "Grupo para testar o PulseChat",
        members = listOf(
            GroupMember(
                user = currentUser,
                isAdmin = true
            ),
            GroupMember(
                user = Contact(
                    id = "user_2",
                    name = "Ana Silva",
                    username = "ana",
                    fullName = "Ana Silva",
                    email = "ana@example.com"
                ),
                isAdmin = false
            )
        )
    )

}
