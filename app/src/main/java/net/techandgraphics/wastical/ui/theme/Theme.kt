package net.techandgraphics.wastical.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = Mellon10,
  tertiaryContainer = Mellon10,
  onPrimary = Color.White
)

private val LightColorScheme = lightColorScheme(
  primary = Yellow20,
  onPrimaryContainer = Mellon10,
  tertiaryContainer = Mellon10,
  onPrimary = Color.Black
)


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WasticalTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }

    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialExpressiveTheme(
    colorScheme = colorScheme,
    typography = setTypography(),
    content = content
  )
}
