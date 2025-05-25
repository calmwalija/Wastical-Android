package net.techandgraphics.wastemanagement.ui.screen.home.model

import androidx.compose.ui.graphics.Color
import net.techandgraphics.wastemanagement.ui.screen.home.HomeEvent
import java.time.ZonedDateTime

data class HomeActivityUiModel(
  val activity: String,
  val drawableRes: Int,
  val epochSecond: Long = ZonedDateTime.now().toEpochSecond(),
  val iconTint: Color = Color.White,
  val iconBackground: Color = Color.White,
  val containerColor: Color = Color.White,
  val clickable: Boolean = true,
  val event: HomeEvent,
)
