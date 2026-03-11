package com.example.jeffenger.ui.calendar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.jeffenger.ui.core.ButtonContent
import com.example.jeffenger.utils.debugging.LogComposable

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EventActionButtons(
    isCreate: Boolean,
    isEditable: Boolean,
    enabled: Boolean,
    onClose: () -> Unit,
    onSave: () -> Unit
) {
    LogComposable("EventActionButtons") {
        val scheme = MaterialTheme.colorScheme
        val useWeight = isEditable

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = if (useWeight) {
                Arrangement.spacedBy(12.dp)
            } else {
                Arrangement.Center
            },
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            OutlinedButton(
                onClick = onClose,
                modifier = if (useWeight) {
                    Modifier
                        .height(42.dp)
                        .weight(1f, fill = true)
                } else {
                    Modifier.height(42.dp)
                },
                border = BorderStroke(2.dp, scheme.onSurfaceVariant),
                contentPadding = if (useWeight) {
                    PaddingValues(horizontal = 8.dp)
                } else {
                    PaddingValues(horizontal = 14.dp)
                }
            ) {
                Box(
                    modifier = if (useWeight) Modifier.fillMaxWidth() else Modifier,
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = null,
                            tint = scheme.onSurfaceVariant
                        )

                        Spacer(Modifier.width(10.dp))

                        Text(
                            text = "Schließen",
                            color = scheme.onSurfaceVariant,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }

            if (isEditable) {
                Button(
                    onClick = onSave,
                    modifier = Modifier
                        .height(42.dp)
                        .weight(1f, fill = true),
                    enabled = enabled,
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Save,
                                contentDescription = null,
                                tint = scheme.surface
                            )

                            Spacer(Modifier.width(10.dp))

                            Text(
                                text = if (isCreate) "Erstellen" else "Speichern",
                                color = scheme.surface,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
