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
import com.example.jeffenger.navigation.helper.ChatRoute
import com.example.jeffenger.ui.auth.AuthScreen
import com.example.jeffenger.ui.screens.ErrorScreen
import com.example.jeffenger.ui.screens.LoadingScreen
import com.example.jeffenger.ui.screens.SplashScreen
import com.example.jeffenger.ui.viewmodels.AuthViewModel
import com.example.jeffenger.utils.state.LoadingState
import org.koin.androidx.compose.koinViewModel

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "dataStore")

class MainActivity : ComponentActivity() {

    // Muss als Property der Activity hier oben stehen (nicht in setContent)
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

                    when (loadingState) {

                        is LoadingState.Loading -> {
                            LoadingScreen(
                                message = (loadingState as LoadingState.Loading).message
                            )
                        }

                        is LoadingState.Error -> {
                            ErrorScreen(
                                message = (loadingState as LoadingState.Error).message,
                                onRetry = {
                                    authViewModel.retryLastAction()
                                }
                            )
                        }

                        else -> {
                            if (authState != null) {

                                NotificationPermissionCoordinator(
                                    context = context,
                                    db = db
                                )

                                BackgroundWrapper {
                                    AppStart(navController = navController)
                                }

                                // Läuft bei Cold Start UND jedes Mal neu bei Warm Start
                                LaunchedEffect(currentIntent) {
                                    handleIntentIfNeeded(currentIntent, navController)

                                    // Optional aber sinnvoll:
                                    // verhindert, dass derselbe Intent nochmal feuert (z.B. Recompose)
                                    intentState.value = null
                                }
                            } else {
                                BackgroundWrapper {
                                    AuthScreen()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        intentState.value = intent
    }

    private fun handleIntentIfNeeded(
        intent: Intent?,
        navController: NavHostController
    ) {
        if (intent?.action != "OPEN_CHAT") return

        val chatId = intent.getStringExtra("chatId") ?: return

        navController.navigate(ChatRoute(id = chatId)) {
            launchSingleTop = true
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