package com.example.jeffenger.navigation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BubbleChart
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.ChatBubble
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.QrCode
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.jeffenger.navigation.helper.CalendarRoute
import com.example.jeffenger.navigation.helper.ChatsRoute
import com.example.jeffenger.navigation.helper.SettingsRoute
import kotlinx.serialization.Serializable

/**
 * Represents a selectable tab in the application's Bottom Navigation Bar.
 *
 * Each [TabItem] defines:
 * - the navigation route it points to
 * - a user-facing label
 * - an icon displayed in the BottomBar
 *
 * This enum is used by [BottomBar] to dynamically render navigation items
 * and determine the currently selected tab based on the active route.
 *
 * @property route Navigation destination associated with this tab.
 * @property label Textual label for accessibility and semantics.
 * @property icon Icon shown in the Bottom Navigation Bar.
 */
@Serializable
enum class TabItem(
    val route: Any,
    val label: String,
    val icon: ImageVector,

    ) {
    CHATS(ChatsRoute, "Chats", Icons.Rounded.ChatBubble),
    CALENDAR(CalendarRoute, "Calendar", Icons.Rounded.CalendarMonth),
    SETTINGS(SettingsRoute, "Settings", Icons.Rounded.Settings)
}
