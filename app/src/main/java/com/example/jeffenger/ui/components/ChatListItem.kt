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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(bottom = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Avatar
            AvatarCircle(
                avatar = item.avatar
            )

            Spacer(Modifier.width(12.dp))

            // NAME + LAST MESSAGE
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp) // Figma spacing
            ) {
                Text(
                    text = item.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )

                Text(
                    text = item.lastMessageText ?: "",
                    style = UrbanistText.BodyRegular,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // TIME + UNREAD MESSAGS
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .height(48.dp)
            ) {
                Text(
                    text = item.lastMessageTimestamp.relativeTimeString(),
                    style = UrbanistText.Label,
                    color = if (item.unreadCount > 0)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.weight(1f))

                if (item.unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.unreadCount.toString(),
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = UrbanistText.Label
                        )
                    }
                }
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