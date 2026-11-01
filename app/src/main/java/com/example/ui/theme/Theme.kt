package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val RetroDarkColorScheme = darkColorScheme(
    primary = DeterminationGold,
    onPrimary = UndertaleBlack,
    primaryContainer = UndertaleCardBg,
    onPrimaryContainer = DeterminationGold,
    secondary = DarkWorldCyan,
    onSecondary = UndertaleBlack,
    secondaryContainer = UndertaleCardBg,
    onSecondaryContainer = DarkWorldCyan,
    tertiary = SoulRed,
    onTertiary = DogBoneWhite,
    background = UndertaleBlack,
    onBackground = DogBoneWhite,
    surface = UndertaleDarkSurface,
    onSurface = DogBoneWhite,
    surfaceVariant = UndertaleCardBg,
    onSurfaceVariant = TextGray,
    outline = UndertaleBorder,
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = RetroDarkColorScheme,
        typography = Typography,
        content = content
    )
}
