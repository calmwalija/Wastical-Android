package net.techandgraphics.wastical.ui.screen.company.home

import androidx.compose.ui.graphics.Color

data class CompanyHomeItemModel(
  val title: String,
  val drawableRes: Int = 1,
  val containerColor: Color,
  val event: CompanyHomeEvent = CompanyHomeEvent.Tap,
)
