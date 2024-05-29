package com.example.gf5

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun GF5Theme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        darkColorScheme(
            primary = Purple_500,

            secondary = Teal_200
        )
    } else {
        lightColorScheme(
            primary = Purple_500,

            secondary = Teal_200
        )
    }

    MaterialTheme(
        colorScheme = colors,


        content = content
    )
}
