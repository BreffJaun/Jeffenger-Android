package com.example.jeffenger.ui.theme


import android.R.attr.fontFamily
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.jeffenger.R

val Archivo = FontFamily(
    Font(R.font.archivo_light, FontWeight.Light),
    Font(R.font.archivo_medium, FontWeight.Medium),
    Font(R.font.archivo_bold, FontWeight.Bold),
    Font(R.font.archivo_extrabold, FontWeight.ExtraBold),
    Font(R.font.archivo_black, FontWeight.Black)
)

val Urbanist = FontFamily(
    Font(R.font.urbanist_regular, FontWeight.Normal),
    Font(R.font.urbanist_medium, FontWeight.Medium),
    Font(R.font.urbanist_light, FontWeight.Light),
    Font(
        R.font.urbanist_light_italic,
        FontWeight.Light,
        style = FontStyle.Italic
    )
)

// AVATAR INITIALS -> Usage: AvatarCircle initials
val AvatarInitialsTextStyle = TextStyle(
    fontFamily = Archivo,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 42.sp,
    letterSpacing = 0.sp
)

val Typography = Typography(

    // H1 (Archivo Black 42 / 42)
    displayLarge = TextStyle(
        fontFamily = Archivo,
        fontWeight = FontWeight.Black,
        fontSize = 42.sp,
        lineHeight = 42.sp
    ),

    // H2 (Archivo Black 36 / 42)
    displayMedium = TextStyle(
        fontFamily = Archivo,
        fontWeight = FontWeight.Black,
        fontSize = 36.sp,
        lineHeight = 42.sp
    ),

    // H3 (Archivo Black 27 / 42)
    displaySmall = TextStyle(
        fontFamily = Archivo,
        fontWeight = FontWeight.Black,
        fontSize = 27.sp,
        lineHeight = 42.sp
    ),

    // H4 (Archivo Black 18 / 22)
    titleMedium = TextStyle(
        fontFamily = Archivo,
        fontWeight = FontWeight.Black,
        fontSize = 18.sp,
        lineHeight = 22.sp
    ),

    // archivo_bt
    // → Chat-Name / Primary title
    titleLarge = TextStyle(
        fontFamily = Archivo,
        fontWeight = FontWeight.Black,
        fontSize = 18.sp,
        lineHeight = 22.sp
    ),

    // archivo_label (14)
    labelLarge = TextStyle(
        fontFamily = Archivo,
        fontWeight = FontWeight.Black,
        fontSize = 14.sp,
        lineHeight = 22.sp
    ),

    // Fallbacks (kaum genutzt)
    bodyMedium = TextStyle(
        fontFamily = Archivo,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    bodySmall = TextStyle(
        fontFamily = Archivo,
        fontWeight = FontWeight.Light,
        fontSize = 12.sp
    )
)

//val Typography = Typography(
//
//    displayLarge = TextStyle(
//        fontFamily = Archivo,
//        fontWeight = FontWeight.Black,
//        fontSize = 57.sp
//    ),
//    displayMedium = TextStyle(
//        fontFamily = Archivo,
//        fontWeight = FontWeight.ExtraBold,
//        fontSize = 45.sp
//    ),
//    displaySmall = TextStyle(
//        fontFamily = Archivo,
//        fontWeight = FontWeight.Bold,
//        fontSize = 36.sp
//    ),
//
//    headlineLarge = TextStyle(
//        fontFamily = Archivo,
//        fontWeight = FontWeight.Bold,
//        fontSize = 32.sp
//    ),
//    headlineMedium = TextStyle(
//        fontFamily = Archivo,
//        fontWeight = FontWeight.Medium,
//        fontSize = 28.sp
//    ),
//    headlineSmall = TextStyle(
//        fontFamily = Archivo,
//        fontWeight = FontWeight.Medium,
//        fontSize = 24.sp
//    ),
//
//    titleLarge = TextStyle(
//        fontFamily = Archivo,
//        fontWeight = FontWeight.Medium,
//        fontSize = 22.sp
//    ),
//    // Chat list – Name
//    titleMedium = TextStyle(
//        fontFamily = Archivo,
//        fontWeight = FontWeight.Bold,
//        fontSize = 18.sp,
//        lineHeight = 22.sp
//    ),
//
////    titleMedium = TextStyle(
////        fontFamily = Archivo,
////        fontWeight = FontWeight.Medium,
////        fontSize = 16.sp
////    ),
//    titleSmall = TextStyle(
//        fontFamily = Archivo,
//        fontWeight = FontWeight.Medium,
//        fontSize = 14.sp
//    ),
//
//    // Chat list – Message preview
//    bodyLarge = TextStyle(
//        fontFamily = Archivo,
//        fontWeight = FontWeight.Normal,
//        fontSize = 18.sp,
//        lineHeight = 22.sp,
//        letterSpacing = 0.25.sp
//    ),
////    bodyLarge = TextStyle(
////        fontFamily = Archivo,
////        fontWeight = FontWeight.Normal,
////        fontSize = 16.sp,
////        lineHeight = 24.sp
////    ),
//    bodyMedium = TextStyle(
//        fontFamily = Archivo,
//        fontWeight = FontWeight.Normal,
//        fontSize = 14.sp,
//        lineHeight = 20.sp
//    ),
//    bodySmall = TextStyle(
//        fontFamily = Archivo,
//        fontWeight = FontWeight.Light,
//        fontSize = 12.sp,
//        lineHeight = 16.sp
//    ),
//
//    labelLarge = TextStyle(
//        fontFamily = Archivo,
//        fontWeight = FontWeight.Medium,
//        fontSize = 14.sp
//    ),
//    // Timestamp
//    labelMedium = TextStyle(
//        fontFamily = Archivo,
//        fontWeight = FontWeight.Medium,
//        fontSize = 12.sp
//    ),
////    labelMedium = TextStyle(
////        fontFamily = Archivo,
////        fontWeight = FontWeight.Medium,
////        fontSize = 12.sp
////    ),
//    labelSmall = TextStyle(
//        fontFamily = Archivo,
//        fontWeight = FontWeight.Medium,
//        fontSize = 11.sp
//    )
//)
