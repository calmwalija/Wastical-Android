package net.techandgraphics.wastical.ui.screen.company.client.plan

import net.techandgraphics.wastical.domain.model.payment.PaymentPlanUiModel

sealed interface CompanyClientPlanEvent {
  data class Load(val id: Long) : CompanyClientPlanEvent

  sealed interface Button : CompanyClientPlanEvent {
    data object Submit : Button
    data class ChangePlan(val plan: PaymentPlanUiModel) : Button
    data class Phone(val contact: String) : Button
  }

  sealed interface Goto : CompanyClientPlanEvent {
    data object BackHandler : CompanyClientPlanEvent
    data class Location(val id: Long) : Goto
  }
}
