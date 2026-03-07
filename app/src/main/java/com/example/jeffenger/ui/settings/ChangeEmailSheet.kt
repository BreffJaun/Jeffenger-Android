package com.example.jeffenger.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.jeffenger.ui.auth.AuthTextField
import com.example.jeffenger.ui.theme.UrbanistText
import de.syntax_institut.jetpack.a04_05_online_shopper.utilities.BackgroundWrapper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeEmailSheet(
    currentEmail: String,
    onDismiss: () -> Unit,
    onChangeEmail: (String, String) -> Unit
) {

    val scheme = MaterialTheme.colorScheme
    val sheetState = rememberModalBottomSheetState()

    var currentPassword by remember { mutableStateOf("") }
    var newEmail by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null
    ) {

        BackgroundWrapper(isSheet = true) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {

                // Drag Handle
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(36.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(50))
                            .background(scheme.onSurface)
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    Text(
                        text = "E-Mail ändern",
                        style = MaterialTheme.typography.titleLarge,
                        color = scheme.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp),
                    )

                    Text(
                        text = "Aktuelle E-Mail",
                        style = MaterialTheme.typography.labelMedium,
                        color = scheme.onSurfaceVariant
                    )

                    Text(
                        text = currentEmail,
                        style = UrbanistText.BodyRegular,
                        color = scheme.onSurface
                    )


                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        color = scheme.surfaceVariant
                    )


                    AuthTextField(
                        value = newEmail,
                        onValueChange = { newEmail = it },
                        label = "Neue E-Mail",
                        placeholder = "Neue E-Mail eingeben"
                    )

                    AuthTextField(
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        label = "Aktuelles Passwort",
                        placeholder = "Passwort bestätigen",
                        isPassword = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    ChangeActionButton(
                        text = "E-Mail ändern",
                        icon = Icons.Outlined.AlternateEmail,
                        onClick = {

                            if (newEmail.isBlank() || currentPassword.isBlank())
                                return@ChangeActionButton

                            onChangeEmail(
                                currentPassword,
                                newEmail
                            )

                            currentPassword = ""
                            newEmail = ""

                            onDismiss()
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}