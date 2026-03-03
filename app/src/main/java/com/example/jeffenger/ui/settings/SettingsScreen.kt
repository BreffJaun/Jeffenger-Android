package com.example.jeffenger.ui.settings

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jeffenger.ui.theme.AppTheme
import com.example.jeffenger.ui.viewmodels.AuthViewModel
import com.example.jeffenger.utils.debugging.LogComposable
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = koinViewModel()
) {
    LogComposable("SettingsScreen") {

        val scheme = MaterialTheme.colorScheme

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {

            Text(
                "SETTINGS SCREEN",
                color = scheme.onSurface
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    onLogout()          // ← Navigation zuerst
                    viewModel.logout()  // ← Danach Logout
                }
            ) {
                Text("Logout")
            }
        }
    }
}

