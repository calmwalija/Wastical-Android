package net.techandgraphics.wastemanagement.ui.screen.company.info.method

import net.techandgraphics.wastemanagement.domain.model.company.CompanyUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentMethodUiModel

sealed interface CompanyInfoMethodState {
  data object Loading : CompanyInfoMethodState
  data class Success(
    val company: CompanyUiModel,
    val methods: List<PaymentMethodUiModel> = listOf(),
  ) : CompanyInfoMethodState
}
