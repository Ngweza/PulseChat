package pt.iade.ei.pulsechat.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import pt.iade.ei.pulsechat.data.network.RoomsApi
import pt.iade.ei.pulsechat.data.network.ConnectionStatus
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import pt.iade.ei.pulsechat.data.network.PulseSocketManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.iade.ei.pulsechat.data.models.Conversation
import pt.iade.ei.pulsechat.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onConversationSelected: (Conversation) -> Unit,
    onNavigateToContacts: () -> Unit,
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val socketClient = PulseSocketManager.client

    val connectionStatus by
    socketClient.connectionStatus.collectAsState()

    val serverInstance by
    socketClient.serverInstance.collectAsState()

    LaunchedEffect(Unit) {
        socketClient.connect()
    }
    var conversations by remember {
        mutableStateOf<List<Conversation>>(emptyList())
    }
    LaunchedEffect(Unit) {
        val result = RoomsApi.getRooms()

        result.onSuccess { rooms ->
            conversations = rooms.map { room ->
                Conversation(
                    id = room.id,
                    targetId = room.id,
                    title = room.name,
                    lastMessage = "",
                    lastMessageTime = "",
                    unreadCount = 0,
                    isGroup = true
                )
            }
        }
    }
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) }
    var showCreateGroupDialog by remember { mutableStateOf(false) }
    var newGroupName by remember { mutableStateOf("") }
    var createGroupError by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val filteredConversations = remember(searchQuery, conversations) {
        if (searchQuery.isBlank()) {
            conversations
        } else {
            conversations.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                        it.lastMessage.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 12.dp)
                    ) {
                        Text(
                            text = "Mensagens",
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = Color(0xFF0F172A)
                        )

                        // Indicador de estado "Online"
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(OnlineGreen)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = when (connectionStatus) {
                                    ConnectionStatus.CONNECTED -> serverInstance ?: "Ligado"
                                    ConnectionStatus.RECONNECTING -> "A reconectar..."
                                    ConnectionStatus.OFFLINE -> "Offline"
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = OnlineGreen
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PureWhite)
            )
        },
        bottomBar = {
            Surface(
                color = PureWhite,
                tonalElevation = 0.dp,
                shadowElevation = 8.dp
            ) {
                Column {
                    HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)
                    NavigationBar(
                        containerColor = PureWhite,
                        tonalElevation = 0.dp
                    ) {
                        NavigationBarItem(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            icon = { Icon(Icons.Default.ChatBubble, contentDescription = "Conversas") },
                            label = { Text("Conversas", fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = RoyalBlue,
                                selectedTextColor = RoyalBlue,
                                indicatorColor = RoyalBlueContainer
                            )
                        )
                        NavigationBarItem(
                            selected = selectedTab == 1,
                            onClick = {
                                selectedTab = 1
                                onNavigateToContacts()
                            },
                            icon = { Icon(Icons.Default.Group, contentDescription = "Contactos") },
                            label = { Text("Contactos", fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = RoyalBlue,
                                selectedTextColor = RoyalBlue,
                                indicatorColor = RoyalBlueContainer
                            )
                        )
                        NavigationBarItem(
                            selected = selectedTab == 2,
                            onClick = {
                                selectedTab = 2
                                onNavigateToProfile()
                            },
                            icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                            label = { Text("Perfil", fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = RoyalBlue,
                                selectedTextColor = RoyalBlue,
                                indicatorColor = RoyalBlueContainer
                            )
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    newGroupName = ""
                    createGroupError = null
                    showCreateGroupDialog = true
                },
                containerColor = RoyalBlue,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Criar grupo",
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        containerColor = PureWhite,
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Barra de pesquisa arredondada (24.dp, fundo #F3F4F6, ícone de lupa)
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = "Pesquisar conversas...",
                        color = Color(0xFF94A3B8),
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Color(0xFF64748B)
                    )
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SoftGraySurface,
                    unfocusedContainerColor = SoftGraySurface,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(24.dp)
            )

            // Lista de conversas recentes (LazyColumn)
            if (filteredConversations.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nenhuma conversa encontrada",
                        fontSize = 14.sp,
                        color = Color(0xFF64748B)
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredConversations, key = { it.id }) { conversation ->
                        ConversationRow(
                            conversation = conversation,
                            onClick = { onConversationSelected(conversation) }
                        )
                    }
                }
            }
        }
    }
    if (showCreateGroupDialog) {
        AlertDialog(
            onDismissRequest = {
                showCreateGroupDialog = false
            },
            title = {
                Text("Criar novo grupo")
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = newGroupName,
                        onValueChange = {
                            newGroupName = it
                            createGroupError = null
                        },
                        label = {
                            Text("Nome do grupo")
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    createGroupError?.let { error ->
                        Text(
                            text = error,
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newGroupName.isBlank()) {
                            createGroupError = "Escreve um nome para o grupo."
                        } else {
                            scope.launch {
                                val result = RoomsApi.createRoom(
                                    newGroupName.trim()
                                )

                                result.onSuccess {
                                    val updatedRooms = RoomsApi.getRooms()

                                    if (updatedRooms.isSuccess) {
                                        conversations = updatedRooms.getOrDefault(emptyList()).map { room ->
                                            Conversation(
                                                id = room.id,
                                                targetId = room.id,
                                                title = room.name,
                                                lastMessage = "",
                                                lastMessageTime = "",
                                                unreadCount = 0,
                                                isGroup = true
                                            )
                                        }
                                    }

                                    showCreateGroupDialog = false
                                    newGroupName = ""
                                }.onFailure { error ->
                                    createGroupError = error.message
                                        ?: "Não foi possível criar o grupo."
                                }
                            }
                        }
                    }
                ) {
                    Text("Criar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showCreateGroupDialog = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }}


@Composable
fun ConversationRow(
    conversation: Conversation,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar circular (52.dp) com iniciais e bolinha verde de estado online
        Box {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(
                        if (conversation.isGroup) RoyalBlueContainer else Color(0xFFE2E8F0)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (conversation.isGroup) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = null,
                        tint = RoyalBlue,
                        modifier = Modifier.size(26.dp)
                    )
                } else {
                    Text(
                        text = conversation.title.take(1).uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = RoyalBlueDark
                    )
                }
            }

            // Bolinha verde de estado online
            Box(
                modifier = Modifier
                    .size(13.dp)
                    .clip(CircleShape)
                    .background(OnlineGreen)
                    .align(Alignment.BottomEnd)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Coluna central: Nome em negrito (16.sp) e pré-visualização da última mensagem
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = conversation.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = conversation.lastMessage,
                fontSize = 13.sp,
                color = Color(0xFF64748B),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Coluna direita: Hora da mensagem (12.sp) e badge azul de não lidas
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = conversation.lastMessageTime,
                fontSize = 12.sp,
                color = if (conversation.unreadCount > 0) RoyalBlue else Color(0xFF94A3B8),
                fontWeight = if (conversation.unreadCount > 0) FontWeight.Bold else FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (conversation.unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(RoyalBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${conversation.unreadCount}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    PulseChatTheme {
        HomeScreen(
            onConversationSelected = {},
            onNavigateToContacts = {},
            onNavigateToProfile = {}
        )
    }
}
