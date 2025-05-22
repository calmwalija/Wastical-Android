package net.techandgraphics.wastemanagement.ui.screen.home

import androidx.compose.ui.graphics.Color
import net.techandgraphics.wastemanagement.R
import net.techandgraphics.wastemanagement.data.Status
import net.techandgraphics.wastemanagement.data.local.database.account.AccountTitle
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentStatus
import net.techandgraphics.wastemanagement.domain.model.account.AccountUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentUiModel
import net.techandgraphics.wastemanagement.ui.screen.home.model.HomeActionUiModel
import net.techandgraphics.wastemanagement.ui.screen.home.model.HomeActivityUiModel
import net.techandgraphics.wastemanagement.ui.theme.WhiteFE
import java.time.ZonedDateTime

internal val homeActivityUiModels = listOf(
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

internal val homeActionUiModels = listOf(
  HomeActionUiModel(
    action = "Make Payment",
    drawableRes = R.drawable.ic_payments,
  ),
  HomeActionUiModel(
    action = "Waste Sorting Guide",
    drawableRes = R.drawable.ic_help,
    iconBackground = WhiteFE.copy(.7f),
    containerColor = WhiteFE.copy(.2f),
  ),
)

internal val account = AccountUiModel(
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

internal val payment = PaymentUiModel(
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
