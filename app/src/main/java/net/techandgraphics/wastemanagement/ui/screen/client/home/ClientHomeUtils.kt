package net.techandgraphics.wastemanagement.ui.screen.client.home

import androidx.compose.ui.graphics.Color
import net.techandgraphics.wastemanagement.R
import net.techandgraphics.wastemanagement.ui.theme.WhiteFE

internal val homeActivityUiModels = listOf(
  ClientHomeActivityItemModel(
    activity = "Trash Collection",
    drawableRes = R.drawable.ic_cleaning_bucket,
    iconBackground = Color.DarkGray,
    containerColor = Color.DarkGray.copy(.05f),
    clickable = false,
    event = ClientHomeEvent.Button.MakePayment,
  ),
  ClientHomeActivityItemModel(
    activity = "Payment Due",
    drawableRes = R.drawable.ic_payment,
    iconBackground = WhiteFE.copy(.7f),
    containerColor = WhiteFE.copy(.2f),
    event = ClientHomeEvent.Button.MakePayment,
  ),
)

internal val homeQuickActionUiModels = listOf(
  ClientHomeQuickActionItemModel(
    title = "Send Payment Screenshot",
    drawableRes = R.drawable.ic_add_photo,
    event = ClientHomeEvent.Button.MakePayment,
  ),
)
