package net.techandgraphics.quantcal.ui.screen.company.home

import net.techandgraphics.quantcal.data.local.database.payment.pay.PaymentEntity
import net.techandgraphics.quantcal.data.remote.ApiResult
import java.io.File

sealed interface CompanyHomeChannel {
  sealed interface Payment : CompanyHomeChannel {
    data class Success(val payments: List<PaymentEntity>) : Payment
    data class Failure(val error: ApiResult.Error) : Payment
  }

  data class Export(val file: File) : CompanyHomeChannel

  sealed interface Import : CompanyHomeChannel {
    enum class Status { Wait, Invalid, Error, Success }
    data class Data(val status: Status) : Import
    data class Progress(val total: Int, val current: Int) : Import
  }
}
