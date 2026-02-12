package com.example.jeffenger.navigation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jeffenger.ui.viewmodels.AuthViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = koinViewModel()
) {
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val authState by viewModel.authState.collectAsState()
    var showRegister by remember { mutableStateOf(true) }
    val buttonText = if(showRegister) "Register" else "Login"
    val switchText = if(!showRegister) "Register" else "Login"
    // Müssen hier das if else in ein Lambda packen, da sonst eine der beiden Funktionen direkt in Zeile 30
    // ausgeführt wird. Das Lambda ermöglicht uns diesen Funktionsaufruf später zu machen.
    val authFunction = { if(showRegister) viewModel.registerWithEmailAndPassword() else viewModel.loginWithEmailAndPassword() }

    if(authState != null) {
        // Oder auch AppStart / AppNavigation
//        HomeScreen()
    } else {
        Scaffold { innerPadding ->
            Column(
                Modifier.fillMaxSize().padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("$buttonText screen")

                    TextField(
                        value = email,
                        // Kurze schreibweise, da die Funktionen die gleiche Signatur haben.
                        onValueChange = viewModel::onEmailChange,
                        label = { Text("Email") }
                    )

                    TextField(
                        value = password,
                        onValueChange = { viewModel.onPasswordChange(it) },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation()
                    )

                    Button({
                        authFunction()
                    }) { Text(buttonText) }

                    TextButton({
                        showRegister = !showRegister
                    }) { Text("$switchText here...") }
                }
            }
        }
    }
}


@Preview(showSystemUi = true)
@Composable
fun AuthScreenPreview() {
    AuthScreen()
}