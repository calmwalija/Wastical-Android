package net.techandgraphics.wastemanagement.services

import androidx.core.app.NotificationCompat
import androidx.room.withTransaction
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.techandgraphics.wastemanagement.calculate
import net.techandgraphics.wastemanagement.data.local.database.AccountRole
import net.techandgraphics.wastemanagement.data.local.database.AppDatabase
import net.techandgraphics.wastemanagement.data.local.database.account.token.AccountFcmTokenEntity
import net.techandgraphics.wastemanagement.data.local.database.payment.pay.PaymentEntity
import net.techandgraphics.wastemanagement.data.local.database.toPaymentEntity
import net.techandgraphics.wastemanagement.data.remote.account.ACCOUNT_ID
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentApi
import net.techandgraphics.wastemanagement.defaultDateTime
import net.techandgraphics.wastemanagement.domain.toAccountUiModel
import net.techandgraphics.wastemanagement.domain.toPaymentUiModel
import net.techandgraphics.wastemanagement.getTimeOfDay
import net.techandgraphics.wastemanagement.notification.NotificationBuilder
import net.techandgraphics.wastemanagement.notification.NotificationType
import net.techandgraphics.wastemanagement.notification.NotificationUiModel
import net.techandgraphics.wastemanagement.toFullName
import net.techandgraphics.wastemanagement.toZonedDateTime
import javax.inject.Inject

@AndroidEntryPoint
class FcmService : FirebaseMessagingService() {

  @Inject lateinit var database: AppDatabase

  @Inject lateinit var paymentApi: PaymentApi

  private val coroutineScope = CoroutineScope(Dispatchers.IO + Job())
  private val accountRole = if (ACCOUNT_ID == 1L) AccountRole.Client else AccountRole.Company

  /**
   Based on [net.techandgraphics.wastemanagement.data.local.database.account.AccountEntity]
   logged in [net.techandgraphics.wastemanagement.data.local.database.AccountRole]
   */

  private suspend fun onVerificationEvent(payments: List<PaymentEntity>) {
    when (accountRole) {
      AccountRole.Client -> onClientRole(payments)
      AccountRole.Company -> onCompanyRole(payments)
    }
  }

  private suspend fun onClientRole(payments: List<PaymentEntity>) = payments.forEach { newValue ->

    val account = database
      .accountDao
      .get(newValue.accountId)
      .toAccountUiModel()

    val notification = NotificationUiModel(
      type = NotificationType.PaymentVerification,
      title = "Payment Screenshot Feedback",
      body = "Your payment screenshot with ${newValue.transactionId} has been ${newValue.status}.",
      style = NotificationCompat.BigTextStyle().bigText(
        "Good ${getTimeOfDay()} ${account.toFullName()}, your payment with ${newValue.transactionId} " +
          "you send for verification has been ${newValue.status}. " +
          "Thank you for your patience.",
      ),
    )
    val builder = NotificationBuilder(this)
    builder.show(notification)
  }

  private suspend fun onCompanyRole(payments: List<PaymentEntity>) = payments
    .map { it.toPaymentUiModel() }
    .forEach { payment ->
      val account = database
        .accountDao
        .get(payment.accountId)
        .toAccountUiModel()

      val gateway = database
        .paymentGatewayDao
        .get(payment.paymentGatewayId)

      val notification = NotificationUiModel(
        type = NotificationType.PaymentVerification,
        title = "Payment Screenshot Request Verification",
        body = "${account.toFullName()} has sent a payment request",
        style = NotificationCompat.BigTextStyle().bigText(
          "${account.toFullName()} has sent a payment request of ${payment.calculate()} " +
            "using ${gateway.name} on ${payment.updatedAt.toZonedDateTime().defaultDateTime()}",
        ),
      )
      val builder = NotificationBuilder(this)
      builder.show(notification)
    }

  override fun onMessageReceived(p0: RemoteMessage) {
    if (p0.data["event"]?.contains("verify") == true) {
      coroutineScope.launch {
        val epochSecond = database.paymentDao.getByUpdatedAtLatest()?.updatedAt ?: -1
        runCatching {
          database.withTransaction {
            paymentApi.fetchLatest(ACCOUNT_ID, epochSecond)
              .map { it.toPaymentEntity() }
              .also { database.paymentDao.upsert(it) }
          }
        }.onSuccess { onVerificationEvent(it) }
      }
    }
    super.onMessageReceived(p0)
  }

  override fun onNewToken(token: String) {
    coroutineScope.launch {
      database.accountFcmTokenDao.upsert(AccountFcmTokenEntity(token = token))
    }
    super.onNewToken(token)
  }
}
