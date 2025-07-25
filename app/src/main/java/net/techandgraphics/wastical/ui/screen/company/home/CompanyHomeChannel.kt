package net.techandgraphics.wastical.ui.screen.company.home

import net.techandgraphics.wastical.data.local.database.payment.pay.PaymentEntity
import net.techandgraphics.wastical.data.remote.ApiResult
import java.io.File

sealed interface CompanyHomeChannel {
  sealed interface Payment : CompanyHomeChannel {
    data class Success(val payments: List<PaymentEntity>) : Payment
    data class Failure(val error: ApiResult.Error) : Payment
  }

  data class Export(val file: File) : CompanyHomeChannel

  sealed interface Goto : CompanyHomeChannel {
    data object Login : Goto
    data object Reload : Goto
  }

  sealed interface Fetch : CompanyHomeChannel {
    data object Fetching : Fetch
    data object Success : Fetch
    data class Error(val error: ApiResult.Error) : Fetch
  }
}
