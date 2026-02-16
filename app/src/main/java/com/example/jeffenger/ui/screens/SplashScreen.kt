package com.example.jeffenger.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.jeffenger.R
import com.example.jeffenger.utils.debugging.LogComposable
import de.syntax_institut.jetpack.a04_05_online_shopper.utilities.BackgroundWrapper
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onFinished: () -> Unit
) {
    LogComposable("SplashScreen") {

        LaunchedEffect(Unit) {
            delay(3000)
            onFinished()
        }

        BackgroundWrapper {

            Box(
                modifier = Modifier.fillMaxSize()
            ) {

                // CENTER CONTENT
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Image(
                        painter = painterResource(R.drawable.jeffenger_splash_icon),
                        contentDescription = "Jeffenger Icon",
                        modifier = Modifier.size(120.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Image(
                        painter = painterResource(R.drawable.jeffenger_lettering),
                        contentDescription = "Jeffenger Lettering"
                    )
                }

                // BOTTOM CONTENT
                Image(
                    painter = painterResource(R.drawable.app_by),
                    contentDescription = "App by",
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 48.dp)
                )
            }
        }
    }
}