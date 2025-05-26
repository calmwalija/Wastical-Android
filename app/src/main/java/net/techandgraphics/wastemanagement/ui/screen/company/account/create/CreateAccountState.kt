package net.techandgraphics.wastemanagement.ui.screen.company.account.create

import net.techandgraphics.wastemanagement.data.local.database.account.AccountTitle
import net.techandgraphics.wastemanagement.domain.model.company.TrashCollectionScheduleUiModel
import net.techandgraphics.wastemanagement.domain.model.demographic.StreetUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentPlanUiModel
import net.techandgraphics.wastemanagement.ui.activity.main.activity.main.MainActivityState

data class CreateAccountState(
  val account: CreateAccountUiModel = CreateAccountUiModel(),
  val appState: MainActivityState = MainActivityState(),
)

data class CreateAccountUiModel(
  val title: AccountTitle = AccountTitle.MR,
  val firstname: String = "",
  val lastname: String = "",
  val contact: String = "",
  val altContact: String = "",
  val street: StreetUiModel? = null,
  val paymentPlan: PaymentPlanUiModel? = null,
  val tcSchedule: TrashCollectionScheduleUiModel? = null,
  val companyStreets: List<StreetUiModel> = listOf(),
)
