package pt.iade.ei.pulsechat.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import pt.iade.ei.pulsechat.ui.theme.*

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    isAuthenticated: Boolean = false
) {
    val scale = remember { Animatable(0.7f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800)
        )
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800)
        )

        delay(2000)

        if (isAuthenticated) {
            onNavigateToHome()
        } else {
            onNavigateToLogin()
        }
    }

    val gradientBrush = remember {
        Brush.verticalGradient(
            colors = listOf(SplashGradStart, SplashGradMid, SplashGradEnd)
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(gradientBrush),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Logótipo geométrico minimalista a branco
            Box(
                modifier = Modifier
                    .scale(scale.value)
                    .alpha(alpha.value)
                    .size(90.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.15f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ChatBubble,
                    contentDescription = "PulseChat Logo",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "PULSECHAT",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp,
                color = Color.White,
                modifier = Modifier.alpha(alpha.value)
            )

            Text(
                text = "Chat Distribuído & Moderação IA",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier
                    .padding(top = 6.dp, bottom = 48.dp)
                    .alpha(alpha.value)
            )

            // Indicador de carregamento discreto
            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 2.5.dp,
                modifier = Modifier
                    .size(24.dp)
                    .alpha(alpha.value)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    PulseChatTheme {
        SplashScreen(
            onNavigateToHome = {},
            onNavigateToLogin = {}
        )
    }
}
