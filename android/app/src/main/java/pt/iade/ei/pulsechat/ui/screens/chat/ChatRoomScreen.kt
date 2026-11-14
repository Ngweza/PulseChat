package pt.iade.ei.pulsechat.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import pt.iade.ei.pulsechat.data.network.MessagesApi
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import pt.iade.ei.pulsechat.data.network.PulseSocketManager
import pt.iade.ei.pulsechat.data.network.ConnectionStatus
import org.json.JSONObject
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import pt.iade.ei.pulsechat.data.models.ChatMessage
import pt.iade.ei.pulsechat.data.models.ConnectionState
import pt.iade.ei.pulsechat.data.models.DeliveryStatus
import pt.iade.ei.pulsechat.data.models.ModerationStatus
import pt.iade.ei.pulsechat.data.models.SampleData
import pt.iade.ei.pulsechat.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatRoomScreen(
    roomId: String,
    roomName: String,
    onNavigateBack: () -> Unit,
    onOpenGroupDetails: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var messages by remember {
        mutableStateOf<List<ChatMessage>>(emptyList())
    }
    var inputText by remember { mutableStateOf("") }
    var connectionState by remember { mutableStateOf(ConnectionState.CONNECTED) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val socketClient = PulseSocketManager.client

    val socketConnectionStatus by
    socketClient.connectionStatus.collectAsState()

    val incomingMessage by
    socketClient.incomingMessages.collectAsState()

    LaunchedEffect(roomId, socketConnectionStatus) {
        if (socketConnectionStatus == ConnectionStatus.CONNECTED) {
            socketClient.joinRoom(roomId)
        }
    }
    LaunchedEffect(incomingMessage) {
        val jsonText = incomingMessage

        if (!jsonText.isNullOrBlank()) {
            try {
                val json = JSONObject(jsonText)
                val receivedMessage = MessagesApi.parseMessage(json)

                if (
                    receivedMessage.roomId == roomId &&
                    messages.none { it.id == receivedMessage.id }
                ) {
                    messages = messages + receivedMessage
                }
            } catch (e: Exception) {
                android.util.Log.e(
                    "PulseChat",
                    "Erro ao processar mensagem recebida",
                    e
                )
            }
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem((messages.size - 1).coerceAtLeast(0))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onOpenGroupDetails(roomId) }
                    ) {
                        // Avatar pequeno do contacto/grupo
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(RoyalBlueContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Group,
                                contentDescription = null,
                                tint = RoyalBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = roomName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = when (connectionState) {
                                    ConnectionState.CONNECTED -> "Ligado • Servidor 1"
                                    ConnectionState.RECONNECTING -> "A reconectar..."
                                    ConnectionState.OFFLINE -> "Offline"
                                    ConnectionState.DISCONNECTED -> "Desligado"
                                },
                                fontSize = 12.sp,
                                color = when (connectionState) {
                                    ConnectionState.CONNECTED -> OnlineGreen
                                    ConnectionState.RECONNECTING -> WarningAmber
                                    ConnectionState.OFFLINE -> ErrorRed
                                    ConnectionState.DISCONNECTED -> ErrorRed
                                }
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color(0xFF0F172A)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onOpenGroupDetails(roomId) }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Informação",
                            tint = RoyalBlue
                        )
                    }
                    IconButton(
                        onClick = {
                            connectionState = when (connectionState) {
                                ConnectionState.CONNECTED -> ConnectionState.OFFLINE
                                else -> ConnectionState.CONNECTED
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (connectionState == ConnectionState.CONNECTED) Icons.Default.Wifi else Icons.Default.WifiOff,
                            contentDescription = "Estado da Ligação",
                            tint = if (connectionState == ConnectionState.CONNECTED) OnlineGreen else ErrorRed
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PureWhite)
            )
        },
        bottomBar = {
            Surface(
                color = PureWhite,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                        .navigationBarsPadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Campo de texto arredondado (24.dp, fundo #F3F4F6)
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = {
                            Text("Escreva uma mensagem...", color = Color(0xFF94A3B8), fontSize = 14.sp)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SoftGraySurface,
                            unfocusedContainerColor = SoftGraySurface,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        maxLines = 4
                    )

                    // Botão circular azul com avião de papel
                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                val currentText = inputText.trim()

                                val sent = socketClient.sendMessage(roomId, currentText)

                                if (sent) {
                                    inputText = ""
                                }
                            }
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = RoyalBlue,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Enviar"
                        )
                    }
                }
            }
        },
        containerColor = PureWhite,
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (messages.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sem mensagens nesta sala ainda.",
                        fontSize = 14.sp,
                        color = Color(0xFF64748B)
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(messages, key = { it.id }) { message ->
                        val isFromCurrentUser = message.senderId == SampleData.currentUser.id
                        ChatMessageBubble(
                            message = message,
                            isFromCurrentUser = isFromCurrentUser
                        )
                    }
                }
            }
        }
    }
}

private fun newMessageId(seq: Long): String {
    return String.format(Locale.US, "%04d", seq)
}

@Composable
fun ChatMessageBubble(
    message: ChatMessage,
    isFromCurrentUser: Boolean,
    modifier: Modifier = Modifier
) {
    // Balões de conversa bem definidos
    val bubbleShape = if (isFromCurrentUser) {
        RoundedCornerShape(16.dp).copy(bottomEnd = CornerSize(4.dp))
    } else {
        RoundedCornerShape(16.dp).copy(bottomStart = CornerSize(4.dp))
    }

    val bubbleColor = when {
        message.moderationStatus == ModerationStatus.BLOCKED -> Color(0xFFFEE2E2)
        isFromCurrentUser -> RoyalBlue
        else -> ReceivedBubbleBg
    }

    val contentColor = when {
        message.moderationStatus == ModerationStatus.BLOCKED -> ErrorRed
        isFromCurrentUser -> Color.White
        else -> Color(0xFF0F172A)
    }

    val formattedTime = remember(message.timestamp) {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        sdf.format(Date(message.timestamp))
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = if (isFromCurrentUser) Alignment.End else Alignment.Start
    ) {
        if (!isFromCurrentUser) {
            Text(
                text = message.senderName,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = RoyalBlue,
                modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
            )
        }

        Surface(
            shape = bubbleShape,
            color = bubbleColor,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                // Cartão discreto de aviso caso a mensagem seja sinalizada pela moderação IA
                if (message.moderationStatus == ModerationStatus.FLAGGED) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = WarningAmber,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Sinalizada pela IA de Moderação",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = WarningAmber
                            )
                        }
                    }
                } else if (message.moderationStatus == ModerationStatus.BLOCKED) {
                    Text(
                        text = "🚫 Mensagem Bloqueada pela Moderação.",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ErrorRed,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                // Texto da mensagem
                Text(
                    text = if (message.moderationStatus == ModerationStatus.BLOCKED) {
                        "[Conteúdo Ocultado]"
                    } else {
                        message.content
                    },
                    fontSize = 14.sp,
                    color = contentColor,
                    lineHeight = 19.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Rodapé (Hora e Vistos)
                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = formattedTime,
                        fontSize = 10.sp,
                        color = if (isFromCurrentUser) Color.White.copy(alpha = 0.8f) else Color(0xFF94A3B8)
                    )

                    if (isFromCurrentUser) {
                        DeliveryStatusIcon(
                            status = message.deliveryStatus,
                            contentColor = contentColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DeliveryStatusIcon(
    status: DeliveryStatus,
    contentColor: Color
) {
    when (status) {
        DeliveryStatus.PENDENTE -> {
            Icon(
                imageVector = Icons.Default.AccessTime,
                contentDescription = "Pendente",
                modifier = Modifier.size(12.dp),
                tint = Color.White.copy(alpha = 0.7f)
            )
        }
        DeliveryStatus.ENVIADA -> {
            Icon(
                imageVector = Icons.Default.Done,
                contentDescription = "Enviada",
                modifier = Modifier.size(12.dp),
                tint = Color.White.copy(alpha = 0.8f)
            )
        }
        DeliveryStatus.PROCESSADA, DeliveryStatus.ENTREGUE, DeliveryStatus.LIDA -> {
            Icon(
                imageVector = Icons.Default.DoneAll,
                contentDescription = "Lida",
                modifier = Modifier.size(13.dp),
                tint = Color.White
            )
        }
        DeliveryStatus.FALHOU -> {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = "Falhou",
                modifier = Modifier.size(12.dp),
                tint = Color.Red
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatRoomScreenPreview() {
    PulseChatTheme {
        ChatRoomScreen(
            roomId = "room_2",
            roomName = "Sistemas Distribuídos - G4",
            onNavigateBack = {}
        )
    }
}
