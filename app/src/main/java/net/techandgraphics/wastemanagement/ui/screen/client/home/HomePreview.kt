package net.techandgraphics.wastemanagement.ui.screen.client.home

import androidx.compose.ui.graphics.Color
import net.techandgraphics.wastemanagement.R
import net.techandgraphics.wastemanagement.data.Status
import net.techandgraphics.wastemanagement.data.local.database.account.AccountTitle
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentStatus
import net.techandgraphics.wastemanagement.domain.model.account.AccountUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentUiModel
import net.techandgraphics.wastemanagement.ui.screen.client.home.model.HomeActivityUiModel
import net.techandgraphics.wastemanagement.ui.screen.client.home.model.HomeQuickActionUiModel
import net.techandgraphics.wastemanagement.ui.theme.WhiteFE
import java.time.ZonedDateTime

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

internal val account4Preview = AccountUiModel(
  id = 1L,
  uuid = "",
  title = AccountTitle.DR,
  firstname = "Lorem",
  lastname = "Ipsum",
  username = "lorem.ipsum",
  email = "example@email.com",
  status = Status.Active,
  companyId = 1L,
  createdAt = System.currentTimeMillis(),
  leavingTimestamp = null,
  updatedAt = null,
)

internal val payment4Preview = PaymentUiModel(
  id = 1L,
  status = PaymentStatus.Approved,
  numberOfMonths = 1,
  transactionId = "TXN-5983-1747899108",
  paymentMethodId = 1L,
  createdAt = ZonedDateTime.now().toEpochSecond(),
  screenshotText = "Lorem",
  accountId = 1L,
  updatedAt = null,
)
