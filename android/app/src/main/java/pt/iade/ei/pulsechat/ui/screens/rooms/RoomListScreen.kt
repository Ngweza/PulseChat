package pt.iade.ei.pulsechat.ui.screens.rooms

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import pt.iade.ei.pulsechat.data.network.RoomsApi
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.iade.ei.pulsechat.data.network.RoomInfo
import pt.iade.ei.pulsechat.ui.theme.PulseChatTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomListScreen(
    onRoomSelected: (RoomInfo) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var rooms by remember { mutableStateOf(emptyList<RoomInfo>()) }
    var searchQuery by remember { mutableStateOf("") }
    var showCreateRoomDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        isLoading = true

        val result = RoomsApi.getRooms()

        result.onSuccess { loadedRooms ->
            rooms = loadedRooms
            errorMessage = null
        }.onFailure { error ->
            errorMessage = error.message ?: "Não foi possível carregar as salas."
        }

        isLoading = false
    }

    val filteredRooms = remember(searchQuery, rooms) {
        if (searchQuery.isBlank()) {
            rooms
        } else {
            rooms.filter {
                it.name.contains(searchQuery, ignoreCase = true)
            }
        }
    }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "PulseChat",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Salas de Chat Ativas",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Terminar Sessão",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateRoomDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Criar Sala"
                )
            }
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Pesquisar salas de conversa...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(12.dp)
            )

            // Rooms List
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (errorMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = errorMessage ?: "Erro desconhecido",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            } else if (filteredRooms.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nenhuma sala encontrada",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredRooms, key = { it.id }) { room ->
                        RoomCard(
                            room = room,
                            onClick = { onRoomSelected(room) }
                        )
                    }
                }
            }
        }
    }

    // Create Room Dialog
    if (showCreateRoomDialog) {
        CreateRoomDialog(
            onDismiss = { showCreateRoomDialog = false },
            onCreateRoom = { newName, newDesc ->
                scope.launch  {
                    val result = RoomsApi.createRoom(newName)

                    result.onSuccess { newRoom ->
                        rooms = rooms + newRoom
                        showCreateRoomDialog = false
                    }.onFailure { error ->
                        errorMessage = error.message ?: "Não foi possível criar a sala."
                    }
                }
            }
        )
    }
}

@Composable
fun RoomCard(
    room: RoomInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = room.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = room.role,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = room.role,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
fun CreateRoomDialog(
    onDismiss: () -> Unit,
    onCreateRoom: (name: String, description: String) -> Unit
) {
    var roomName by remember { mutableStateOf("") }
    var roomDescription by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Criar Nova Sala",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                error?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                OutlinedTextField(
                    value = roomName,
                    onValueChange = {
                        roomName = it
                        error = null
                    },
                    label = { Text("Nome da Sala") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = roomDescription,
                    onValueChange = {
                        roomDescription = it
                        error = null
                    },
                    label = { Text("Descrição / Tópico") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (roomName.isBlank()) {
                        error = "O nome da sala não pode estar em branco."
                    } else {
                        onCreateRoom(roomName, roomDescription)
                    }
                }
            ) {
                Text("Criar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun RoomListScreenPreview() {
    PulseChatTheme {
        RoomListScreen(
            onRoomSelected = {},
            onLogout = {}
        )
    }
}
