package com.example.jeffenger

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
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
import com.example.jeffenger.ui.auth.AuthScreen
import com.example.jeffenger.ui.screens.ErrorScreen
import com.example.jeffenger.ui.screens.LoadingScreen
import com.example.jeffenger.ui.screens.SplashScreen
import com.example.jeffenger.ui.viewmodels.AuthViewModel
import com.example.jeffenger.utils.state.LoadingState
import org.koin.androidx.compose.koinViewModel

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "dataStore")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val authViewModel: AuthViewModel = koinViewModel()
            val authState by authViewModel.authState.collectAsState()
            val loadingState by authViewModel.loadingState.collectAsState()

            var showSplash by rememberSaveable { mutableStateOf(true) }

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
                                BackgroundWrapper {
                                    AppStart()
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
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppTheme {
//        AppStart()
    }
}