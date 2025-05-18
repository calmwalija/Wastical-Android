package net.techandgraphics.wastemanagement.ui.screen.home

import androidx.compose.ui.graphics.Color
import net.techandgraphics.wastemanagement.R
import net.techandgraphics.wastemanagement.ui.screen.home.model.HomeActionUiModel
import net.techandgraphics.wastemanagement.ui.screen.home.model.HomeActivityUiModel
import net.techandgraphics.wastemanagement.ui.theme.WhiteFE

val homeActivityUiModels = listOf(
  HomeActivityUiModel(
    activity = "Payment",
    drawableRes = R.drawable.ic_payment,
    iconBackground = WhiteFE.copy(.7f),
    containerColor = WhiteFE.copy(.2f),
  ),
  HomeActivityUiModel(
    activity = "Collection",
    drawableRes = R.drawable.ic_cleaning_bucket,
    iconBackground = Color.DarkGray,
    containerColor = Color.DarkGray.copy(.05f),
    clickable = false,

  ),
)

val homeActionUiModels = listOf(
  HomeActionUiModel(
    action = "Request for collection",
    drawableRes = R.drawable.ic_truck,
    iconBackground = WhiteFE.copy(.7f),
    containerColor = WhiteFE.copy(.2f),
  ),
  HomeActionUiModel(
    action = "Waste Sorting Guide",
    drawableRes = R.drawable.ic_help,
    iconBackground = WhiteFE.copy(.7f),
    containerColor = WhiteFE.copy(.2f),
  ),
)
