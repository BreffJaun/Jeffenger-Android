package com.example.jeffenger.navigation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.outlined.PersonOutline
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
    CHATS(ChatsRoute, "Chats", Icons.AutoMirrored.Outlined.Chat),
    //    CALENDAR(CalendarRoute, "Calendar", Icons.Outlined.CalendarToday),
    CALENDAR(CalendarRoute, "Termine", Icons.Outlined.CalendarToday),
    SETTINGS(SettingsRoute, "Profil", Icons.Outlined.PersonOutline)
}
