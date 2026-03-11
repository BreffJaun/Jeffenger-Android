package com.example.jeffenger.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.jeffenger.data.remote.model.ui_model.AvatarUiModel
import com.example.jeffenger.ui.core.avatar.AvatarCircle
import com.example.jeffenger.ui.core.avatar.ProfileAvatar
import com.example.jeffenger.ui.theme.UrbanistText
import com.example.jeffenger.utils.model.ChatTopBarUiState


@Composable
fun ChatInfoDialog(
    state: ChatTopBarUiState,
    participants: List<Pair<AvatarUiModel, String>>,
    onDismiss: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme

    Dialog(onDismissRequest = onDismiss) {

        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(28.dp))
                .background(scheme.surface)
                .padding(24.dp)
        ) {

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                // TITLE
                Text(
                    text = "Gruppeninfo",
                    style = MaterialTheme.typography.titleLarge,
                    color = scheme.onSurface
                )

                Spacer(Modifier.height(24.dp))

                // ANZEIGEBILD
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    SectionLabel("Anzeigebild")
                }

                Spacer(Modifier.height(12.dp))

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    AvatarCircle(
                        avatar = state.avatar,
                        boxSize = 110.dp,
//                        iconSize = 110.dp
                    )
                }

                Spacer(Modifier.height(24.dp))

                HorizontalDivider(color = scheme.outlineVariant)

                Spacer(Modifier.height(20.dp))

                // GRUPPENNAME
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    SectionLabel("Gruppenname")
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = state.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = scheme.onSurface
                )

                Spacer(Modifier.height(24.dp))

                HorizontalDivider(color = scheme.outlineVariant)

                Spacer(Modifier.height(20.dp))

                // MEMBERS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    SectionLabel("Mitglieder")
                }

                Spacer(Modifier.height(12.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.heightIn(max = 260.dp)
                ) {
                    items(participants) { (avatar, name) ->

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            AvatarCircle(
                                avatar = avatar
                            )

                            Spacer(Modifier.width(14.dp))

                            Text(
                                text = name,
                                style = MaterialTheme.typography.bodyLarge,
                                color = scheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}