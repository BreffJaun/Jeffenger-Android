package de.syntax_institut.projektwoche1.ui.component


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.jeffenger.R
import com.example.jeffenger.navigation.helper.CalendarRoute
import com.example.jeffenger.navigation.helper.ChatRoute
import com.example.jeffenger.navigation.helper.ChatsRoute
import com.example.jeffenger.navigation.helper.SettingsRoute
import com.example.jeffenger.ui.components.GlassIconButton
import com.example.jeffenger.utils.debugging.LogComposable


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
 * @param isDarkMode Current theme state (not directly used here)
 * @param onBackClick Callback invoked when the back button is pressed
 * @param modifier Optional modifier for layout adjustments
 */
@Composable
fun TopBar(
    currentRoute: String?,
//    isDarkMode: Boolean,
    onBackClick: () -> Unit,
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

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 60.dp)
                .padding(horizontal = 25.dp)
                .padding(bottom = 10.dp)
        ) {

            // LEFT SIDE
            Image(
                painter = painterResource(R.drawable.jeffenger_font),
                contentDescription = "Jeffenger Logo",
                contentScale = ContentScale.FillHeight,
                modifier = Modifier.height(20.dp)
            )

            // CENTER
            // ...


            // RIGHT SIDE
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

                        GlassIconButton(onClick = { /* Calendar */ }) {
                            Icon(
                                imageVector = Icons.Outlined.CalendarToday,
                                tint = scheme.outlineVariant,
                                contentDescription = "Calendar"
                            )
                        }

                        GlassIconButton(onClick = { /* Add Chat */ }) {
                            Icon(
                                imageVector = Icons.Outlined.Add,
                                tint = scheme.outlineVariant,
                                contentDescription = "New Chat"
                            )
                        }

                        GlassIconButton(onClick = { /* More */ }) {
                            Icon(
                                imageVector = Icons.Outlined.MoreVert,
                                tint = scheme.outlineVariant,
                                contentDescription = "More"
                            )
                        }
                    }
                }

                AnimatedVisibility(
                    visible = isChatRoute || isCalendarRoute,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    GlassIconButton(onClick = { /* Add */ }) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            tint = scheme.onSurface,
                            contentDescription = "Add"
                        )
                    }
                }

                AnimatedVisibility(
                    visible = isSettingsRoute,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(modifier = Modifier.width(1.dp).height(48.dp))
                }
            }

        }
    }
}

