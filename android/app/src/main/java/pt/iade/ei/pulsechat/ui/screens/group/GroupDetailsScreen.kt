package pt.iade.ei.pulsechat.ui.screens.group

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.filled.Shield
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
import pt.iade.ei.pulsechat.data.models.GroupMember
import pt.iade.ei.pulsechat.data.models.SampleData
import pt.iade.ei.pulsechat.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailsScreen(
    groupId: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val groupDetails = remember(groupId) { SampleData.sampleGroupDetails }
    var members by remember { mutableStateOf(groupDetails.members) }
    var showLeaveDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalhes do grupo",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
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
                actions = {
                    TextButton(onClick = { /* Editar */ }) {
                        Text(
                            text = "Editar",
                            fontWeight = FontWeight.Bold,
                            color = RoyalBlue,
                            fontSize = 14.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PureWhite)
            )
        },
        containerColor = SoftGraySurface,
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Secção central: Fotografia/Avatar grande do grupo (84.dp)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(84.dp)
                                .clip(CircleShape)
                                .background(RoyalBlueContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Group,
                                contentDescription = null,
                                modifier = Modifier.size(46.dp),
                                tint = RoyalBlue
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = groupDetails.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = groupDetails.description,
                            fontSize = 13.sp,
                            color = Color(0xFF64748B),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Criado por Gianni Lopes • Replicação Ativa",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = RoyalBlue
                        )
                    }
                }
            }

            // Botão de ação azul: "+ Adicionar participantes"
            item {
                Button(
                    onClick = { /* Adicionar */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RoyalBlue,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "+ Adicionar participantes",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            // Cartão agrupado com Lista de membros
            item {
                Text(
                    text = "MEMBROS DO GRUPO (${members.size})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column {
                        members.forEachIndexed { index, member ->
                            MemberRow(
                                member = member,
                                onRemoveMember = { target ->
                                    members = members.filter { it.user.id != target.user.id }
                                }
                            )
                            if (index < members.size - 1) {
                                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                            }
                        }
                    }
                }
            }

            // Botão inferior em cartão branco com texto vermelho: "Sair do grupo"
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { showLeaveDialog = true },
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = null,
                            tint = ErrorRed,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Sair do grupo",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = ErrorRed
                        )
                    }
                }
            }
        }
    }

    if (showLeaveDialog) {
        AlertDialog(
            onDismissRequest = { showLeaveDialog = false },
            title = { Text("Sair do grupo", fontWeight = FontWeight.Bold) },
            text = { Text("Tem a certeza de que pretende sair do grupo '${groupDetails.name}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        showLeaveDialog = false
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("Sair")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLeaveDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun MemberRow(
    member: GroupMember,
    onRemoveMember: (GroupMember) -> Unit
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
                .background(Color(0xFFE2E8F0)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = member.user.fullName.take(1).uppercase(),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF334155)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = member.user.fullName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                if (member.isAdmin) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(RoyalBlueContainer)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                modifier = Modifier.size(10.dp),
                                tint = RoyalBlueDark
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "Admin",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = RoyalBlueDark
                            )
                        }
                    }
                }
            }

            Text(
                text = member.user.email,
                fontSize = 12.sp,
                color = Color(0xFF64748B)
            )
        }

        if (!member.isAdmin) {
            IconButton(onClick = { onRemoveMember(member) }) {
                Icon(
                    imageVector = Icons.Default.RemoveCircleOutline,
                    contentDescription = "Remover",
                    tint = Color(0xFFCBD5E1)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GroupDetailsScreenPreview() {
    PulseChatTheme {
        GroupDetailsScreen(
            groupId = "room_2",
            onNavigateBack = {}
        )
    }
}
