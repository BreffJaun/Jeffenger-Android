package com.example.jeffenger.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.PersonOutline
import com.example.jeffenger.R
import com.example.jeffenger.utils.debugging.LogComposable
import com.example.jeffenger.utils.model.StartChatUiState

@Composable
fun StartChatSection(
    state: StartChatUiState,
    onDirectJeffClick: () -> Unit,
    onCompanyClick: () -> Unit,
    onCompanyWithJeffClick: () -> Unit
) {
    LogComposable("StartChatSection") {
        val scheme = MaterialTheme.colorScheme

        val showAnyButton =
            state.showDirectJeff ||
                    state.showCompany ||
                    state.showCompanyWithJeff

        if (!showAnyButton) return@LogComposable

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Neuer Chat",
                style = MaterialTheme.typography.displaySmall,
                color = scheme.onSurface,
                textAlign = TextAlign.Center
            )

            if (state.showDirectJeff) {
                ChatStartButton(
                    text = "Jeff",
                    iconVector = Icons.Rounded.PersonOutline,
                    onClick = onDirectJeffClick
                )
            }

            if (state.showCompany) {
                ChatStartButton(
                    text = "Company",
                    iconPainter = painterResource(id = R.drawable.ic_family_group),
                    onClick = onCompanyClick,
                    outlined = true
                )
            }

            if (state.showCompanyWithJeff) {
                ChatStartButton(
                    text = "Company & Jeff",
                    iconVector = Icons.Outlined.Groups,
                    onClick = onCompanyWithJeffClick,
                    outlined = true
                )
            }
        }
    }
}