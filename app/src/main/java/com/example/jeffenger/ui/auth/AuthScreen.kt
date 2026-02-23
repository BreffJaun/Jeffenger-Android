package com.example.jeffenger.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jeffenger.R
import com.example.jeffenger.ui.core.avatar.ProfileAvatar
import com.example.jeffenger.ui.theme.UrbanistText
import com.example.jeffenger.ui.viewmodels.AuthViewModel
import com.example.jeffenger.utils.debugging.LogComposable
import com.example.jeffenger.utils.enums.AuthMode
import org.koin.androidx.compose.koinViewModel

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = koinViewModel()
) {
    LogComposable("AuthScreen") {
        val scheme = MaterialTheme.colorScheme

        val email by viewModel.email.collectAsState()
        val password by viewModel.password.collectAsState()
        val displayName by viewModel.displayName.collectAsState()
        val company by viewModel.company.collectAsState()
        val loadingState by viewModel.loadingState.collectAsState()

        val isEmailValid by viewModel.isEmailValid.collectAsState(initial = false)
        val isPasswordValid by viewModel.isPasswordValid.collectAsState(initial = false)
        val isDisplayNameValid by viewModel.isDisplayNameValid.collectAsState(initial = false)
        val isCompanyValid by viewModel.isCompanyValid.collectAsState(initial = false)
        val isFormValid by viewModel.isFormValid.collectAsState(initial = false)
        var isRegister by remember { mutableStateOf(true) }
        val authMode by viewModel.authMode.collectAsState()

        val hasMinLength by viewModel.hasMinLength.collectAsState(false)
        val hasUppercase by viewModel.hasUppercase.collectAsState(false)
        val hasLowercase by viewModel.hasLowercase.collectAsState(false)
        val hasDigit by viewModel.hasDigit.collectAsState(false)
        val hasSpecialChar by viewModel.hasSpecialChar.collectAsState(false)


        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 25.dp)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 60.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Image
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
                        .padding(bottom = 73.dp)
                )

                Text(
                    text = "Dein Profil",
                    style = MaterialTheme.typography.displaySmall,
                    color = scheme.onSurface,
                )

                Spacer(modifier = Modifier.height(24.dp))

                ProfileAvatar(
                    displayName = displayName
                )

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
                    else
                        "",
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

//                Spacer(modifier = Modifier.height(16.dp))
                Spacer(modifier = Modifier.weight(1f))

                // CTA
                Button(
                    onClick = { viewModel.submit() },
                    enabled = isFormValid,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = scheme.primary,
                        contentColor = scheme.surface,
                        disabledContainerColor = scheme.outline,
                        disabledContentColor = scheme.surface
                    ),
                    modifier = Modifier.padding(bottom = 25.dp),
                ) {
                    Text(
                        "los geht’s",
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
            }
        }
    }
}


@Preview(showSystemUi = true)
@Composable
fun AuthScreenPreview() {
//    AuthScreen()
}