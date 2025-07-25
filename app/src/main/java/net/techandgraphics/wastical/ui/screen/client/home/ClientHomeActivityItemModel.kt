package net.techandgraphics.wastical.ui.screen.client.home

import androidx.compose.ui.graphics.Color
import java.time.ZonedDateTime

data class ClientHomeActivityItemModel(
  val activity: String,
  val drawableRes: Int,
  val epochSecond: Long = ZonedDateTime.now().toEpochSecond(),
  val iconTint: Color = Color.Companion.White,
  val iconBackground: Color = Color.Companion.White,
  val containerColor: Color = Color.Companion.White,
  val clickable: Boolean = true,
)
