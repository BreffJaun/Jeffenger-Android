package de.syntax_institut.projektwoche1.ui.component


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.jeffenger.R
import com.example.jeffenger.navigation.helper.CalendarRoute
import com.example.jeffenger.navigation.helper.ChatRoute
import com.example.jeffenger.navigation.helper.ChatsRoute
import com.example.jeffenger.navigation.helper.SettingsRoute
import com.example.jeffenger.ui.core.avatar.AvatarCircle
import com.example.jeffenger.ui.core.GlassIconButton
import com.example.jeffenger.utils.debugging.LogComposable
import com.example.jeffenger.utils.model.ChatTopBarUiState


/**
 * Top application bar used across the entire app.
 *
 * Responsibilities:
 * - Displays a centered app title
 * - Shows a back navigation button on non-home routes
 *
 * Behaviour:
 * - Home route:
 *   • No back button
 *
 * - Generate / Scan routes:
 *   • Back button on the left
 *
 * Notes:
 * - This composable is fully stateless
 * - Navigation logic is delegated to the parent (`AppStart`)
 *
 * @param currentRoute The currently active navigation route
 * @param onBackClick Callback invoked when the back button is pressed
 * @param modifier Optional modifier for layout adjustments
 */
@Composable
fun TopBar(
    currentRoute: String?,
    onBackClick: () -> Unit,
    onAddChatClick: () -> Unit,
    chatTopBarState: ChatTopBarUiState?,
    onChatHeaderClick: () -> Unit,
    onChatCalendarClick: () -> Unit,
    onCalendarAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LogComposable("TopBar") {
        val scheme = MaterialTheme.colorScheme

        val isChatsRoute = currentRoute?.startsWith(ChatsRoute::class.qualifiedName ?: "") == true
        val isChatRoute = currentRoute?.startsWith(ChatRoute::class.qualifiedName ?: "") == true
        val isCalendarRoute =
            currentRoute?.startsWith(CalendarRoute::class.qualifiedName ?: "") == true
        val isSettingsRoute =
            currentRoute?.startsWith(SettingsRoute::class.qualifiedName ?: "") == true

        Column(
            modifier = modifier
                .fillMaxWidth()
                .statusBarsPadding()
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 25.dp, vertical = 10.dp)
            ) {

                // LEFT
                Box(
                    modifier = Modifier.wrapContentWidth(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (isChatRoute || isCalendarRoute) {
                        GlassIconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowBackIosNew,
                                tint = scheme.onSurface,
                                contentDescription = "Back"
                            )
                        }
                    } else {
                        Image(
                            painter = painterResource(R.drawable.jeffenger_font),
                            contentDescription = "Jeffenger Logo",
                            contentScale = ContentScale.FillHeight,
                            modifier = Modifier.height(20.dp)
                        )
                    }
                }

                Spacer(Modifier.width(10.dp))

                // MIDDLE (Header) -> in ChatRoute LINKS
                Box(
                    modifier = Modifier
                        .weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {

                    this@Row.AnimatedVisibility(
                        visible = isChatRoute && chatTopBarState != null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        val state = chatTopBarState!!

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { onChatHeaderClick() }
                                .padding(end = 10.dp)
                        ) {

                            AvatarCircle(
                                avatar = state.avatar,
                                modifier = Modifier.size(44.dp)
                            )

                            Spacer(Modifier.width(10.dp))

                            Column(
                                modifier = Modifier.widthIn(max = 220.dp)
                            ) {
                                Text(
                                    text = state.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = scheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = state.subtitle,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = scheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                // RIGHT (Actions)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    AnimatedVisibility(
                        visible = isChatsRoute,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

//                            GlassIconButton(onClick = { /* Calendar */ }) {
//                                Icon(
//                                    imageVector = Icons.Outlined.CalendarToday,
//                                    tint = scheme.outlineVariant,
//                                    contentDescription = "Calendar"
//                                )
//                            }

                            GlassIconButton(onClick = onAddChatClick) {
                                Icon(
                                    imageVector = Icons.Outlined.Add,
                                    tint = scheme.outlineVariant,
                                    contentDescription = "New Chat"
                                )
                            }

//                            GlassIconButton(onClick = { /* More */ }) {
//                                Icon(
//                                    imageVector = Icons.Outlined.MoreVert,
//                                    tint = scheme.outlineVariant,
//                                    contentDescription = "More"
//                                )
//                            }
                        }
                    }

                    AnimatedVisibility(
                        visible = isChatRoute,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        GlassIconButton(onClick = onChatCalendarClick) {
                            Icon(
                                imageVector = Icons.Outlined.CalendarToday,
                                tint = scheme.onSurface,
                                contentDescription = "Chat Calendar"
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = isCalendarRoute,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        GlassIconButton(onClick = onCalendarAddClick) {
                            Icon(
                                imageVector = Icons.Outlined.Add,
                                tint = scheme.onSurface,
                                contentDescription = "New Event"
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = isSettingsRoute,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Spacer(Modifier.size(48.dp))
                    }
                }
            }

            if (isChatRoute) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(scheme.surfaceVariant)
                )
            }
        }
    }
}

