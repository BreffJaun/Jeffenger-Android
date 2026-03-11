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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.navigation.toRoute
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
import com.example.jeffenger.ui.viewmodels.AuthViewModel
import com.example.jeffenger.ui.viewmodels.SettingsViewModel
import com.example.jeffenger.utils.debugging.LogComposable
import com.example.jeffenger.utils.debugging.LogStateMap
import com.example.jeffenger.utils.model.ChatTopBarUiState
import de.syntax_institut.projektwoche1.ui.component.TopBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppStart(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = viewModel()
) {
    LogComposable("AppStart") {
        val scheme = MaterialTheme.colorScheme

        // NAVIGATION
//        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        // UI STATE
        val authState by authViewModel.authState.collectAsState()

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

//        LogStateMap(
//            "AppStart",
//            "selectedTab" to selectedTab
//        )

        LaunchedEffect(authState) {
            if (authState == null) {
                navController.navigate(ChatsRoute) {
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }

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
            },

            topBar = {
                TopBar(
                    currentRoute = currentRoute,
                    onBackClick = { navController.popBackStack() },
                    onAddChatClick = {
                        if (currentRoute?.startsWith(ChatsRoute::class.qualifiedName ?: "") == true) {
                            // Trigger event
                            CoroutineScope(Dispatchers.Main)
                                .launch {
                                openNewChatSheet.emit(Unit) // Sends Event WITHOUT Data, just Signl
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
//                                popUpTo(ChatsRoute) { saveState = true }
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }

        ) { p ->
            NavHost(
                navController = navController,
//                startDestination = selectedTab.route,
                startDestination = ChatsRoute,
                modifier = Modifier.padding(p)
            ) {
                composable<ChatsRoute> {
                    ChatsScreen(
                        openNewChatSheetFlow = openNewChatSheet,
                        snackbarHostState = snackbarHostState,
                        onNavigateToDetail = { chatId, companyId ->
                            navController.navigate(
                                ChatRoute(
                                    id = chatId,
                                    companyId = companyId
                                )
                            )
                        }
                    )
                }

                composable<ChatRoute> {
                    ChatScreen(
                        snackbarHostState = snackbarHostState,
                        onBack = { navController.popBackStack() },
                        onTopBarStateChange = { state, participants ->
                            chatTopBarState = state
                            chatParticipants = participants
                        }
                    )
                }

                composable<CalendarRoute> { backStack ->

                    val eventId by backStack.savedStateHandle
                        .getStateFlow<String?>("openEventId", null)
                        .collectAsState()

                    val calendarMessage by backStack.savedStateHandle
                        .getStateFlow<String?>("calendarMessage", null)
                        .collectAsState()

                    CalendarScreen(
                        onBack = { navController.popBackStack() },
                        showCreateEvent = showCreateEvent,
                        onDismissCreateEvent = { showCreateEvent = false },
                        openEventId = eventId,
                        calendarMessage = calendarMessage,
                        snackbarHostState = snackbarHostState,
                        clearOpenEventId = {
                            backStack.savedStateHandle["openEventId"] = null
                        }
                    )

                    LaunchedEffect(calendarMessage) {
                        if (calendarMessage != null) {
                            backStack.savedStateHandle["calendarMessage"] = null
                        }
                    }
                }


                composable<SettingsRoute> {
                    SettingsScreen(
                        snackbarHostState = snackbarHostState,
                        onBack = { navController.popBackStack() },
                        onLogout = {
                            navController.navigate(ChatsRoute) {
                                popUpTo(navController.graph.id) { inclusive = true }
                                launchSingleTop = true
                            }
                            authViewModel.logout()
                        }
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
    }
}


