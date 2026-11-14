package pt.iade.ei.pulsechat.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import pt.iade.ei.pulsechat.data.network.AuthApi
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.iade.ei.pulsechat.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit,
    onNavigateToRegister: () -> Unit,
    modifier: Modifier = Modifier
) {
    var emailOrUsername by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var forgotPasswordSuccessMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = PureWhite,
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.Start
            ) {
                // Topo esquerdo: Quadrado arredondado azul com símbolo de chat
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(RoyalBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubble,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Título e Subtítulo
                Text(
                    text = "Bem-vindo de volta",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Text(
                    text = "Inicie sessão na sua conta PulseChat",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B),
                    modifier = Modifier.padding(top = 4.dp, bottom = 28.dp)
                )

                // Banner de Sucesso
                forgotPasswordSuccessMessage?.let { success ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = RoyalBlueContainer),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = success,
                            color = RoyalBlueDark,
                            modifier = Modifier.padding(12.dp),
                            fontSize = 13.sp
                        )
                    }
                }

                // Banner de Erro
                errorMessage?.let { error ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = error,
                            color = ErrorRed,
                            modifier = Modifier.padding(12.dp),
                            fontSize = 13.sp
                        )
                    }
                }

                // Field Label 1
                Text(
                    text = "Nome de utilizador",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF334155),
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                OutlinedTextField(
                    value = emailOrUsername,
                    onValueChange = {
                        emailOrUsername = it
                        errorMessage = null
                    },
                    placeholder = { Text("joão", color = Color(0xFF94A3B8)) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = RoyalBlue)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RoyalBlue,
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    )
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Field Label 2
                Text(
                    text = "Palavra-passe",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF334155),
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorMessage = null
                    },
                    placeholder = { Text("••••••••", color = Color(0xFF94A3B8)) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = RoyalBlue)
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Ocultar" else "Mostrar",
                                tint = Color(0xFF64748B)
                            )
                        }
                    },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RoyalBlue,
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    )
                )

                // Link alinhado à direita
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { showForgotPasswordDialog = true }) {
                        Text(
                            text = "Esqueceu-se da palavra-passe?",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = RoyalBlue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Botão principal Azul Royal
                Button(
                    onClick = {
                        if (emailOrUsername.isBlank() || password.isBlank()) {
                            errorMessage = "Por favor, preencha todos os campos."
                            return@Button
                        }

                        isLoading = true
                        errorMessage = null

                        scope.launch {
                            val result = AuthApi.login(
                                username = emailOrUsername.trim(),
                                password = password
                            )

                            if (result.token != null) {
                                onLoginSuccess(result.token)
                            } else {
                                errorMessage = result.error ?: "Erro ao iniciar sessão."
                                isLoading = false
                            }
                        }
                    },
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Iniciar Sessão",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Rodapé centrado
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ainda não tem conta? ",
                        fontSize = 14.sp,
                        color = Color(0xFF64748B)
                    )
                    TextButton(
                        onClick = onNavigateToRegister,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "Registar",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = RoyalBlue
                        )
                    }
                }
            }
        }
    }

    if (showForgotPasswordDialog) {
        ForgotPasswordDialog(
            initialEmail = emailOrUsername,
            onDismiss = { showForgotPasswordDialog = false },
            onSendResetLink = { targetEmail ->
                showForgotPasswordDialog = false
                forgotPasswordSuccessMessage = "Instruções de recuperação enviadas para $targetEmail."
            }
        )
    }
}

@Composable
fun ForgotPasswordDialog(
    initialEmail: String,
    onDismiss: () -> Unit,
    onSendResetLink: (String) -> Unit
) {
    var email by remember { mutableStateOf(initialEmail) }
    var dialogError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Recuperar Palavra-passe",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = "Introduza o seu e-mail para receber o link de reposição.",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                dialogError?.let {
                    Text(
                        text = it,
                        color = ErrorRed,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        dialogError = null
                    },
                    label = { Text("E-mail") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (email.isBlank() || !email.contains("@")) {
                        dialogError = "Introduza um e-mail válido."
                    } else {
                        onSendResetLink(email)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue)
            ) {
                Text("Enviar")
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
fun LoginScreenPreview() {
    PulseChatTheme {
        LoginScreen(
            onLoginSuccess = { _ -> },
            onNavigateToRegister = {}
        )
    }
}
