package net.techandgraphics.wastemanagement.ui.screen.company.home

import net.techandgraphics.wastemanagement.data.local.database.payment.pay.PaymentEntity
import net.techandgraphics.wastemanagement.data.remote.ApiResult

sealed interface CompanyHomeChannel {
  sealed interface Payment : CompanyHomeChannel {
    data class Success(val payments: List<PaymentEntity>) : Payment
    data class Failure(val error: ApiResult.Error) : Payment
  }
}
