package com.example.jeffenger.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Teal,
    onPrimary = Color.Black,

    secondary = TealLight,
    onSecondary = Color.Black,

    tertiaryContainer = Gray950,
    onTertiaryContainer = Gray200,

    background = BodyColor,
    onBackground = Gray100,

    surface = Gray900,
    onSurface = White,

    surfaceVariant = Gray700,
    onSurfaceVariant = Gray200,

    outline = Gray600,
    outlineVariant = Gray400,

    error = Red
)

private val LightColorScheme = lightColorScheme(
    primary = Teal,
    onPrimary = Color.Black,

    secondary = TealLight,
    onSecondary = Color.Black,

    tertiaryContainer = Gray200,
    onTertiaryContainer = Gray950,

    background = Gray100,
    onBackground = Gray900,

    surface =White,
    onSurface = Gray900,

    surfaceVariant = Gray200,
    onSurfaceVariant = Gray700,

    outline = Gray400,
    error = Red
)


@Composable
fun AppTheme(
//    darkTheme: Boolean = isSystemInDarkTheme(),
//    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
//    val colorScheme = when {
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        }
//
//        darkTheme -> DarkColorScheme
//        else -> LightColorScheme
//    }

    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
