package net.techandgraphics.wastemanagement.ui.screen.home.model

import androidx.compose.ui.graphics.Color
import java.time.LocalDate

data class HomeActivityUiModel(
  val activity: String,
  val drawableRes: Int,
  val date: LocalDate = LocalDate.now(),
  val iconTint: Color = Color.White,
  val iconBackground: Color = Color.White,
  val containerColor: Color = Color.White,
  val clickable: Boolean = true,
)
