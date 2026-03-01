package com.example.jeffenger.ui.chats

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import com.example.jeffenger.ui.theme.AppTheme
import com.example.jeffenger.ui.viewmodels.ChatsViewModel
import com.example.jeffenger.utils.debugging.LogComposable
import com.example.jeffenger.utils.enums.NewChatSheetMode
import kotlinx.coroutines.flow.SharedFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatsScreen(
    openNewChatSheetFlow: SharedFlow<Unit>,
    snackbarHostState: SnackbarHostState,
    viewModel: ChatsViewModel = koinViewModel(),
    onNavigateToDetail: (String, String) -> Unit
) {
    LogComposable("ChatsScreen") {
        val scheme = MaterialTheme.colorScheme

        val startState by viewModel.startChatUiState.collectAsState()
        val chatItems by viewModel.chatListItems.collectAsState()

        var sheetMode by remember { mutableStateOf<NewChatSheetMode?>(null) }
        val modalSheetState = rememberModalBottomSheetState()

        var searchQuery by remember { mutableStateOf("") }

        Log.d("DEBUG", "chatItems size = ${chatItems.size}")
        Log.d("DEBUG", "StartState = $startState")

        LaunchedEffect(Unit) {
            viewModel.navigateToChat.collect { (chatId, companyId) ->
                onNavigateToDetail(chatId, companyId)
            }
        }

        LaunchedEffect(Unit) {
            viewModel.uiEvents.collect { message ->
                snackbarHostState.showSnackbar(message)
            }
        }

        LaunchedEffect(openNewChatSheetFlow) {
            openNewChatSheetFlow.collect {
                viewModel.resetSelection()
                sheetMode = NewChatSheetMode.GENERAL
            }
        }

        if (chatItems.isEmpty()) {

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 25.dp),
            ) {

                StartChatSection(
                    state = startState,
                    onDirectJeffClick = { viewModel.startDirectJeffChat() },

                    onCompanyClick = {
                        viewModel.resetSelection()
                        sheetMode = NewChatSheetMode.COMPANY
                    },

                    onCompanyWithJeffClick = {
                        viewModel.prepareCompanyWithJeffSelection()
                        sheetMode = NewChatSheetMode.COMPANY_WITH_JEFF
                    }
                )
            }

        } else {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 25.dp)
            ) {

                ChatSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    modifier = Modifier
                        .padding(bottom = 40.dp)
                )

                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(
                        chatItems,
                        key = { it.chatId }
                    ) { item ->
                        ChatListItem(
                            item = item,
                            onClick = {
                                item.companyId?.let { cid ->
                                    onNavigateToDetail(item.chatId, cid)
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    StartChatSection(
                        state = startState,
                        onDirectJeffClick = { viewModel.startDirectJeffChat() },

                        onCompanyClick = {
//                            viewModel.resetSelection()
                            viewModel.prepareCompanySelection()
                            sheetMode = NewChatSheetMode.COMPANY
                        },

                        onCompanyWithJeffClick = {
                            viewModel.prepareCompanyWithJeffSelection()
                            sheetMode = NewChatSheetMode.COMPANY_WITH_JEFF
                        }
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }

        if (sheetMode != null) {
            NewChatBottomSheet(
                mode = sheetMode!!,
                viewModel = viewModel,
                onClose = {
                    viewModel.resetSelection()
                    sheetMode = null
                }
            )
        }
    }
}
