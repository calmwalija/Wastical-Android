package net.techandgraphics.wastemanagement.ui.screen.company.info

import net.techandgraphics.wastemanagement.R

data class CompanyInfoItem(
  val title: String,
  val drawableRes: Int = 1,
  val event: CompanyInfoEvent = CompanyInfoEvent.Tap,
)

val companyInfoItems = listOf(
  CompanyInfoItem(
    title = "Edit Info",
    drawableRes = R.drawable.ic_account,
  ),
  CompanyInfoItem(
    title = "Payment Plan",
    drawableRes = R.drawable.ic_payments,
  ),
  CompanyInfoItem(
    title = "Payment Method",
    drawableRes = R.drawable.ic_method,
  ),
)
