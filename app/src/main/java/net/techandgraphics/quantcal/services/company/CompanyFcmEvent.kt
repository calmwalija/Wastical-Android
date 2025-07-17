package net.techandgraphics.quantcal.services.company

import android.content.Context
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.techandgraphics.quantcal.data.local.database.AppDatabase
import net.techandgraphics.quantcal.data.remote.payment.PaymentApi
import net.techandgraphics.quantcal.worker.company.payment.fcm.scheduleCompanyFetchLatestPaymentWorker

class CompanyFcmEvent(
  private val context: Context,
  private val coroutineScope: CoroutineScope,
  private val remoteMessage: RemoteMessage,
  private val database: AppDatabase,
  private val paymentApi: PaymentApi,
) {

  fun onEvent() = coroutineScope.launch {
    when {
      remoteMessage.data["event"]
        ?.contains("verify") == true -> {
        context.scheduleCompanyFetchLatestPaymentWorker()
      }
    }
  }
}
