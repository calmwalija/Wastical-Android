package net.techandgraphics.wastical.services.client

import android.content.Context
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.data.remote.payment.PaymentApi
import net.techandgraphics.wastical.notification.NotificationType
import net.techandgraphics.wastical.worker.client.payment.fcm.scheduleClientFetchProofOfPaymentSubmittedByCompanyWorker
import net.techandgraphics.wastical.worker.client.payment.fcm.scheduleClientFetchProofOfPaymentWorker

class ClientFcmEvent(
  private val context: Context,
  private val coroutineScope: CoroutineScope,
  private val remoteMessage: RemoteMessage,
  private val database: AppDatabase,
  private val paymentApi: PaymentApi,
) {

  fun onEvent() = coroutineScope.launch {
    when {
      remoteMessage.data["event"]
        ?.contains(NotificationType.PROOF_OF_PAYMENT_SUBMITTED_BY_COMPANY.name) == true
      -> context.scheduleClientFetchProofOfPaymentSubmittedByCompanyWorker()

      remoteMessage.data["event"]
        ?.contains(NotificationType.PROOF_OF_PAYMENT_APPROVED.name) == true
      -> context.scheduleClientFetchProofOfPaymentWorker()

      remoteMessage.data["event"]
        ?.contains(NotificationType.PROOF_OF_PAYMENT_DECLINED.name) == true
      -> context.scheduleClientFetchProofOfPaymentWorker()

      remoteMessage.data["type"]
        ?.contains(NotificationType.COMPANY_BROADCAST_NOTIFICATION.name) == true
      -> {
        println(remoteMessage.data["title"])
        println(remoteMessage.data["body"])
        println(remoteMessage.data["type"])
      }

      remoteMessage.data["type"]
        ?.contains(NotificationType.ACCOUNT_BASED_NOTIFICATION.name) == true
      -> {
        println(remoteMessage.data["title"])
        println(remoteMessage.data["body"])
        println(remoteMessage.data["type"])
      }

      remoteMessage.data["type"]
        ?.contains(NotificationType.LOCATION_BASED_NOTIFICATION.name) == true
      -> {
        println(remoteMessage.data["title"])
        println(remoteMessage.data["body"])
        println(remoteMessage.data["type"])
      }
    }
  }
}
