package com.example.jeffenger

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.Preferences
import com.example.jeffenger.ui.theme.AppTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jeffenger.navigation.AppStart
import com.example.jeffenger.ui.viewmodels.SettingsViewModel
import de.syntax_institut.jetpack.a04_05_online_shopper.utilities.BackgroundWrapper
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.jeffenger.ui.screens.SplashScreen

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "dataStore")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            var showSplash by remember { mutableStateOf(true) }
            val viewModel: SettingsViewModel = viewModel()
            val darkModePref by viewModel.darkModeEnabled.collectAsState()
//            val isDarkTheme = when (darkModePref) {
//                true -> true
//                false -> false
//                null -> isSystemInDarkTheme()
//            }
            val isDarkTheme = true
            AppTheme(darkTheme = isDarkTheme) {
                if (showSplash) {

                    SplashScreen(
                        onFinished = { showSplash = false }
                    )

                } else {
                    BackgroundWrapper {
                        AppStart(
                            isDarkMode = isDarkTheme,
                            onToggleTheme = {
                                viewModel.setDarkMode(
                                    when (darkModePref) {
                                        null -> true
                                        true -> false
                                        false -> null
                                    }
                                )
                            }
                        )
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