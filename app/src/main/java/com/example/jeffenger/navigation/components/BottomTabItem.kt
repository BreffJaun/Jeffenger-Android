package com.example.jeffenger.navigation.components

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jeffenger.ui.theme.AppTheme
import com.example.jeffenger.utils.debugging.LogComposable

@Composable
fun BottomTabItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    LogComposable("BottomTabItem") {
        val scheme = MaterialTheme.colorScheme
        val color by animateColorAsState(
            if (selected)
                scheme.primary
            else
                scheme.onSurface.copy(alpha = 0.6f),
            label = "tabColor"
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier

                .clip(RoundedCornerShape(16.dp))
                .clickable(onClick = onClick)
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Icon(icon, contentDescription = label, tint = color)
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = color
            )
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
private fun BottomTabItemPreview() {
    AppTheme {
//        BottomTabItem()
    }
}

