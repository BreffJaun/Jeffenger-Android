package de.syntax_institut.jetpack.a04_05_online_shopper.utilities

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import com.example.jeffenger.ui.theme.BodyColor
import com.example.jeffenger.ui.theme.Gray800
import com.example.jeffenger.ui.theme.Gray900

/**
 * Provides a full-screen gradient background wrapper for the app.
 *
 * Responsibilities:
 * - Fills the entire available screen space
 * - Renders a gradient background depending on context
 * - Hosts arbitrary composable content on top of the background
 *
 * Behaviour:
 * - Uses a softer gradient for main screens
 * - Uses a stronger gradient when displayed inside modal sheets
 *
 * This composable does not manage any internal state and serves
 * purely as a layout and styling helper.
 *
 * @param isSheet Whether the background is used inside a modal sheet
 * @param modifier Optional modifier for external layout adjustments
 * @param content Composable content to be displayed on top of the background
 */
@Composable
fun BackgroundWrapper(
    isSheet: Boolean = false,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val colors = if (isSheet) listOf(Gray800, Gray900) else listOf(BodyColor, Gray900)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors))
    ) {
        content()
    }
}


@Preview(showSystemUi = true)
@Composable
private fun BackgroundWrapperPreview() {
    BackgroundWrapper {
        Text("Hallo Welt")
    }
}

//@Composable
//fun BackgroundWrapperColumnCentered(
//    isSheet: Boolean = false,
//    modifier: Modifier = Modifier,
//    content: @Composable () -> Unit
//) {
//    val colors = if (isSheet) listOf(Gray800, Gray900) else listOf(BodyColor, Gray900)
//
//    Box(
//        modifier = modifier
//            .fillMaxSize()
//            .background(Brush.verticalGradient(colors))
//    ) {
//        Column(
//            modifier = Modifier.fillMaxSize(),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            content()
//        }
//    }
//}