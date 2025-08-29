package net.techandgraphics.wastical.worker.client.payment

import android.accounts.AccountManager
import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.room.withTransaction
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.google.gson.Gson
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import net.techandgraphics.wastical.account.AuthenticatorHelper
import net.techandgraphics.wastical.asVerifying
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.data.local.database.payment.pay.request.PaymentRequestEntity
import net.techandgraphics.wastical.data.local.database.toNotificationEntity
import net.techandgraphics.wastical.data.local.database.toPaymentEntity
import net.techandgraphics.wastical.data.local.database.toPaymentMonthCoveredEntity
import net.techandgraphics.wastical.data.remote.payment.PaymentApi
import net.techandgraphics.wastical.data.remote.payment.PaymentType
import net.techandgraphics.wastical.data.remote.toPaymentRequest
import net.techandgraphics.wastical.domain.model.account.AccountUiModel
import net.techandgraphics.wastical.getAccount
import net.techandgraphics.wastical.getProofFile
import net.techandgraphics.wastical.getProofFileWithExtension
import net.techandgraphics.wastical.getUCropFile
import net.techandgraphics.wastical.notification.pendingIntent
import net.techandgraphics.wastical.toAmount
import net.techandgraphics.wastical.worker.workerShowNotification
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.UUID
import java.util.concurrent.TimeUnit

@HiltWorker class ClientPaymentRequestWorker @AssistedInject constructor(
  @Assisted val context: Context,
  @Assisted params: WorkerParameters,
  private val database: AppDatabase,
  private val paymentApi: PaymentApi,
  private val gson: Gson,
  private val authenticatorHelper: AuthenticatorHelper,
  private val accountManager: AccountManager,
) : CoroutineWorker(context, params) {

  override suspend fun doWork(): Result {
    return authenticatorHelper.getAccount(accountManager)
      ?.let { account ->
        try {
          database.paymentRequestDao.query().forEach { paymentRequest ->
            processPayment(paymentRequest, account)
          }
          Result.success()
        } catch (e: Exception) {
          e.printStackTrace()
          Result.retry()
        }
      } ?: Result.retry()
  }

  private suspend fun processPayment(
    paymentRequest: PaymentRequestEntity,
    account: AccountUiModel,
  ) {
    val request = paymentRequest.toPaymentRequest().asVerifying()
    val method = database.paymentMethodDao.get(request.paymentMethodId)
    val gateway = database.paymentGatewayDao.get(method.paymentGatewayId)

    val newValue = when (PaymentType.valueOf(gateway.type)) {
      PaymentType.Cash -> paymentApi.pay(request)
      else -> {
        val jsonRequestBody = gson.toJson(request)
          .toRequestBody(contentType = "application/json; charset=utf-8".toMediaType())
        val file = paymentRequest.proofExt?.let { ext ->
          context.getProofFileWithExtension(paymentRequest.createdAt, ext)
        } ?: (
          context.getProofFile(paymentRequest.createdAt)
            ?: context.getUCropFile(paymentRequest.createdAt)
          )
        val mime = when (paymentRequest.proofExt?.lowercase()) {
          "pdf" -> "application/pdf"
          "jpg", "jpeg", "png" -> "image/*"
          else -> if (file.name.endsWith(
              ".pdf",
              ignoreCase = true,
            )
          ) {
            "application/pdf"
          } else {
            "image/*"
          }
        }
        val fileRequestBody = file.asRequestBody(mime.toMediaType())
        val multipartFile = MultipartBody.Part.createFormData(
          name = "file",
          filename = file.name,
          body = fileRequestBody,
        )
        paymentApi.payWithScreenshot(multipartFile, jsonRequestBody)
      }
    }

    database.withTransaction {
      newValue.payments?.onEach { payment ->
        database.paymentDao.upsert(payment.toPaymentEntity())
        newValue.paymentMonthsCovered
          ?.map { it.toPaymentMonthCoveredEntity() }
          ?.onEach { database.paymentMonthCoveredDao.upsert(it) }
        database.paymentRequestDao.delete(paymentRequest)
      }
      newValue.notifications?.map { notificationResponse ->
        notificationResponse.toNotificationEntity()
      }?.forEach { notification ->
        val plan = database.paymentPlanDao.get(method.paymentPlanId)
        val theAmount = paymentRequest.months.times(plan.fee).toAmount()
        val theBody =
          "Your proof of payment of $theAmount has been submitted and awaits verification."
        val bigText =
          "$theBody We'll notify you once the verification is complete. Thank you for your patience."
        val newNotification = notification.copy(
          title = bigText,
          body = theBody,
        )
        database.notificationDao.insert(newNotification)
      }
    }
    database.workerShowNotification(
      context,
      account,
      pendingIntent = pendingIntent(context, GOTO_NOTIFICATION),
    )
  }
}

const val GOTO_NOTIFICATION = "GOTO_NOTIFICATION"
const val INTENT_EXTRA_GOTO = "INTENT_EXTRA_GOTO"

private const val WORKER_UUID = "0add6fac-665b-4190-a10e-f1f5e72a09a4"

fun Context.scheduleClientPaymentRequestWorker() {
  val workRequest = OneTimeWorkRequestBuilder<ClientPaymentRequestWorker>()
    .setConstraints(Constraints(requiredNetworkType = NetworkType.CONNECTED))
    .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
    .setId(UUID.fromString(WORKER_UUID))
    .build()
  WorkManager.Companion
    .getInstance(this)
    .enqueueUniqueWork(
      uniqueWorkName = WORKER_UUID,
      existingWorkPolicy = ExistingWorkPolicy.REPLACE,
      request = workRequest,
    )
}
