package de.syntax_institut.jetpack.a04_05_online_shopper.utilities

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.jeffenger.ui.theme.BodyColor
import com.example.jeffenger.ui.theme.Gray800
import com.example.jeffenger.ui.theme.Teal


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
    val baseColor = if (isSheet) Gray800 else BodyColor

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(baseColor)
    ) {


        val radiusDp = maxHeight * 0.63f  // 63% HEIGHT

        val density = LocalDensity.current
        val radiusPx = with(density) { radiusDp.toPx() }

        val radialBrush = Brush.radialGradient(
            colorStops = arrayOf(
                0.0f to Teal,
                0.22f to Color(0x8022C997),
                0.8f to Color(0x0024C896)
            ),
            center = Offset(0f, 0f),
            radius = radiusPx
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(radialBrush, alpha = 0.2f)
        )

        content()
    }
}


