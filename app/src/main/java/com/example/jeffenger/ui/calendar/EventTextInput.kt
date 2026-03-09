package com.example.jeffenger.ui.calendar

import android.R.attr.bottom
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.jeffenger.ui.theme.UrbanistText
import com.example.jeffenger.utils.debugging.LogComposable

@Composable
fun EventTextInput(
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    required: Boolean = false,
    singleLine: Boolean = true,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    leadingIcon: ImageVector? = null,
    minHeight: Dp = 44.dp
) {
    LogComposable("EventTextInput") {

        val scheme = MaterialTheme.colorScheme
        val interactionSource = remember { MutableInteractionSource() }
        val isFocused by interactionSource.collectIsFocusedAsState()

        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
        ) {

            // LABEL + OPTIONAL REQUIRED STAR
            if (label != null) {

                Row(
//                    verticalAlignment = Alignment.CenterVertically,
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {

                    Text(
                        text = label,
                        style = UrbanistText.Label,
                        color = scheme.onSurfaceVariant
                    )

                    if (required) {
                        Text(
                            text = "*",
                            style = UrbanistText.Label,
                            color = scheme.primary
                        )
                    }
                }
            }

            // INPUT FIELD
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = minHeight)
                    .background(
                        color = scheme.tertiaryContainer,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = if (isFocused && enabled && !readOnly) scheme.primary else Color.Transparent,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 10.dp),
//                contentAlignment = Alignment.CenterStart
                contentAlignment = Alignment.TopStart
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {

                    if (leadingIcon != null) {
                        Icon(
                            imageVector = leadingIcon,
                            contentDescription = null,
                            tint = scheme.outline,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        singleLine = singleLine,
                        enabled = enabled,
                        readOnly = readOnly,
                        textStyle = UrbanistText.BodyRegular.copy(
                            color = scheme.onSurface
                        ),
                        cursorBrush = SolidColor(scheme.onSurface),
                        interactionSource = interactionSource,
                        modifier = Modifier.weight(1f),
                        decorationBox = { innerTextField ->
                            Box {
                                if (value.isBlank()) {
                                    Text(
                                        text = placeholder,
                                        style = UrbanistText.Placeholder,
                                        color = scheme.outline
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }
            }
        }
    }
}