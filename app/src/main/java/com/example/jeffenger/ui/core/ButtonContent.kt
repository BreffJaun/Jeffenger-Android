package com.example.jeffenger.ui.core

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.jeffenger.utils.debugging.LogComposable

@Composable
fun ButtonContent(
    text: String,
    iconVector: ImageVector? = null,
    iconPainter: Painter? = null,
    tint: Color
) {
    LogComposable("ButtonContent") {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            when {
                iconPainter != null -> {
                    Icon(
                        painter = iconPainter,
                        contentDescription = null,
                        tint = tint
                    )
                }

                iconVector != null -> {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = null,
                        tint = tint
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = text,
                color = tint,
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}