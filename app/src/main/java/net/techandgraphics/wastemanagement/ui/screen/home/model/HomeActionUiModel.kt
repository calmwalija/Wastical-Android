package net.techandgraphics.wastemanagement.ui.screen.home.model

import androidx.compose.ui.graphics.Color

data class HomeActionUiModel(
  val action: String,
  val drawableRes: Int,
  val iconTint: Color = Color.White,
  val iconBackground: Color = Color.White,
  val containerColor: Color = Color.White,
)
