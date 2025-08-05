package net.techandgraphics.wastical.services.company

import android.content.Context
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.data.remote.payment.PaymentApi
import net.techandgraphics.wastical.notification.NotificationType
import net.techandgraphics.wastical.worker.company.payment.fcm.scheduleCompanyFetchLatestPaymentWorker

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
        ?.contains(NotificationType.PROOF_OF_PAYMENT_COMPANY_VERIFY.name) == true -> {
        context.scheduleCompanyFetchLatestPaymentWorker()
      }
    }
  }
}
