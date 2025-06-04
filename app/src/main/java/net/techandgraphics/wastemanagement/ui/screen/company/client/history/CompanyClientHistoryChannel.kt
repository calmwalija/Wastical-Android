package net.techandgraphics.wastemanagement.ui.screen.company.client.history

import net.techandgraphics.wastemanagement.data.local.database.payment.pay.PaymentEntity
import net.techandgraphics.wastemanagement.data.remote.ApiResult

sealed interface CompanyClientHistoryChannel {
  sealed interface Payment : CompanyClientHistoryChannel {
    data class Success(val payments: List<PaymentEntity>) : Payment
    data class Failure(val error: ApiResult.Error) : Payment
  }
}
