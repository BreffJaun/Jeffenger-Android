package com.example.jeffenger.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.jeffenger.ui.theme.AppTheme
import com.example.jeffenger.ui.viewmodels.ChatViewModel
import com.example.jeffenger.utils.debugging.LogComposable
import org.koin.androidx.compose.koinViewModel

@Composable
fun ChatScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = koinViewModel (),
) {
    LogComposable("ChatScreen") {
        val scheme = MaterialTheme.colorScheme
        val chat = viewModel.chat.collectAsState(null)
        val messages = viewModel.messages.collectAsState(emptyList())

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                "CHAT SCREEN",
                color = scheme.onSurface
            )
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
private fun ChatScreenPreview() {
    AppTheme {
//        ChatScreen()
    }
}

