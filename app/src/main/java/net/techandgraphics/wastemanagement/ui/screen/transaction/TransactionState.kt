package net.techandgraphics.wastemanagement.ui.screen.transaction

import net.techandgraphics.wastemanagement.R
import net.techandgraphics.wastemanagement.ui.screen.home.model.TransactionUiModel

data class TransactionState(
  val id: Int = 0,
  val transactionUiModels: List<TransactionUiModel> = theTransactionUiModels,
)

private val theModels = listOf(
  TransactionUiModel(
    paymentMethod = "Airtel Money",
    numberOfMonths = 2,
    drawableRes = R.drawable.im_airtel_money_logo,
  ),
  TransactionUiModel(
    paymentMethod = "National Bank",
    numberOfMonths = 3,
    drawableRes = R.drawable.im_national_bank_logo,
  ),
  TransactionUiModel(
    paymentMethod = "TNM Mpamba",
    numberOfMonths = 4,
    drawableRes = R.drawable.im_tnm_mpamba,
  ),
)

private val theTransactionUiModels = (theModels + theModels + theModels + theModels).shuffled()
