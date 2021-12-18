package com.sychev.facedetector.presentation.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.sychev.facedetector.R

private val Rubik = FontFamily(
    Font(R.font.rubik_light, FontWeight.W100),
    Font(R.font.rubik_light, FontWeight.W200),
    Font(R.font.rubik_regular, FontWeight.W300),
    Font(R.font.rubik_medium, FontWeight.W500),
    Font(R.font.rubik_bold, FontWeight.W700),
)

val RubikTypography = Typography(
    h1 = TextStyle(
        fontFamily = Rubik,
        fontWeight = FontWeight.W500,
        fontSize = 23.sp
    ),
    h2 = TextStyle(
        fontFamily = Rubik,
        fontWeight = FontWeight.W500,
        fontSize = 19.sp
    ),
    h3 = TextStyle(
        fontFamily = Rubik,
        fontWeight = FontWeight.W500,
        fontSize = 17.sp
    ),
    h4 = TextStyle(
        fontFamily = Rubik,
        fontWeight = FontWeight.W400,
        fontSize = 15.sp
    ),
    h5 = TextStyle(
        fontFamily = Rubik,
        fontWeight = FontWeight.W400,
        fontSize = 13.sp
    ),
    h6 = TextStyle(
        fontFamily = Rubik,
        fontWeight = FontWeight.W400,
        fontSize = 11.sp
    ),
    subtitle1 = TextStyle(
        fontFamily = Rubik,
        fontWeight = FontWeight.W200,
        fontSize = 15.sp
    ),
    subtitle2 = TextStyle(
        fontFamily = Rubik,
        fontWeight = FontWeight.W200,
        fontSize = 11.sp,
    ),
    body1 = TextStyle(
        fontFamily = Rubik,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp
    ),
    body2 = TextStyle(
        fontFamily = Rubik,
        fontSize = 11.sp
    ),
    button = TextStyle(
        fontFamily = Rubik,
        fontWeight = FontWeight.W500,
        fontSize = 12.sp,
    ),
    caption = TextStyle(
        fontFamily = Rubik,
        fontWeight = FontWeight.W300,
        fontSize = 9.sp
    ),
    overline = TextStyle(
        fontFamily = Rubik,
        fontWeight = FontWeight.W700,
        fontSize = 11.sp
    )
)