package com.example.gf5.ui.theme



import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.gf5.ui.theme.GF5Shapes
import com.example.gf5.ui.theme.GF5Typography

// Define the color scheme
private val GratisFareBlue = Color(0xFF4A90E2)
private val LightGray = Color(0xFFF2F2F2)
private val White = Color(0xFFFFFFFF)
private val NavyBlue = Color(0xFF2C3E50)
private val SoftYellow = Color(0xFFF1C40F)

@Preview(showBackground = true)
@Composable
fun GF5ThemePreview() {
    GF5Theme {
        // Your preview UI components here
    }
}

@Composable
fun GF5Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        darkColorScheme(
            primary = GratisFareBlue,
            onPrimary = White,
            primaryContainer = NavyBlue,
            secondary = SoftYellow,
            onSecondary = NavyBlue,
            background = NavyBlue,
            onBackground = LightGray,
            surface = NavyBlue,
            onSurface = LightGray,
        )
    } else {
        lightColorScheme(
            primary = GratisFareBlue,
            onPrimary = White,
            primaryContainer = LightGray,
            secondary = SoftYellow,
            onSecondary = NavyBlue,
            background = LightGray,
            onBackground = NavyBlue,
            surface = White,
            onSurface = NavyBlue,
        )
    }

    MaterialTheme(
        colorScheme = colors,
        typography = GF5Typography,
        shapes = GF5Shapes,
        content = content
    )
}

