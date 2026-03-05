package com.example.jeffenger

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.Preferences
import com.example.jeffenger.ui.theme.AppTheme
import com.example.jeffenger.navigation.AppStart
import de.syntax_institut.jetpack.a04_05_online_shopper.utilities.BackgroundWrapper
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import com.example.jeffenger.data.notifications.NotificationPermissionCoordinator
import com.example.jeffenger.navigation.helper.CalendarRoute
import com.example.jeffenger.navigation.helper.ChatRoute
import com.example.jeffenger.ui.auth.AuthScreen
import com.example.jeffenger.ui.screens.ErrorScreen
import com.example.jeffenger.ui.screens.LoadingScreen
import com.example.jeffenger.ui.screens.SplashScreen
import com.example.jeffenger.ui.viewmodels.AuthViewModel
import com.example.jeffenger.utils.state.AppForegroundState
import com.example.jeffenger.utils.state.LoadingState
import org.koin.androidx.compose.koinViewModel

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "dataStore")

class MainActivity : ComponentActivity() {

    // Holds the latest launch intent for cold/warm start deep links; cleared after handling to avoid reprocessing on recomposition.
    private val intentState = mutableStateOf<Intent?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialen Intent direkt setzen (für Cold Start)
        intentState.value = intent

        setContent {
            val context = androidx.compose.ui.platform.LocalContext.current
            val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()

            val authViewModel: AuthViewModel = koinViewModel()
            val authState by authViewModel.authState.collectAsState()
            val loadingState by authViewModel.loadingState.collectAsState()

            var showSplash by rememberSaveable { mutableStateOf(true) }

            val navController = androidx.navigation.compose.rememberNavController()

            // INTENT STATE
            val currentIntent = intentState.value

            AppTheme {
                if (showSplash) {

                    SplashScreen(
                        onFinished = { showSplash = false }
                    )

                } else {

//                    when (loadingState) {
//
//                        is LoadingState.Loading -> {
//                            LoadingScreen(
//                                message = (loadingState as LoadingState.Loading).message
//                            )
//                        }
//
//                        is LoadingState.Error -> {
//                            ErrorScreen(
//                                message = (loadingState as LoadingState.Error).message,
//                                onRetry = {
//                                    authViewModel.retryLastAction()
//                                }
//                            )
//                        }

//                        else -> {
                            if (authState != null) {

                                NotificationPermissionCoordinator(
                                    context = context,
                                    db = db
                                )

                                BackgroundWrapper {
                                    AppStart(navController = navController)
                                }

                                // Handles deep link intents (e.g., notification tap). Runs once
                                // per new intent and clears state to prevent duplicate navigation.
                                // -> Runs during cold start AND every time during warm start
                                LaunchedEffect(currentIntent) {
                                    handleIntentIfNeeded(currentIntent, navController)

                                    // Optional but useful:
                                    // prevents the SAME intent from firing again
                                    intentState.value = null
                                }
                            } else {
                                BackgroundWrapper {
                                    AuthScreen()
                                }
                            }
//                        }
                    }
//                }
            }
        }
    }

    // Deep link handler for notification intents.
    // Navigates directly to the requested chat when action == "OPEN_CHAT".
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        intentState.value = intent
    }

    override fun onResume() {
        super.onResume()
        AppForegroundState.isAppInForeground = true
    }

    override fun onPause() {
        super.onPause()
        AppForegroundState.isAppInForeground = false
    }

    private fun handleIntentIfNeeded(
        intent: Intent?,
        navController: NavHostController
    ) {

        when (intent?.action) {

            "OPEN_CHAT" -> {
                val chatId = intent.getStringExtra("chatId") ?: return
                val companyId = intent.getStringExtra("companyId") ?: return
                navController.navigate(
                    ChatRoute(
                        id = chatId,
                        companyId = companyId
                    )
                ) {

                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }

                    launchSingleTop = true
                }
//                navController.navigate(
//                    ChatRoute(
//                        id = chatId,
//                        companyId = companyId
//                    )
//                ) {
//                    launchSingleTop = true
//                }
            }

            "OPEN_CALENDAR_EVENT" -> {
                val eventId = intent.getStringExtra("eventId") ?: return

//                navController.navigate(CalendarRoute) {
//                    launchSingleTop = true
//                    restoreState = true
//                }

                navController.navigate(CalendarRoute) {

                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }

                    launchSingleTop = true
                    restoreState = true
                }

                val calendarEntry = navController.getBackStackEntry(CalendarRoute)
                calendarEntry.savedStateHandle["openEventId"] = eventId
            }


            "OPEN_CALENDAR" -> {

                val message = intent.getStringExtra("calendarMessage")

                navController.navigate(CalendarRoute) {
                    launchSingleTop = true
                    restoreState = true
                }

                val calendarEntry = navController.getBackStackEntry(CalendarRoute)

                if (message != null) {
                    calendarEntry.savedStateHandle["calendarMessage"] = message
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppTheme {
//        AppStart()
    }
}