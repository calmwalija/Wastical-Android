package net.techandgraphics.wastical.ui.screen.client.home

import androidx.compose.ui.graphics.Color

data class ClientHomeQuickActionItemModel(
  val title: String,
  val drawableRes: Int,
  val iconTint: Color = Color.Companion.White,
  val iconBackground: Color = Color.Companion.White,
  val containerColor: Color = Color.Companion.White,
  val event: ClientHomeEvent,
)
