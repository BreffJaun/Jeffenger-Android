package com.example.jeffenger.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.koinViewModel
import com.example.jeffenger.data.remote.model.Chat
import com.example.jeffenger.ui.components.ChatListItem
import com.example.jeffenger.ui.theme.AppTheme
import com.example.jeffenger.ui.viewmodels.ChatsViewModel
import com.example.jeffenger.utils.debugging.LogComposable

@Composable
fun ChatsScreen(
    viewModel: ChatsViewModel = koinViewModel (),
    onNavigateToDetail: (String) -> Unit
) {
    LogComposable("ChatsScreen") {
        val scheme = MaterialTheme.colorScheme
        val items = viewModel.chatListItems.collectAsState(emptyList())

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = items.value,
                key = { it.chatId }
            ) { item ->
                ChatListItem(
                    item = item,
                    onClick = { onNavigateToDetail(item.chatId) }
                )
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
private fun ChatsScreenPreview() {
    AppTheme {
//        ChatsScreen()
    }
}