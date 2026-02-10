package com.example.jeffenger.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object UrbanistText {

    // urban_bt_reg
    val BodyRegular = TextStyle(
        fontFamily = Urbanist,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.25.sp
    )

    // urban_ph
    val Placeholder = TextStyle(
        fontFamily = Urbanist,
        fontWeight = FontWeight.Light,
        fontStyle = FontStyle.Italic,
        fontSize = 18.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.25.sp
    )

    // label (Urbanist Medium)
    val Label = TextStyle(
        fontFamily = Urbanist,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 22.sp
    )
}