package pt.iade.ei.pulsechat.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import pt.iade.ei.pulsechat.ui.screens.auth.LoginScreen
import pt.iade.ei.pulsechat.ui.screens.auth.RegisterScreen
import pt.iade.ei.pulsechat.ui.screens.chat.ChatRoomScreen
import pt.iade.ei.pulsechat.ui.screens.contacts.ContactsScreen
import pt.iade.ei.pulsechat.ui.screens.group.GroupDetailsScreen
import pt.iade.ei.pulsechat.ui.screens.home.HomeScreen
import pt.iade.ei.pulsechat.ui.screens.profile.ProfileSettingsScreen
import pt.iade.ei.pulsechat.ui.screens.splash.SplashScreen
import java.net.URLDecoder
import java.net.URLEncoder
import pt.iade.ei.pulsechat.data.network.SessionManager
import java.nio.charset.StandardCharsets

object Screen {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val CHAT = "chat/{roomId}/{roomName}"
    const val CONTACTS = "contacts"
    const val GROUP_DETAILS = "group_details/{groupId}"
    const val PROFILE = "profile"

    fun createChatRoute(roomId: String, roomName: String): String {
        val encodedName = URLEncoder.encode(roomName, StandardCharsets.UTF_8.toString())
        return "chat/$roomId/$encodedName"
    }

    fun createGroupDetailsRoute(groupId: String): String {
        return "group_details/$groupId"
    }
}

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.SPLASH
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // 1. SplashScreen
        composable(Screen.SPLASH) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.HOME) {
                        popUpTo(Screen.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.LOGIN) {
                        popUpTo(Screen.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        // 2. LoginScreen
        composable(Screen.LOGIN) {
            LoginScreen(
                onLoginSuccess = { token ->
                    SessionManager.token = token

                    navController.navigate(Screen.HOME) {
                        popUpTo(Screen.LOGIN) {
                            inclusive = true
                        }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.REGISTER)}
            )
        }

        // 3. RegisterScreen
        composable(Screen.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.HOME) {
                        popUpTo(Screen.LOGIN) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // 4. HomeScreen (Lista de conversas)
        composable(Screen.HOME) {
            HomeScreen(
                onConversationSelected = { conversation ->
                    navController.navigate(Screen.createChatRoute(conversation.targetId, conversation.title))
                },
                onNavigateToContacts = {
                    navController.navigate(Screen.CONTACTS)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.PROFILE)
                }
            )
        }

        // 5. ChatRoomScreen
        composable(
            route = Screen.CHAT,
            arguments = listOf(
                navArgument("roomId") { type = NavType.StringType },
                navArgument("roomName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId") ?: ""
            val rawRoomName = backStackEntry.arguments?.getString("roomName") ?: ""
            val roomName = URLDecoder.decode(rawRoomName, StandardCharsets.UTF_8.toString())

            ChatRoomScreen(
                roomId = roomId,
                roomName = roomName,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onOpenGroupDetails = { id ->
                    navController.navigate(Screen.createGroupDetailsRoute(id))
                }
            )
        }

        // 6. ContactsScreen
        composable(Screen.CONTACTS) {
            ContactsScreen(
                onContactSelected = { contact ->
                    navController.navigate(Screen.createChatRoute(contact.id, contact.name))
                },
                onCreateGroupClicked = {
                    navController.navigate(Screen.createGroupDetailsRoute("new_group"))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // 7. GroupDetailsScreen
        composable(
            route = Screen.GROUP_DETAILS,
            arguments = listOf(
                navArgument("groupId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getString("groupId") ?: ""

            GroupDetailsScreen(
                groupId = groupId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // 8. ProfileSettingsScreen
        composable(Screen.PROFILE) {
            ProfileSettingsScreen(
                onLogout = {
                    navController.navigate(Screen.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
