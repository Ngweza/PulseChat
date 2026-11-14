package pt.iade.ei.pulsechat.ui.screens.contacts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.iade.ei.pulsechat.data.models.Contact
import pt.iade.ei.pulsechat.data.models.SampleData
import pt.iade.ei.pulsechat.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    onContactSelected: (Contact) -> Unit,
    onCreateGroupClicked: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var contacts by remember { mutableStateOf(SampleData.sampleContacts) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredContacts = remember(searchQuery, contacts) {
        if (searchQuery.isBlank()) {
            contacts
        } else {
            contacts.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.username.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Contactos",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color(0xFF0F172A)
                    )
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PureWhite)
            )
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
            // Barra de pesquisa
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text("Pesquisar utilizadores...", color = Color(0xFF94A3B8), fontSize = 14.sp)
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
                    .padding(vertical = 10.dp),
                shape = RoundedCornerShape(24.dp)
            )

            // Carrossel horizontal no topo com avatares circulares de contactos frequentes
            Text(
                text = "FREQUENTES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF94A3B8),
                modifier = Modifier.padding(vertical = 6.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(contacts, key = { "freq_${it.id}" }) { contact ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(60.dp)
                            .clickable { onContactSelected(contact) }
                    ) {
                        Box {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(RoyalBlueContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = contact.name.take(1).uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = RoyalBlue
                                )
                            }

                            if (contact.isOnline) {
                                Box(
                                    modifier = Modifier
                                        .size(13.dp)
                                        .clip(CircleShape)
                                        .background(OnlineGreen)
                                        .align(Alignment.BottomEnd)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = contact.name.split(" ").firstOrNull() ?: contact.name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF0F172A),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Botão/Cartão de ação destacado: "Criar novo grupo"
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onCreateGroupClicked() },
                colors = CardDefaults.cardColors(containerColor = RoyalBlueContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(RoyalBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.GroupAdd,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Text(
                        text = "Criar novo grupo",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalBlueDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "TODOS OS UTILIZADORES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF94A3B8),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Lista vertical alfabética de utilizadores
            if (filteredContacts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nenhum contacto encontrado",
                        fontSize = 14.sp,
                        color = Color(0xFF64748B)
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(filteredContacts, key = { it.id }) { contact ->
                        ContactRow(
                            contact = contact,
                            onClick = { onContactSelected(contact) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ContactRow(
    contact: Contact,
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
        Box {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE2E8F0)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = contact.name.take(1).uppercase(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF334155)
                )
            }

            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(if (contact.isOnline) OnlineGreen else Color(0xFFCBD5E1))
                    .align(Alignment.BottomEnd)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = contact.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
            Text(
                text = contact.statusMessage,
                fontSize = 12.sp,
                color = Color(0xFF64748B),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ContactsScreenPreview() {
    PulseChatTheme {
        ContactsScreen(
            onContactSelected = {},
            onCreateGroupClicked = {},
            onNavigateBack = {}
        )
    }
}
