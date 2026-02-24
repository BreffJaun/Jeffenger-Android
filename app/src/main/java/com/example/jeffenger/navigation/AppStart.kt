package com.example.jeffenger.navigation

import android.content.res.Configuration
import android.net.http.SslCertificate.restoreState
import android.net.http.SslCertificate.saveState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.jeffenger.data.remote.model.ui_model.AvatarUiModel
import com.example.jeffenger.navigation.components.TabItem
import com.example.jeffenger.navigation.helper.CalendarRoute
import com.example.jeffenger.navigation.helper.ChatRoute
import com.example.jeffenger.navigation.helper.ChatsRoute
import com.example.jeffenger.navigation.helper.SettingsRoute
import com.example.jeffenger.navigation.screens.IosStyleBottomBar
import com.example.jeffenger.ui.calendar.CalendarScreen
import com.example.jeffenger.ui.chat.ChatInfoDialog
import com.example.jeffenger.ui.chat.ChatScreen
import com.example.jeffenger.ui.chats.ChatsScreen
import com.example.jeffenger.ui.settings.SettingsScreen
import com.example.jeffenger.ui.theme.AppTheme
import com.example.jeffenger.ui.theme.UrbanistText
import com.example.jeffenger.ui.viewmodels.SettingsViewModel
import com.example.jeffenger.utils.debugging.LogComposable
import com.example.jeffenger.utils.debugging.LogStateMap
import com.example.jeffenger.utils.model.ChatTopBarUiState
import de.syntax_institut.projektwoche1.ui.component.TopBar
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppStart(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel = viewModel()
) {
    LogComposable("AppStart") {
        val scheme = MaterialTheme.colorScheme

        // NAVIGATION
//        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        // UI STATE
        val selectedTab by rememberSaveable { mutableStateOf(TabItem.CHATS) }

        // SHARED STATE
        val openNewChatSheet = remember { MutableSharedFlow<Unit>() }

        var chatTopBarState by remember { mutableStateOf<ChatTopBarUiState?>(null) }
        var chatParticipants by remember {
            mutableStateOf<List<Pair<AvatarUiModel, String>>>(emptyList())
        }
        var showChatInfo by remember { mutableStateOf(false) }

        // CALENDAR EVENT
        var showCreateEvent by remember { mutableStateOf(false) }

        // SNACKBAR -> TOAST
        val snackbarHostState = remember { SnackbarHostState() }

        LogStateMap(
            "AppStart",
            "selectedTab" to selectedTab
        )


        Scaffold(
            modifier = modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState
                ) { snackbarData ->

                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = snackbarData.visuals.message,
                            style = UrbanistText.BodyRegular,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
//                SnackbarHost(
//                    hostState = snackbarHostState
//                )
            },

            topBar = {
                TopBar(
                    currentRoute = currentRoute,
                    onBackClick = { navController.popBackStack() },
                    onAddChatClick = {
                        if (currentRoute?.startsWith(ChatsRoute::class.qualifiedName ?: "") == true) {
                            // Trigger event
                            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main)
                                .launch {
                                openNewChatSheet.emit(Unit)
                            }
                        }
                    },
                    chatTopBarState = chatTopBarState,
                    onChatHeaderClick = {
                        showChatInfo = true
                    },
                    onChatCalendarClick = { },
                    onCalendarAddClick = { showCreateEvent = true }
                )
            },
            bottomBar = {
                val isChatRoute =
                    currentRoute?.startsWith(ChatRoute::class.qualifiedName ?: "") == true

                if (!isChatRoute) {
                    IosStyleBottomBar(
                        currentRoute = currentRoute,
                        onTabSelected = { tab ->
                            navController.navigate(tab.route) {
                                popUpTo(ChatsRoute) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
//            bottomBar = {
//                IosStyleBottomBar(
//                    currentRoute = currentRoute,
//                    onTabSelected = { tab ->
//                        navController.navigate(tab.route) {
//                            popUpTo(ChatsRoute) {
//                                saveState = true
//                            }
//                            launchSingleTop = true
//                            restoreState = true
//                        }
//                    },
//                )
//            }
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

//                    ChatsScreen { chatId ->
//                        navController.navigate(
//                            ChatRoute(id = chatId)
//                        )
//                    }

                    ChatsScreen(
                        openNewChatSheetFlow = openNewChatSheet,
                        onNavigateToDetail = { chatId ->
                            navController.navigate(
                                ChatRoute(id = chatId)
                            )
                        }
                    )

                }

//                composable<ChatRoute> {
//                    ChatScreen(
//                        onBack = { navController.popBackStack() },
//                        onTopBarStateChange = { state ->
//                            chatTopBarState = state
//                        }
//                    )
//                }

                composable<ChatRoute> {
                    ChatScreen(
                        snackbarHostState = snackbarHostState,
                        onBack = { navController.popBackStack() },
                        onTopBarStateChange = { state, participants ->
                            chatTopBarState = state
                            chatParticipants = participants
                        }
//                        onTopBarStateChange = { state ->
//                            chatTopBarState = state
//                        }
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

        if (showChatInfo && chatTopBarState != null) {
            ChatInfoDialog(
                state = chatTopBarState!!,
                participants = chatParticipants,
                onDismiss = { showChatInfo = false }
            )
        }

        if (showCreateEvent) {
            // TODO: CreateEventDialog / BottomSheet
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
