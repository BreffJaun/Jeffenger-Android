package com.example.jeffenger.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.jeffenger.data.remote.model.Message
import com.example.jeffenger.ui.theme.UrbanistText
import com.example.jeffenger.utils.debugging.LogComposable
import com.example.jeffenger.utils.extensions.relativeTimeString

@Composable
fun MessageBubble(
    message: Message,
    isMine: Boolean,
    senderName: String? = null // 👈 neu
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
                bottomEnd = 4.dp
            )
        } else {
            RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp,
                bottomStart = 4.dp,
                bottomEnd = 20.dp
            )
        }

    Column(
        horizontalAlignment =
            if (isMine) Alignment.End else Alignment.Start,
        modifier = Modifier.fillMaxWidth()
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

                // 🕒 HEADER IN BUBBLE
                Text(
                    text =
                        if (isMine)
                            time
                        else
                            "$time - ${senderName ?: message.senderId}",
                    style = UrbanistText.Label,
                    color = scheme.surface.copy(alpha = 0.8f)
                )

                Spacer(Modifier.height(4.dp))

                // 💬 MESSAGE TEXT
                Text(
                    text = message.text ?: "",
                    style = UrbanistText.BodyRegular,
                    color = textColor
                )
            }
        }
    }
}

//@Composable
//fun MessageBubble(
//    message: Message,
//    isMine: Boolean,
//    senderName: String? = null
//) {
//
//    val scheme = MaterialTheme.colorScheme
//
//    val time = message.createdAt.relativeTimeString()
//
//    val bubbleColor =
//        if (isMine) scheme.secondary
//        else scheme.onSurfaceVariant
//
//    val textColor = scheme.surface
//
//    val bubbleShape =
//        if (isMine) {
//            RoundedCornerShape(
//                topStart = 20.dp,
//                topEnd = 20.dp,
//                bottomStart = 20.dp,
//                bottomEnd = 4.dp
//            )
//        } else {
//            RoundedCornerShape(
//                topStart = 20.dp,
//                topEnd = 20.dp,
//                bottomStart = 4.dp,
//                bottomEnd = 20.dp
//            )
//        }
//
//    Column(
//        horizontalAlignment =
//            if (isMine) Alignment.End else Alignment.Start,
//        modifier = Modifier.fillMaxWidth()
//    ) {
//
//        // 🕒 TIME + NAME
//        Row(
//            horizontalArrangement =
//                if (isMine) Arrangement.End else Arrangement.Start,
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text(
//                text =
//                    if (isMine)
//                        time
//                    else
//                        "$time - ${message.senderId}",
//                style = UrbanistText.BodyRegular,
//                color = scheme.onSurfaceVariant
//            )
//        }
//
//        Spacer(Modifier.height(4.dp))
//
//        // 💬 BUBBLE
//        Box(
//            modifier = Modifier
//                .widthIn(max = 280.dp)
//                .background(
//                    color = bubbleColor,
//                    shape = bubbleShape
//                )
//                .padding(horizontal = 10.dp, vertical = 10.dp)
//        ) {
//            Text(
//                text = message.text ?: "",
//                style = UrbanistText.BodyRegular,
//                color = textColor,
//                overflow = TextOverflow.Visible
//            )
//        }
//    }
//}

//@Composable
//fun MessageBubble(
//    message: Message,
//    isMine: Boolean
//) {
//    LogComposable("MessageBubble") {
//        val scheme = MaterialTheme.colorScheme
//
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
//        ) {
//            Box(
//                modifier = Modifier
//                    .widthIn(max = 280.dp)
//                    .clip(RoundedCornerShape(16.dp))
//                    .background(if (isMine) scheme.primaryContainer else scheme.surfaceVariant)
//                    .padding(12.dp)
//            ) {
//                Text(
//                    text = message.text ?: "",
//                    color = scheme.onSurface
//                )
//            }
//        }
//    }
//}