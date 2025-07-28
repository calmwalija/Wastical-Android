package net.techandgraphics.wastical.ui.screen.company.info

import net.techandgraphics.wastical.R

data class CompanyInfoItem(
  val title: String,
  val drawableRes: Int = 1,
  val event: CompanyInfoEvent,
)

val companyInfoItems = listOf(
  CompanyInfoItem(
    title = "Edit Info",
    drawableRes = R.drawable.ic_edit_note,
    event = CompanyInfoEvent.Goto.Edit,
  ),
  CompanyInfoItem(
    title = "Payment Plan",
    drawableRes = R.drawable.ic_payments,
    event = CompanyInfoEvent.Goto.Plan,
  ),
  CompanyInfoItem(
    title = "Payment Method",
    drawableRes = R.drawable.ic_method,
    event = CompanyInfoEvent.Goto.Method,
  ),
)
