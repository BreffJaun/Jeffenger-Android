package com.example.jeffenger.navigation

import android.content.res.Configuration
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.jeffenger.navigation.components.TabItem
import com.example.jeffenger.navigation.helper.CalendarRoute
import com.example.jeffenger.navigation.helper.ChatRoute
import com.example.jeffenger.navigation.helper.ChatsRoute
import com.example.jeffenger.navigation.helper.SettingsRoute
import com.example.jeffenger.navigation.screens.IosStyleBottomBar
import com.example.jeffenger.ui.screens.CalendarScreen
import com.example.jeffenger.ui.screens.ChatScreen
import com.example.jeffenger.ui.screens.ChatsScreen
import com.example.jeffenger.ui.screens.SettingsScreen
import com.example.jeffenger.ui.theme.AppTheme
import com.example.jeffenger.ui.viewmodels.SettingsViewModel
import com.example.jeffenger.utils.debugging.LogComposable
import com.example.jeffenger.utils.debugging.LogStateMap
import de.syntax_institut.projektwoche1.ui.component.TopBar

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppStart(
//    isDarkMode: Boolean,
//    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel = viewModel()
) {
    LogComposable("AppStart") {
        val scheme = MaterialTheme.colorScheme

        // NAVIGATION
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        // UI STATE
        val selectedTab by rememberSaveable { mutableStateOf(TabItem.CHATS) }

        LogStateMap(
            "AppStart",
            "selectedTab" to selectedTab
        )


        Scaffold(
            modifier = modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                TopBar(
                    currentRoute = currentRoute,
//                    isDarkMode = isDarkMode,
                    onBackClick = { navController.popBackStack() }
                )
            },
            bottomBar = {
                IosStyleBottomBar(
                    currentRoute = currentRoute,
                    onTabSelected = { tab ->
                        navController.navigate(tab.route) {
                            popUpTo(ChatsRoute) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
//                    isDarkMode = isDarkMode
                )
            },
//            floatingActionButton = {}
        ) { p ->
            NavHost(
                navController = navController,
//                startDestination = selectedTab.route,
                startDestination = ChatsRoute,
                modifier = Modifier.padding(p)
            ) {
                composable<ChatsRoute> {
//                    ChatsScreen { chat ->
//                        navController.navigate(
//                            ChatRoute(
//                                id = chat.id,
//                            )
//                        )
//                    }
                    ChatsScreen { chatId ->
                        navController.navigate(
                            ChatRoute(id = chatId)
                        )
                    }
                }

                composable<ChatRoute> {
                    ChatScreen(
                        onBack = { navController.popBackStack() }
                    )
                }

                composable<CalendarRoute> {
                    CalendarScreen(
                        onBack = { navController.popBackStack() }
                    )
                }

                composable<SettingsRoute> {
                    SettingsScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}


@Preview(
    name = "Darkmode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    name = "Lightmode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
private fun AppStartPreview() {
    AppTheme {
//        AppStart()
    }
}
