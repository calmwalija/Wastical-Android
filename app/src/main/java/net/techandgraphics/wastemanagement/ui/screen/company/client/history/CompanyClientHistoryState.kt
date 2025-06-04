package net.techandgraphics.wastemanagement.ui.screen.company.client.history

import coil.ImageLoader
import net.techandgraphics.wastemanagement.domain.model.account.AccountUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentUiModel

sealed interface CompanyClientHistoryState {
  object Loading : CompanyClientHistoryState
  data class Success(
    val account: AccountUiModel,
    val imageLoader: ImageLoader,
    val payments: List<PaymentUiModel> = listOf(),
  ) : CompanyClientHistoryState
}
