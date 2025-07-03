package net.techandgraphics.quantcal.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.room.withTransaction
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import net.techandgraphics.quantcal.asApproved
import net.techandgraphics.quantcal.data.local.database.AppDatabase
import net.techandgraphics.quantcal.data.local.database.toAccountContactEntity
import net.techandgraphics.quantcal.data.local.database.toAccountEntity
import net.techandgraphics.quantcal.data.local.database.toAccountPaymentPlanEntity
import net.techandgraphics.quantcal.data.local.database.toPaymentEntity
import net.techandgraphics.quantcal.data.local.database.toPaymentMonthCoveredEntity
import net.techandgraphics.quantcal.data.remote.account.AccountApi
import net.techandgraphics.quantcal.data.remote.payment.PaymentApi
import net.techandgraphics.quantcal.data.remote.toAccountPaymentPlanRequest
import net.techandgraphics.quantcal.data.remote.toAccountRequest
import net.techandgraphics.quantcal.data.remote.toPaymentRequest
import net.techandgraphics.quantcal.domain.toAccountUiModel
import net.techandgraphics.quantcal.notification.NotificationBuilder
import net.techandgraphics.quantcal.notification.NotificationType
import net.techandgraphics.quantcal.notification.NotificationUiModel
import net.techandgraphics.quantcal.toFullName
import java.util.concurrent.TimeUnit

@HiltWorker class AppWorker @AssistedInject constructor(
  @Assisted val context: Context,
  @Assisted params: WorkerParameters,
  private val database: AppDatabase,
  private val paymentApi: PaymentApi,
  private val accountApi: AccountApi,
) : CoroutineWorker(context, params) {
  override suspend fun doWork(): Result {
    return try {
      database.withTransaction {
        accountRequestDao()
        accountPaymentPlanRequestDao()
        paymentRequestDao()
      }
      Result.success()
    } catch (e: Exception) {
      e.printStackTrace()
      Result.retry()
    }
  }

  private suspend fun accountPaymentPlanRequestDao() {
    database.accountPaymentPlanRequestDao.query()
      .onEach {
        val newPlan = accountApi.plan(it.paymentPlanId, it.toAccountPaymentPlanRequest())
          .toAccountPaymentPlanEntity()
        database.accountPaymentPlanDao.update(newPlan)
        database.accountPaymentPlanRequestDao.delete(it)
      }
  }

  private suspend fun paymentRequestDao() {
    database.paymentRequestDao.query().onEach { paymentRequest ->
      val request = paymentRequest.toPaymentRequest().asApproved()
      val newValue = paymentApi.pay(request)
      newValue.payments?.onEach { payment ->
        database.paymentDao.upsert(payment.toPaymentEntity())
        val account = database.accountDao.get(payment.accountId).toAccountUiModel()
        newValue.paymentMonthsCovered
          ?.map { it.toPaymentMonthCoveredEntity() }
          ?.onEach { database.paymentMonthCoveredDao.insert(it) }
        database.paymentRequestDao.delete(paymentRequest)
        val notification = NotificationUiModel(
          type = NotificationType.PaymentRecorded,
          title = "Payment Recorded Successfully",
          body = "Your payment with ${payment.transactionId} for ${account.toFullName()} was successfully recorded.",
        )
        val builder = NotificationBuilder(context)
        builder.show(notification)
      }
    }
  }

  private suspend fun accountRequestDao() {
    database.accountRequestDao.query().forEach { accountRequestEntity ->
      val account = accountRequestEntity.toAccountRequest()
      val newAccount = accountApi.create(account)
      database.withTransaction {
        newAccount.accounts
          ?.map { it.toAccountEntity() }
          ?.also { database.accountDao.insert(it) }

        newAccount.accountContacts
          ?.map { it.toAccountContactEntity() }
          ?.also { database.accountContactDao.insert(it) }

        newAccount.accountPaymentPlans
          ?.map { it.toAccountPaymentPlanEntity() }
          ?.also { database.accountPaymentPlanDao.insert(it) }

        val oldAccount = database.accountDao.get(accountRequestEntity.id)
        database.accountDao.delete(oldAccount)
      }
    }
  }
}

fun Context.scheduleAppWorker() {
  val workRequest = OneTimeWorkRequestBuilder<AppWorker>()
    .setConstraints(Constraints(requiredNetworkType = NetworkType.CONNECTED))
    .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
    .build()
  WorkManager.Companion.getInstance(this).enqueue(workRequest)
}
