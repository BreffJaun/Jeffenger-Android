package com.example.jeffenger.ui.chat

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.example.jeffenger.data.remote.model.Message
import com.example.jeffenger.ui.theme.UrbanistText
import com.example.jeffenger.utils.extensions.relativeTimeString

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageBubble(
    message: Message,
    isMine: Boolean,
    senderName: String? = null,
    onLongPress: () -> Unit
) {

    val scheme = MaterialTheme.colorScheme

    val time = message.createdAt.relativeTimeString()

    val bubbleColor =
        if (isMine) scheme.secondary
        else scheme.onSurfaceVariant

    val textColor = scheme.surface

    val bubbleShape =
        if (isMine) {
            RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp,
                bottomStart = 20.dp,
                bottomEnd = 0.dp
            )
        } else {
            RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp,
                bottomStart = 0.dp,
                bottomEnd = 20.dp
            )
        }

    val haptic = LocalHapticFeedback.current

    Column(
        horizontalAlignment =
            if (isMine) Alignment.End else Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {},
                onLongClick = {
                    if (isMine) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onLongPress()
                    }
                }
            )
    ) {

        Box(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .background(
                    color = bubbleColor,
                    shape = bubbleShape
                )
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {

            Column {

                // HEADER IN BUBBLE
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = time,
                        style = UrbanistText.Label,
                        color = scheme.surface.copy(alpha = 0.8f)
                    )

                    if (!isMine) {
                        Text(
                            text = "-",
                            style = UrbanistText.Label,
                            color = scheme.surface.copy(alpha = 0.8f)
                        )

                        Text(
                            text = senderName ?: message.senderId,
                            style = UrbanistText.Label,
                            color = scheme.surface.copy(alpha = 0.8f)
                        )
                    }

                    if (message.editedAt != null) {
                        Text(
                            text = "-",
                            style = UrbanistText.Label,
                            color = scheme.surface.copy(alpha = 0.6f)
                        )

                        Text(
                            text = "bearbeitet",
                            style = UrbanistText.Label,
                            color = scheme.surface.copy(alpha = 0.6f)
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                // MESSAGE TEXT
                Text(
                    text = message.text ?: "",
                    style = UrbanistText.BodyRegular,
                    color = textColor
                )
            }
        }
    }
}