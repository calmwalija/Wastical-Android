package net.techandgraphics.wastical.ui.screen.company.client.profile

import net.techandgraphics.wastical.R

data class ProfileItem(
  val title: String,
  val drawableRes: Int = 1,
  val event: CompanyClientProfileEvent.Option,
  val badgeCount: Int = 0,
)

val billingItems = listOf(
  ProfileItem(
    title = "Payment Invoice",
    drawableRes = R.drawable.ic_invoice,
    event = CompanyClientProfileEvent.Option.Invoice,
  ),
  ProfileItem(
    title = "Payment History",
    drawableRes = R.drawable.ic_history,
    event = CompanyClientProfileEvent.Option.History,
  ),
  ProfileItem(
    title = "Pending Payments",
    drawableRes = R.drawable.ic_upload_ready,
    event = CompanyClientProfileEvent.Option.Pending,
  ),
)

val actionItems = listOf(
  ProfileItem(
    title = "Edit Profile",
    drawableRes = R.drawable.ic_edit_note,
    event = CompanyClientProfileEvent.Option.Info,
  ),
  ProfileItem(
    title = "Proof Of Payment",
    drawableRes = R.drawable.ic_payment,
    event = CompanyClientProfileEvent.Option.Payment,
  ),
  ProfileItem(
    title = "Payment Plan",
    drawableRes = R.drawable.ic_payments,
    event = CompanyClientProfileEvent.Option.Plan,
  ),
  ProfileItem(
    title = "Change Location",
    drawableRes = R.drawable.ic_house,
    event = CompanyClientProfileEvent.Option.Location,
  ),
  ProfileItem(
    title = "Remove Client",
    drawableRes = R.drawable.ic_person_cancel,
    event = CompanyClientProfileEvent.Option.Revoke,
  ),
)
