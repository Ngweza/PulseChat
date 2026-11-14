package pt.iade.ei.pulsechat.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.iade.ei.pulsechat.data.models.SampleData
import pt.iade.ei.pulsechat.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSettingsScreen(
    onLogout: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val user = SampleData.currentUser
    var notificationsEnabled by remember { mutableStateOf(true) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Perfil",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
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
        containerColor = SoftGraySurface,
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Topo: Cabeçalho com fotografia/avatar circular grande de Gianni Lopes
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(RoyalBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.fullName.take(1).uppercase(),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = user.fullName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )

                    Text(
                        text = "@${user.username} • ${user.email}",
                        fontSize = 13.sp,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Secção CONTA (cartão branco arredondado 16.dp com divisórias finas)
            Text(
                text = "CONTA",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF94A3B8),
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 4.dp, bottom = 6.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column {
                    SettingsItem(
                        icon = Icons.Default.Edit,
                        title = "Editar perfil",
                        onClick = { }
                    )
                    HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                    SettingsItem(
                        icon = Icons.Default.Lock,
                        title = "Alterar palavra-passe",
                        onClick = { }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Secção PREFERÊNCIAS (cartão branco arredondado 16.dp com divisórias finas)
            Text(
                text = "PREFERÊNCIAS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF94A3B8),
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 4.dp, bottom = 6.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column {
                    SettingsSwitchItem(
                        icon = Icons.Default.Notifications,
                        title = "Notificações",
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it }
                    )
                    HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                    SettingsItem(
                        icon = Icons.Default.Security,
                        title = "Privacidade",
                        onClick = { }
                    )
                    HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                    SettingsItem(
                        icon = Icons.Default.Info,
                        title = "Sobre o PulseChat (v1.0)",
                        onClick = { }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Secção de saída: Cartão branco arredondado com botão centrado a vermelho "Terminar sessão"
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { showLogoutDialog = true },
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
                        text = "Terminar sessão",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = ErrorRed
                    )
                }
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Terminar sessão", fontWeight = FontWeight.Bold) },
            text = { Text("Tem a certeza de que pretende terminar a sessão?") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("Sair")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = RoyalBlue,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF0F172A),
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color(0xFFCBD5E1),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun SettingsSwitchItem(
    icon: ImageVector,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = RoyalBlue,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF0F172A),
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = RoyalBlue
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileSettingsScreenPreview() {
    PulseChatTheme {
        ProfileSettingsScreen(
            onLogout = {},
            onNavigateBack = {}
        )
    }
}
