package com.example.jeffenger.ui.settings

import android.R.attr.scheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.jeffenger.ui.auth.AuthTextField
import com.example.jeffenger.ui.auth.PasswordRule
import com.example.jeffenger.ui.theme.UrbanistText
import com.example.jeffenger.ui.viewmodels.AuthViewModel
import com.example.jeffenger.utils.debugging.LogComposable
import de.syntax_institut.jetpack.a04_05_online_shopper.utilities.BackgroundWrapper
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ChangePasswordSheet(
    onDismiss: () -> Unit,
    onChangePassword: (String, String) -> Unit,
    authViewModel: AuthViewModel = koinViewModel()
) {
    LogComposable("ChangePasswordSheet") {
        val scheme = MaterialTheme.colorScheme

        val sheetState = rememberModalBottomSheetState()

        val password by authViewModel.password.collectAsState()

        var currentPassword by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }

        val hasMinLength by authViewModel.hasMinLength.collectAsState(false)
        val hasUppercase by authViewModel.hasUppercase.collectAsState(false)
        val hasLowercase by authViewModel.hasLowercase.collectAsState(false)
        val hasDigit by authViewModel.hasDigit.collectAsState(false)
        val hasSpecialChar by authViewModel.hasSpecialChar.collectAsState(false)

        val isPasswordValid by authViewModel.isPasswordValid.collectAsState(false)

        val passwordsMatch =
            password.isNotBlank() &&
                    confirmPassword.isNotBlank() &&
                    password == confirmPassword

        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            dragHandle = null
        ) {
            BackgroundWrapper(isSheet = true) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 25.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {

                        Text(
                            text = "Passwort ändern",
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        AuthTextField(
                            value = currentPassword,
                            onValueChange = { currentPassword = it },
                            label = "Aktuelles Passwort",
                            placeholder = "Aktuelles Passwort eingeben",
                            isPassword = true
                        )

                        AuthTextField(
                            value = password,
                            onValueChange = authViewModel::onPasswordChange,
                            label = "Neues Passwort",
                            placeholder = "Sicheres Passwort",
                            isPassword = true,
                            isValid = if (password.isNotEmpty()) isPasswordValid else null
                        )

                        if (password.isNotEmpty() && !isPasswordValid) {

                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {

                                PasswordRule("Mind. 6 Zeichen, ", hasMinLength)
                                PasswordRule("Groß-/ ", hasUppercase)
                                PasswordRule("Kleinbuchstabe, ", hasLowercase)
                                PasswordRule("Zahl & ", hasDigit)
                                PasswordRule("Sonderzeichen", hasSpecialChar)

                            }
                        }

                        AuthTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = "Passwort bestätigen",
                            placeholder = "Passwort erneut eingeben",
                            isPassword = true,
                            isValid = passwordsMatch
                        )

                        if (confirmPassword.isNotEmpty() && !passwordsMatch) {
                            Text(
                                text = "Passwörter stimmen nicht überein",
                                color = MaterialTheme.colorScheme.error,
                                style = UrbanistText.Label
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        ChangeActionButton(
                            text = "Passwort ändern",
                            icon = Icons.Outlined.Lock,
                            onClick = {
                                if (!passwordsMatch || !isPasswordValid) return@ChangeActionButton

                                onChangePassword(currentPassword, password)

                                authViewModel.onPasswordChange("")
                                confirmPassword = ""
                                currentPassword = ""

                                onDismiss()
                            }
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}


