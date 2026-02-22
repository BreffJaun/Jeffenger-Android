package com.example.jeffenger.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jeffenger.data.remote.model.ui_model.ChatListItemUiModel
import com.example.jeffenger.ui.theme.AppTheme
import com.example.jeffenger.ui.theme.UrbanistText
import com.example.jeffenger.utils.extensions.relativeTimeString
import com.example.jeffenger.utils.debugging.LogComposable

@Composable
fun ChatListItem(
    item: ChatListItemUiModel,
    onClick: () -> Unit
) {
    LogComposable("ChatListItem") {
        val scheme = MaterialTheme.colorScheme

        val unreadCount = item.unreadCount
        val unreadText = when {
            unreadCount > 99 -> "99+"
            else -> unreadCount.toString()
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(bottom = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Avatar
            AvatarCircle(avatar = item.avatar)

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                verticalArrangement = Arrangement.Center
            ) {

                // NAME + TIME
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(25.dp)
                ) {
                    Text(
                        text = item.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        color = scheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = item.lastMessageTimestamp.relativeTimeString(),
                        style = UrbanistText.Label,
                        color = if (unreadCount > 0) scheme.primary else scheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // LAST MESSAGE + UNREAD MESSAGS BADGE
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.lastMessageText ?: "",
                        style = UrbanistText.BodyRegular,
                        color = scheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    // ✅ Platz rechts immer reservieren
                    Box(
                        modifier = Modifier
                            .width(44.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        if (unreadCount > 0) {
                            Box(
                                modifier = Modifier
                                    .height(20.dp)
                                    .widthIn(min = 20.dp)
                                    .clip(CircleShape)
                                    .background(scheme.primary)
                                    .padding(horizontal = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = unreadText,
                                    color = scheme.onPrimary,
                                    style = UrbanistText.Label,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.spacedBy(25.dp)
//                ) {
//                    Text(
//                        text = item.lastMessageText ?: "",
//                        style = UrbanistText.BodyRegular,
//                        color = scheme.onSurfaceVariant,
//                        maxLines = 1,
//                        overflow = TextOverflow.Ellipsis,
//                        modifier = Modifier.weight(1f)
//                    )
//
//                    if (unreadCount > 0) {
//                        Box(
//                            modifier = Modifier
//                                .height(20.dp)
//                                .widthIn(min = 20.dp)
//                                .clip(CircleShape)
//                                .background(scheme.primary)
//                                .padding(horizontal = 6.dp),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Text(
//                                text = unreadText,
//                                color = scheme.onPrimary,
//                                style = UrbanistText.Label,
//                                maxLines = 1
//                            )
//                        }
//                    }
//                }
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
private fun ChatListItemPreview() {
    AppTheme {
//        ChatListItem()
    }
}

