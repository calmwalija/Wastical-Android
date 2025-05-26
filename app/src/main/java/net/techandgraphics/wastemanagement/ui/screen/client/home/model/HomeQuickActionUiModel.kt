package net.techandgraphics.wastemanagement.ui.screen.client.home.model

import androidx.compose.ui.graphics.Color
import net.techandgraphics.wastemanagement.ui.screen.client.home.HomeEvent

data class HomeQuickActionUiModel(
  val title: String,
  val drawableRes: Int,
  val iconTint: Color = Color.White,
  val iconBackground: Color = Color.White,
  val containerColor: Color = Color.White,
  val event: HomeEvent,
)
