package com.example.jeffenger.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

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
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}



//private val DarkColorScheme = darkColorScheme(
//    primary = Purple80,
//    secondary = PurpleGrey80,
//    tertiary = Pink80
//)

//private val LightColorScheme = lightColorScheme(
//    primary = Purple40,
//    secondary = PurpleGrey40,
//    tertiary = Pink40
//
//    /* Other default colors to override
//    background = Color(0xFFFFFBFE),
//    surface = Color(0xFFFFFBFE),
//    onPrimary = Color.White,
//    onSecondary = Color.White,
//    onTertiary = Color.White,
//    onBackground = Color(0xFF1C1B1F),
//    onSurface = Color(0xFF1C1B1F),
//    */
//)