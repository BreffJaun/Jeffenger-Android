package com.example.jeffenger.navigation.screens

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.jeffenger.navigation.components.TabItem
import com.example.jeffenger.utils.debugging.LogComposable


/**
 * Bottom navigation bar of the app.
 *
 * Displays all available [TabItem] entries as navigation tabs and highlights
 * the currently active one based on the current navigation route.
 *
 * This composable itself is stateless. Selection state is fully derived
 * from [currentRoute] and handled by the parent (e.g. AppStart / NavHost).
 *
 * @param currentRoute The currently active navigation route as provided by NavController.
 * Used to determine which tab is selected.
 * @param onTabSelected Callback invoked when a tab is selected.
 * The selected [TabItem] is passed to the caller for navigation handling.
 */
@Composable
fun BottomBar(
    currentRoute: String?,
    onTabSelected: (TabItem) -> Unit
) {
    LogComposable("BottomBar") {
        NavigationBar(
            containerColor = Color(0xFF2A0E14).copy(alpha = 0.35f),
            tonalElevation = 0.dp
        ) {

            TabItem.values().forEach { tab ->
                val selected = currentRoute?.contains(tab.route::class.simpleName ?: "") == true

                NavigationBarItem(
                    selected = selected,
                    onClick = { onTabSelected(tab) },
                    icon = { Icon(tab.icon, contentDescription = tab.label) },
                    label = { Text(tab.label) },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.White.copy(alpha = 0.22f),
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        unselectedIconColor = Color.White.copy(alpha = 0.6f),
                        unselectedTextColor = Color.White.copy(alpha = 0.6f)
                    )
                )
            }
        }
    }
}