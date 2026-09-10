package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CricketDarkColorScheme = darkColorScheme(
  primary = CricketGreen,
  onPrimary = Color(0xFF003816),
  primaryContainer = CricketGreenDark,
  onPrimaryContainer = Color(0xFFDCFCE7),
  secondary = ElectricBlue,
  onSecondary = Color(0xFF003258),
  secondaryContainer = CricketBlue,
  onSecondaryContainer = Color(0xFFE0F2FE),
  tertiary = ColorSix,
  background = DarkBg,
  surface = DarkSurface,
  surfaceVariant = DarkCard,
  onBackground = TextWhite,
  onSurface = TextWhite,
  onSurfaceVariant = TextMuted,
  outline = DarkCardBorder,
  error = ColorWicket
)

private val CricketLightColorScheme = lightColorScheme(
  primary = CricketGreenDark,
  onPrimary = Color.White,
  primaryContainer = Color(0xFFD1FAE5),
  onPrimaryContainer = Color(0xFF065F46),
  secondary = CricketBlue,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFFBAE6FD),
  onSecondaryContainer = Color(0xFF0369A1),
  tertiary = ColorSix,
  background = LightBg,
  surface = LightSurface,
  surfaceVariant = Color(0xFFE2E8F0),
  onBackground = TextDark,
  onSurface = TextDark,
  onSurfaceVariant = TextDarkMuted,
  outline = LightCardBorder,
  error = ColorWicket
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Default to dark mode as requested
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) CricketDarkColorScheme else CricketLightColorScheme
  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}

