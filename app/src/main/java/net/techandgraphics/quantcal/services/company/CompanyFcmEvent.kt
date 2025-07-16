package net.techandgraphics.quantcal.services.company

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.room.withTransaction
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.techandgraphics.quantcal.R
import net.techandgraphics.quantcal.broadcasts.company.CompanyFcmNotificationActionReceiver
import net.techandgraphics.quantcal.data.local.database.AppDatabase
import net.techandgraphics.quantcal.data.local.database.payment.pay.PaymentEntity
import net.techandgraphics.quantcal.data.local.database.toPaymentEntity
import net.techandgraphics.quantcal.data.local.database.toPaymentMonthCoveredEntity
import net.techandgraphics.quantcal.data.remote.account.ACCOUNT_ID
import net.techandgraphics.quantcal.data.remote.mapApiError
import net.techandgraphics.quantcal.data.remote.payment.PaymentApi
import net.techandgraphics.quantcal.defaultDateTime
import net.techandgraphics.quantcal.domain.toAccountUiModel
import net.techandgraphics.quantcal.domain.toPaymentUiModel
import net.techandgraphics.quantcal.notification.NotificationBuilder
import net.techandgraphics.quantcal.notification.NotificationType
import net.techandgraphics.quantcal.notification.NotificationUiModel
import net.techandgraphics.quantcal.toAmount
import net.techandgraphics.quantcal.toFullName
import net.techandgraphics.quantcal.toZonedDateTime

class CompanyFcmEvent(
  private val context: Context,
  private val coroutineScope: CoroutineScope,
  private val remoteMessage: RemoteMessage,
  private val database: AppDatabase,
  private val paymentApi: PaymentApi,
) {

  companion object FcmEventConst {
    const val PAYMENT_ID = "paymentId"
  }

  private suspend fun List<PaymentEntity>.onVerificationEvent() {
    map { it.toPaymentUiModel() }
      .forEach { payment ->
        val account = database.accountDao.get(payment.accountId).toAccountUiModel()
        val method = database.paymentMethodDao.get(payment.paymentMethodId)
        val plan = database.paymentPlanDao.get(method.paymentPlanId)
        val gateway = database.paymentGatewayDao.get(method.paymentGatewayId)
        val months = database.paymentMonthCoveredDao.getByPaymentId(payment.id)
        val notification = NotificationUiModel(
          type = NotificationType.PaymentVerification,
          title = "Payment Request Verification",
          body = "${account.toFullName()} has sent a payment request",
          style = NotificationCompat.BigTextStyle().bigText(
            "${account.toFullName()} has sent a payment request " +
              "of ${months.size.times(plan.fee).toAmount()} " +
              "using ${gateway.name} " +
              "on ${payment.updatedAt.toZonedDateTime().defaultDateTime()}.",
          ),
        )

        val intent = Intent(context, CompanyFcmNotificationActionReceiver::class.java)
        intent.putExtra(PAYMENT_ID, payment.id)

        val approveIntent = PendingIntent.getBroadcast(
          context,
          0,
          intent.also { it.action = CompanyFcmNotificationAction.Approve.name },
          PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val verifyIntent = PendingIntent.getBroadcast(
          context,
          1,
          intent.also { it.action = CompanyFcmNotificationAction.Verify.name },
          PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        NotificationBuilder(context)
          .withActions(
            NotificationCompat.Action(R.drawable.ic_check_circle, "Approve", approveIntent),
            NotificationCompat.Action(R.drawable.ic_eye, "View", verifyIntent),
            notification = notification,
            notificationId = payment.id,
          )
      }
  }

  private suspend fun onVerify() {
    val epochSecond = database.paymentDao.getByUpdatedAtLatest()?.updatedAt ?: -1
    runCatching { paymentApi.fetchLatest(ACCOUNT_ID, epochSecond) }
      .onSuccess { responses ->
        database.withTransaction {
          responses.payments?.map { it.toPaymentEntity() }
            ?.let { payments ->
              database.paymentDao.insert(payments)
              responses.paymentMonthsCovered
                ?.map { it.toPaymentMonthCoveredEntity() }
                ?.also {
                  database.paymentMonthCoveredDao.insert(it)
                }
              payments.onVerificationEvent()
            }
        }
      }.onFailure { println(mapApiError(it)) }
  }

  fun onEvent() = coroutineScope.launch {
    when {
      remoteMessage.data["event"]
        ?.contains("verify") == true -> {
        onVerify()
      }
    }
  }
}
