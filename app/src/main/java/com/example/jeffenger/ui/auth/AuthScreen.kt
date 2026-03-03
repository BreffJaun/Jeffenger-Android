package com.example.jeffenger.ui.auth

import android.R.attr.top
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jeffenger.R
import com.example.jeffenger.ui.catApi.CatSection
import com.example.jeffenger.ui.catApi.CatViewModel
import com.example.jeffenger.ui.core.avatar.ProfileAvatar
import com.example.jeffenger.ui.theme.UrbanistText
import com.example.jeffenger.ui.viewmodels.AuthViewModel
import com.example.jeffenger.utils.debugging.LogComposable
import com.example.jeffenger.utils.enums.AuthMode
import com.example.jeffenger.utils.error.ErrorMapper
import com.example.jeffenger.utils.error.ErrorMessageResolver
import com.example.jeffenger.utils.state.LoadingState
import de.syntax_institut.projektwoche1.ui.component.TopBar
import org.koin.androidx.compose.koinViewModel

/**
 * Authentication screen composable.
 *
 * Displays:
 * - Registration form
 * - Login form
 * - Password validation feedback
 *
 * Observes:
 * - Form state from AuthViewModel
 * - Loading state
 * - Validation flows
 *
 * Pure UI layer — contains no business logic.
 */

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AuthScreen(
    viewModel: AuthViewModel = koinViewModel()
) {
    LogComposable("AuthScreen") {

        val scheme = MaterialTheme.colorScheme

        val loadingState by viewModel.loadingState.collectAsState()
        val scrollState = rememberScrollState()

        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        val catViewModel: CatViewModel = viewModel()
        val catUrl by catViewModel.catImageUrl.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.errorEvents.collect { error ->
                val friendlyMessage =
                    ErrorMessageResolver.resolve(error)
                catViewModel.loadRandomCat()
                snackbarHostState.showSnackbar(friendlyMessage)
            }
        }

        val email by viewModel.email.collectAsState()
        val password by viewModel.password.collectAsState()
        var confirmPassword by remember { mutableStateOf("") }
        val passwordsMatch =
            password.isNotBlank() &&
                    confirmPassword.isNotBlank() &&
                    password == confirmPassword

        val displayName by viewModel.displayName.collectAsState()
        val company by viewModel.company.collectAsState()

        val isEmailValid by viewModel.isEmailValid.collectAsState(initial = false)
        val isPasswordValid by viewModel.isPasswordValid.collectAsState(initial = false)
        val isDisplayNameValid by viewModel.isDisplayNameValid.collectAsState(initial = false)
        val isCompanyValid by viewModel.isCompanyValid.collectAsState(initial = false)
        val isFormValid by viewModel.isFormValid.collectAsState(initial = false)
        val authMode by viewModel.authMode.collectAsState()

        val hasMinLength by viewModel.hasMinLength.collectAsState(false)
        val hasUppercase by viewModel.hasUppercase.collectAsState(false)
        val hasLowercase by viewModel.hasLowercase.collectAsState(false)
        val hasDigit by viewModel.hasDigit.collectAsState(false)
        val hasSpecialChar by viewModel.hasSpecialChar.collectAsState(false)

        // Own scaffold only for AuthScreen
        @Suppress("UnusedMaterial3ScaffoldPaddingParameter")
        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            topBar = { AuthTopBar(authMode = authMode) }
        ) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 25.dp)
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(top = 60.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    if (authMode == AuthMode.REGISTER) {
                        Image(
                            painter = painterResource(R.drawable.breffjaun_profile_img),
                            contentDescription = "image_of_jeff",
                            modifier = Modifier
                                .padding(top = 25.dp)
                                .padding(bottom = 15.dp)
                                .size(50.dp)
                                .clip(CircleShape)
                        )

                        Text(
                            text = "Hi, ich bin Jeff.",
                            color = scheme.onSurface,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = "Toll, dass Du da bist. Ich brauch nur ein \npaar Infos, dann chatten wir.",
                            color = scheme.onSurfaceVariant,
                            style = UrbanistText.BodyRegular,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(top = 5.dp)
                                .padding(bottom = 45.dp)
                        )

                        Text(
                            text = "Dein Profil",
                            style = MaterialTheme.typography.displaySmall,
                            color = scheme.onSurface,
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        ProfileAvatar(displayName = displayName)
                    } else {
                        Spacer(modifier = Modifier.height(75.dp))

                        Text(
                            text = "Login",
                            style = MaterialTheme.typography.displaySmall,
                            color = scheme.onSurface,
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (catUrl != null) {
                            CatSection(
                                modifier = Modifier.padding(bottom = 4.dp),
                                catViewModel = catViewModel
                            )
                            Text(
                                text = "Passwort falsch? Hier ist eine Katze zur moralischen Unterstützung 🐱",
                                style = UrbanistText.Label,
                                color = scheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    if (authMode == AuthMode.REGISTER) {
                        AuthTextField(
                            value = displayName,
                            onValueChange = viewModel::onDisplayNameChange,
                            label = "Name",
                            placeholder = "Max Mustermann",
                            isValid = isDisplayNameValid
                        )

                        AuthTextField(
                            value = company,
                            onValueChange = viewModel::onCompanyChange,
                            label = "Firma",
                            placeholder = "Mustermann GmbH",
                            isValid = isCompanyValid
                        )
                    }

                    AuthTextField(
                        value = email,
                        onValueChange = viewModel::onEmailChange,
                        label = "E-Mail-Adresse",
                        placeholder = "max@firma.de",
                        isValid = isEmailValid
                    )

                    AuthTextField(
                        value = password,
                        onValueChange = viewModel::onPasswordChange,
                        label = "Passwort",
                        placeholder = if (authMode == AuthMode.REGISTER)
                            "Sicheres Passwort"
                        else "",
                        isPassword = true,
                        isValid = isPasswordValid
                    )

                    if (
                        authMode == AuthMode.REGISTER &&
                        password.isNotEmpty() &&
                        !isPasswordValid
                    ) {
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

                    if (authMode == AuthMode.REGISTER) {

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
                                color = scheme.error,
                                style = UrbanistText.Label
                            )
                        }
                    }

                    TextButton(
                        onClick = {
                            viewModel.setAuthMode(
                                if (authMode == AuthMode.LOGIN)
                                    AuthMode.REGISTER
                                else
                                    AuthMode.LOGIN
                            )
                        }
                    ) {
                        Text(
                            when (authMode) {
                                AuthMode.REGISTER -> "Schon registriert? Anmelden"
                                AuthMode.LOGIN -> "Noch kein Account? Registrieren"
                            },
                            style = UrbanistText.Label,
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = { viewModel.submit() },
//                        enabled = isFormValid && loadingState !is LoadingState.Loading,
                        enabled =
                            isFormValid && loadingState !is LoadingState.Loading && (
                                authMode == AuthMode.LOGIN ||
                                        passwordsMatch
                            ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = scheme.primary,
                            contentColor = scheme.surface,
                            disabledContainerColor = scheme.outline,
                            disabledContentColor = scheme.surface
                        ),
                        modifier = Modifier.padding(bottom = 25.dp),
                    ) {
                        if (loadingState is LoadingState.Loading) {
                            CircularProgressIndicator(
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text(
                                "los geht’s",
                                style = MaterialTheme.typography.titleLarge,
                            )
                        }
                    }
                }
            }
        }
    }
}

