package com.example.jeffenger.ui.settings

import android.R.attr.bottom
import android.R.attr.top
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AppSettingsAlt
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.jeffenger.ui.auth.AuthTextField
import com.example.jeffenger.ui.chats.ChatStartButton
import com.example.jeffenger.ui.core.avatar.ProfileAvatar
import com.example.jeffenger.ui.theme.UrbanistText
import com.example.jeffenger.ui.viewmodels.SettingsViewModel
import com.example.jeffenger.utils.debugging.LogComposable
import com.example.jeffenger.utils.helper.openAppSettings
import com.example.jeffenger.utils.helper.openNotificationSettings
import org.koin.androidx.compose.koinViewModel
import org.koin.core.context.stopKoin

@Composable
fun SettingsScreen(
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel = koinViewModel(),
) {
    LogComposable("SettingsScreen") {

        val scheme = MaterialTheme.colorScheme
        val context = LocalContext.current

        val user by settingsViewModel.user.collectAsState()
        val displayName by settingsViewModel.displayName.collectAsState()
        val email by settingsViewModel.email.collectAsState()
        val company by settingsViewModel.company.collectAsState()
        val hasChanges by settingsViewModel.hasChanges.collectAsState()
        val tempAvatarUri by settingsViewModel.tempAvatarUri.collectAsState()

        var showCompanyWarningDialog by remember { mutableStateOf(false) }
        var showPasswordSheet by remember { mutableStateOf(false) }
        var showEmailSheet by remember { mutableStateOf(false) }
        val scrollState = rememberScrollState()

        val photoPickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia()
        ) { uri ->

            uri?.let {
                settingsViewModel.uploadAvatar(it)
            }

        }

        // Snackbar events
        LaunchedEffect(Unit) {
            settingsViewModel.uiEvents.collect { msg ->
                snackbarHostState.showSnackbar(msg)
            }
        }

        val companyChanged =
            user != null && company != user!!.company

        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 25.dp)
                .padding(top = 24.dp),
//            verticalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title
                Text(
                    text = "Dein Profil",
                    style = MaterialTheme.typography.displaySmall,
                    color = scheme.onSurface
                )

                // Avatar
                ProfileAvatar(
                    displayName = displayName,
                    imageUrl = user?.avatarUrl,
                    imageUri = tempAvatarUri,
                    modifier = Modifier.padding(top = 6.dp),
                    size = 110.dp
                )

                // Upload / Delete button row
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {

                    val hasAvatar = !user?.avatarUrl.isNullOrBlank()

                    ChatStartButton(
                        text = if (hasAvatar) "löschen" else "hochladen",
                        iconVector = if (hasAvatar)
                            Icons.Outlined.DeleteOutline
                        else
                            Icons.Outlined.CloudUpload,
                        outlined = true,
                        onClick = {
                            if (hasAvatar) {
                                settingsViewModel.deleteAvatar()
                            } else {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            }
                        },
                        modifier = Modifier
                            .width(185.dp)
                    )

                    Text(
                        text = "Das Profilbild ist optional",
                        style = UrbanistText.Label,
                        color = scheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            SettingsGroup(title = "") {

                AuthTextField(
                    value = displayName,
                    onValueChange = settingsViewModel::onDisplayNameChange,
                    label = "Name",
                    placeholder = "Dein Name",
                    isValid = displayName.isNotBlank()
                )

                AuthTextField(
                    value = company,
                    onValueChange = settingsViewModel::onCompanyChange,
                    label = "Firma",
                    placeholder = "Deine Firma",
                    isValid = company.isNotBlank()
                )

                if (companyChanged) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Änderung des Firmennamens führt zum Verlust des Zugriffs auf bereits bestehende Chats.",
                        style = UrbanistText.Label,
                        color = scheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                    )
                }

                SettingsEmailField(
                    label = "E-Mail-Adresse",
                    email = email,
                    onClick = {
                        showEmailSheet = true
                    }
                )

                SettingsPasswordField(
                    label = "Passwort",
                    onClick = {
                        showPasswordSheet = true
                    }
                )

                // Animated Save Button (only when hasChanges)
                AnimatedVisibility(
                    visible = hasChanges,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
                ) {
                    Button(
                        onClick = {
                            if (companyChanged) {
                                showCompanyWarningDialog = true
                            } else {
                                settingsViewModel.saveProfile()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = scheme.primary,
                            contentColor = scheme.onPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp)
                    ) {
                        Text(
                            text = "Änderungen speichern",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }

            // Divider + Settings Groups (Slack/Discord vibe)
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 16.dp),
                color = scheme.surfaceVariant
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Einstellungen",
                    style = MaterialTheme.typography.displaySmall,
                    color = scheme.onSurface,
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                )

                SettingsRow(
                    icon = Icons.Outlined.AppSettingsAlt,
                    title = "App-Einstellungen",
                    subtitle = "Berechtigungen, Speicher, Akku…",
                    onClick = { openAppSettings(context) }
                )

//                Spacer(modifier = Modifier.height(10.dp))

                SettingsRow(
                    icon = Icons.Outlined.Notifications,
                    title = "Benachrichtigungen",
                    subtitle = "System-Notification-Einstellungen",
                    onClick = { openNotificationSettings(context) }
                )
            }

            Spacer(modifier = Modifier.height(25.dp))

            LogoutButton(
                onClick = onLogout,
                modifier = Modifier
            )
        }

        // AlertDialog on company change save
        if (showCompanyWarningDialog) {
            AlertDialog(
                onDismissRequest = { showCompanyWarningDialog = false },
                title = { Text("Firma ändern?") },
                text = {
                    Text(
                        "Wenn du deine Firma änderst, kann das dazu führen, dass du auf bestehende Chats keinen Zugriff mehr hast. Möchtest du fortfahren?"
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showCompanyWarningDialog = false
                            settingsViewModel.saveProfile()
                        }
                    ) {
                        Text("Trotzdem speichern")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showCompanyWarningDialog = false }
                    ) {
                        Text("Abbrechen")
                    }
                }
            )
        }

        if (showPasswordSheet) {
            ChangePasswordSheet(
                onDismiss = { showPasswordSheet = false },
                onChangePassword = { current, new ->
                    settingsViewModel.changePassword(
                        current,
                        new
                    )
                }
            )
        }

        if (showEmailSheet) {
            ChangeEmailSheet(
                currentEmail = email,
                onDismiss = { showEmailSheet = false },
                onChangeEmail = { current, new ->
                    settingsViewModel.changeEmail(
                        current,
                        new
                    )
                }
            )
        }
    }
}
