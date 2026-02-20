package com.example.jeffenger.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.jeffenger.utils.debugging.LogComposable

@Composable
fun ChatStartButton(
    text: String,
    iconVector: ImageVector? = null,
    iconPainter: Painter? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    outlined: Boolean = false,
    enabled: Boolean = true
) {
    LogComposable("ChatStartButton") {
        val scheme = MaterialTheme.colorScheme

        val buttonModifier = modifier
            .height(42.dp)
            .width(236.dp)

        if (outlined) {
            OutlinedButton(
                onClick = onClick,
                modifier = buttonModifier,
                enabled = enabled,
                border = BorderStroke(2.dp, scheme.primary),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = scheme.primary
                )
            ) {
                ButtonContent(
                    text = text,
                    iconVector = iconVector,
                    iconPainter = iconPainter,
                    tint = scheme.primary
                )
            }
        } else {
            Button(
                onClick = onClick,
                modifier = buttonModifier,
                enabled = enabled,
            ) {
                ButtonContent(
                    text = text,
                    iconVector = iconVector,
                    iconPainter = iconPainter,
                    tint = scheme.surface
                )
            }
        }
    }
}