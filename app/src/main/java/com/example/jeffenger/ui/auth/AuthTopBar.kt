package com.example.jeffenger.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.jeffenger.R
import com.example.jeffenger.utils.enums.AuthMode
import com.example.jeffenger.utils.model.ChatTopBarUiState

@Composable
fun AuthTopBar(
    authMode: AuthMode,
    modifier: Modifier = Modifier
) {
    when (authMode) {

        AuthMode.LOGIN -> {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 25.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Image(
                    painter = painterResource(R.drawable.jeffenger_font),
                    contentDescription = "Jeffenger Logo",
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier.height(20.dp)
                )
            }
        }

        AuthMode.REGISTER -> {
            Spacer(
                modifier = modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .height(40.dp)
            )
        }
    }
}