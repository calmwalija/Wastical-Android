package net.techandgraphics.wastical.ui.screen.client.home

import androidx.compose.ui.graphics.Color
import net.techandgraphics.wastical.R
import net.techandgraphics.wastical.ui.theme.WhiteFE

internal val homeActivityUiModels = listOf(
  ClientHomeActivityItemModel(
    activity = "Bin Collection",
    drawableRes = R.drawable.ic_cleaning_bucket,
    iconBackground = Color.DarkGray,
    containerColor = Color.DarkGray.copy(.05f),
    clickable = false,
  ),
  ClientHomeActivityItemModel(
    activity = "Payment Due",
    drawableRes = R.drawable.ic_payment,
    iconBackground = WhiteFE.copy(.7f),
    containerColor = WhiteFE.copy(.2f),
  ),
)

internal val homeQuickActionUiModels = listOf(
  ClientHomeActivityItemModel(
    activity = "Send Payment Screenshot",
    drawableRes = R.drawable.ic_add_photo,
    iconBackground = WhiteFE.copy(.7f),
    containerColor = WhiteFE.copy(.2f),
  ),
)
