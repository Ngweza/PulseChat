package pt.iade.ei.pulsechat.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val passwordsMatch = remember(password, confirmPassword) {
        confirmPassword.isEmpty() || password == confirmPassword
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Criar conta",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Text(
                    text = "Crie uma conta para começar a comunicar com segurança",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B),
                    modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                )

                // Error Message
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

                // 1. Nome
                Text(
                    text = "Nome completo",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF334155),
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        errorMessage = null
                    },
                    placeholder = { Text("Gianni Lopes", color = Color(0xFF94A3B8)) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = RoyalBlue)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RoyalBlue,
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Email
                Text(
                    text = "Email",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF334155),
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        errorMessage = null
                    },
                    placeholder = { Text("gianni@iade.pt", color = Color(0xFF94A3B8)) },
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

                Spacer(modifier = Modifier.height(14.dp))

                // 3. Palavra-passe
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
                                contentDescription = null,
                                tint = Color(0xFF64748B)
                            )
                        }
                    },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RoyalBlue,
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 4. Confirmar palavra-passe
                Text(
                    text = "Confirmar palavra-passe",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF334155),
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        errorMessage = null
                    },
                    placeholder = { Text("••••••••", color = Color(0xFF94A3B8)) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = RoyalBlue)
                    },
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = Color(0xFF64748B)
                            )
                        }
                    },
                    isError = !passwordsMatch,
                    supportingText = {
                        if (!passwordsMatch) {
                            Text("As palavras-passe não coincidem", color = ErrorRed, fontSize = 12.sp)
                        }
                    },
                    singleLine = true,
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RoyalBlue,
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        errorBorderColor = ErrorRed
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Botão principal Azul Royal
                Button(
                    onClick = {
                        when {
                            name.isBlank() || email.isBlank() || password.isBlank() -> {
                                errorMessage = "Por favor, preencha todos os campos."
                            }
                            !email.contains("@") -> {
                                errorMessage = "Por favor, introduza um email válido."
                            }
                            password != confirmPassword -> {
                                errorMessage = "As palavras-passe não coincidem."
                            }
                            password.length < 6 -> {
                                errorMessage = "A palavra-passe deve ter pelo menos 6 caracteres."
                            }
                            else -> {
                                isLoading = true
                                onRegisterSuccess()
                            }
                        }
                    },
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RoyalBlue,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Criar conta",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Rodapé
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Já tem conta? ",
                        fontSize = 14.sp,
                        color = Color(0xFF64748B)
                    )
                    TextButton(
                        onClick = onNavigateBack,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "Iniciar sessão",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = RoyalBlue
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    PulseChatTheme {
        RegisterScreen(
            onRegisterSuccess = {},
            onNavigateBack = {}
        )
    }
}
