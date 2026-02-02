package com.example.jeffenger.navigation.screens

import android.content.res.Configuration
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jeffenger.navigation.components.BottomTabItem
import com.example.jeffenger.navigation.components.GlassBottomBar
import com.example.jeffenger.navigation.components.TabItem
import com.example.jeffenger.ui.theme.AppTheme
import com.example.jeffenger.utils.debugging.LogComposable


@Composable
fun IosStyleBottomBar(
    currentRoute: String?,
    onTabSelected: (TabItem) -> Unit,
    isDarkMode: Boolean
) {
    LogComposable("IosStyleBottomBar") {


        val tabs = TabItem.values()

        fun isSelected(tab: TabItem): Boolean =
            currentRoute?.startsWith(tab.route::class.qualifiedName ?: "") == true

        val selectedIndex =
            tabs.indexOfFirst { isSelected(it) }.let { if (it >= 0) it else 0 }

        var tabWidth by remember { mutableStateOf(0.dp) }
        val density = LocalDensity.current

        val animatedOffset by animateDpAsState(
            targetValue = tabWidth * selectedIndex,
            animationSpec = spring(
                dampingRatio = 0.82f,
                stiffness = Spring.StiffnessMediumLow
            ),
            label = "pillOffset"
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(WindowInsets.navigationBars.asPaddingValues()),
            contentAlignment = Alignment.BottomCenter
        ) {
            GlassBottomBar(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(68.dp) // 🔑 fix Bar-hight like iOS
                ) {

                    // iOS Active Pill
                    Box(
                        modifier = Modifier
                            .offset(x = animatedOffset)
                            .padding(horizontal = 4.dp, vertical = 4.dp)
                            .width(tabWidth - 8.dp)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(999.dp))
                            .background(
                                MaterialTheme.colorScheme.onSurface
                                    .copy(alpha = if (isDarkMode) 0.18f else 0.12f)
                            )
//                        .background(
//                            MaterialTheme.colorScheme.primary
//                                .copy(alpha = if (isDarkMode) 0.18f else 0.12f)
//                        )
                    )

                    // Tabs
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        tabs.forEach { tab ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .onSizeChanged { size ->
                                        if (tabWidth == 0.dp) {
                                            tabWidth = with(density) { size.width.toDp() }
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                BottomTabItem(
                                    icon = tab.icon,
                                    label = tab.label,
                                    selected = isSelected(tab),
                                    onClick = { onTabSelected(tab) }
                                )
                            }
                        }
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
private fun IosStyleBottomBarPreview() {
    AppTheme {
//        IosStyleBottomBar()
    }
}