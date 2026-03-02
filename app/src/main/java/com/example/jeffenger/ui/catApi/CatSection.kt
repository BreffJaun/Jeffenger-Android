package com.example.jeffenger.ui.catApi

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.jeffenger.utils.debugging.LogComposable
import com.example.jeffenger.utils.state.LoadingState

@Composable
fun CatSection(
    modifier: Modifier = Modifier,
    catViewModel: CatViewModel = viewModel()
) {
    LogComposable("CatSection") {

        val catUrl by catViewModel.catImageUrl.collectAsState()

        if (catUrl != null) {
            Box(
                modifier = modifier
                    .size(100.dp)
                    .clip(CircleShape)
            ) {
                AsyncImage(
                    model = catUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}