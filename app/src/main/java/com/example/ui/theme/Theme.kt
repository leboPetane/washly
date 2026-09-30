package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = UberGreenDarkPrimary,
    onPrimary = UberGreenDarkOnPrimary,
    primaryContainer = UberGreenDarkContainer,
    onPrimaryContainer = UberGreenDarkOnContainer,
    secondary = UberDarkDarkSecondary,
    onSecondary = UberDarkDarkOnSecondary,
    secondaryContainer = UberDarkDarkContainer,
    onSecondaryContainer = UberDarkDarkOnContainer,
    tertiary = UberGoldDarkTertiary,
    onTertiary = UberGoldDarkOnTertiary,
    tertiaryContainer = UberGoldDarkContainer,
    onTertiaryContainer = UberGoldDarkOnContainer,
    background = WashlyBackgroundDark,
    surface = WashlySurfaceDark,
    surfaceVariant = WashlySurfaceVariantDark,
    outline = WashlyOutlineDark,
    onSurface = WashlyOnSurfaceDark,
    onSurfaceVariant = WashlyOnSurfaceVariantDark
)

private val LightColorScheme = lightColorScheme(
    primary = UberGreenPrimary,
    onPrimary = UberGreenOnPrimary,
    primaryContainer = UberGreenContainer,
    onPrimaryContainer = UberGreenOnContainer,
    secondary = UberDarkSecondary,
    onSecondary = UberDarkOnSecondary,
    secondaryContainer = UberDarkContainer,
    onSecondaryContainer = UberDarkOnContainer,
    tertiary = UberGoldTertiary,
    onTertiary = UberGoldOnTertiary,
    tertiaryContainer = UberGoldContainer,
    onTertiaryContainer = UberGoldOnContainer,
    background = WashlyBackgroundLight,
    surface = WashlySurfaceLight,
    surfaceVariant = WashlySurfaceVariantLight,
    outline = WashlyOutlineLight,
    onSurface = WashlyOnSurfaceLight,
    onSurfaceVariant = WashlyOnSurfaceVariantLight
)

@Composable
fun MyApplicationTheme(
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
