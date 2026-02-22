package com.example.jeffenger.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.jeffenger.ui.theme.UrbanistText
import com.example.jeffenger.utils.debugging.LogComposable

@Composable
fun AppTextField(
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null
) {
    LogComposable("AppTextField") {
        val scheme = MaterialTheme.colorScheme

        Box(
            modifier = modifier
                .fillMaxWidth()
                .heightIn(min = 42.dp, max = 120.dp)
//                .height(42.dp)
//                .background(
//                    color = scheme.tertiaryContainer,
//                    shape = RoundedCornerShape(10.dp)
//                )
//                .padding(horizontal = 12.dp),
                .background(
                    color = scheme.onSurface,
                    shape = RoundedCornerShape(25.dp)
//                    shape = RoundedCornerShape(100.dp)
                )
                .padding(horizontal = 15.dp, vertical = 10.dp),
//                .padding(horizontal = 15.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = scheme.outline,
                        modifier = Modifier.size(18.dp)
                    )
                }

                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = singleLine,
                    maxLines = 5,
                    enabled = enabled,
//                    textStyle = UrbanistText.Placeholder.copy(
//                        color = scheme.onSurface
//                    ),
                    textStyle = UrbanistText.BodyRegular.copy(
                        color = scheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { innerTextField ->
                        Box {
                            // PLACHOLDER
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

