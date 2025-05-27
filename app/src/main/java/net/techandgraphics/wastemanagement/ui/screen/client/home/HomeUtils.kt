package net.techandgraphics.wastemanagement.ui.screen.client.home

import androidx.compose.ui.graphics.Color
import net.techandgraphics.wastemanagement.R
import net.techandgraphics.wastemanagement.ui.screen.client.home.model.HomeActivityUiModel
import net.techandgraphics.wastemanagement.ui.screen.client.home.model.HomeQuickActionUiModel
import net.techandgraphics.wastemanagement.ui.theme.WhiteFE

internal val homeActivityUiModels = listOf(
  HomeActivityUiModel(
    activity = "Payment Due",
    drawableRes = R.drawable.ic_payment,
    iconBackground = WhiteFE.copy(.7f),
    containerColor = WhiteFE.copy(.2f),
    event = HomeEvent.Button.MakePayment,
  ),
  HomeActivityUiModel(
    activity = "Trash Collection",
    drawableRes = R.drawable.ic_cleaning_bucket,
    iconBackground = Color.DarkGray,
    containerColor = Color.DarkGray.copy(.05f),
    clickable = false,
    event = HomeEvent.Button.MakePayment,
  ),
)

internal val homeQuickActionUiModels = listOf(
  HomeQuickActionUiModel(
    title = "Make Payment",
    drawableRes = R.drawable.ic_payments,
    event = HomeEvent.Button.MakePayment,
  ),
  HomeQuickActionUiModel(
    title = "Waste Sorting Guide",
    drawableRes = R.drawable.ic_help,
    iconBackground = WhiteFE.copy(.7f),
    containerColor = WhiteFE.copy(.2f),
    event = HomeEvent.Button.WasteSortGuide,
  ),
)
