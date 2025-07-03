package net.techandgraphics.quantcal.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import net.techandgraphics.quantcal.R

val MerriWeatherSans = FontFamily(
  Font(R.font.merri_weather_sans_light, FontWeight.Light),
  Font(R.font.merri_weather_sans_bold, FontWeight.Bold),
  Font(R.font.merri_weather_sans_regular, FontWeight.Normal),
  Font(R.font.merri_weather_sans_medium, FontWeight.Medium),
  Font(R.font.merri_weather_sans_extra_bold, FontWeight.ExtraBold),
)

private val dl = Typography().displayLarge
private val dm = Typography().displayMedium
private val ds = Typography().displaySmall
private val hl = Typography().headlineLarge
private val hm = Typography().headlineMedium
private val hs = Typography().headlineSmall
private val tl = Typography().titleLarge
private val tm = Typography().titleMedium
private val ts = Typography().titleSmall
private val bl = Typography().bodyLarge
private val bm = Typography().bodyMedium
private val bs = Typography().bodySmall
private val ll = Typography().labelLarge
private val lm = Typography().labelMedium
private val ls = Typography().labelSmall

fun setTypography(fontFamily: FontFamily = MerriWeatherSans) = Typography(
  displayLarge = dl.copy(fontFamily = fontFamily),
  displayMedium = dm.copy(fontFamily = fontFamily),
  displaySmall = ds.copy(fontFamily = fontFamily),
  headlineLarge = hl.copy(fontFamily = fontFamily),
  headlineMedium = hm.copy(fontFamily = fontFamily),
  headlineSmall = hs.copy(fontFamily = fontFamily),
  titleLarge = tl.copy(fontFamily = fontFamily),
  titleMedium = tm.copy(fontFamily = fontFamily),
  titleSmall = ts.copy(fontFamily = fontFamily),
  bodyLarge = bl.copy(fontFamily = fontFamily),
  bodyMedium = bm.copy(fontFamily = fontFamily),
  bodySmall = bs.copy(fontFamily = fontFamily),
  labelLarge = ll.copy(fontFamily = fontFamily),
  labelMedium = lm.copy(fontFamily = fontFamily),
  labelSmall = ls.copy(fontFamily = fontFamily),
)
