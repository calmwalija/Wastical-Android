package net.techandgraphics.wastemanagement.ui.screen.company.client.profile

import net.techandgraphics.wastemanagement.R

data class ProfileItem(
  val title: String,
  val drawableRes: Int = 1,
  val event: CompanyClientProfileEvent = CompanyClientProfileEvent.It,
)

val profileItems = listOf(
  ProfileItem(
    title = "Payment Plan",
    drawableRes = R.drawable.ic_payments,
  ),
  ProfileItem(
    title = "Payment History",
    drawableRes = R.drawable.ic_history,
  ),
  ProfileItem(
    title = "Change Location",
    drawableRes = R.drawable.ic_house,
  ),
  ProfileItem(
    title = "Reset Password",
    drawableRes = R.drawable.ic_password,
  ),
  ProfileItem(
    title = "Revoke & Archive Account",
    drawableRes = R.drawable.ic_revoke,
  ),
)
