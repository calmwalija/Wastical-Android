package net.techandgraphics.wastemanagement.ui.screen.company.client.plan

import net.techandgraphics.wastemanagement.domain.model.payment.PaymentPlanUiModel

sealed interface CompanyClientPlanEvent {
  data class Load(val id: Long) : CompanyClientPlanEvent

  sealed interface Button : CompanyClientPlanEvent {
    data object Submit : Button
    data class ChangePlan(val plan: PaymentPlanUiModel) : Button
  }
}
