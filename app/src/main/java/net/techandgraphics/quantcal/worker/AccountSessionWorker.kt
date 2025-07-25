package net.techandgraphics.quantcal.worker

import android.accounts.AccountManager
import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import net.techandgraphics.quantcal.account.AuthenticatorHelper
import net.techandgraphics.quantcal.data.local.database.AppDatabase
import net.techandgraphics.quantcal.data.local.database.account.session.AccountSessionRepository
import net.techandgraphics.quantcal.getAccount
import java.util.UUID
import java.util.concurrent.TimeUnit

@HiltWorker class AccountSessionWorker @AssistedInject constructor(
  @Assisted val context: Context,
  @Assisted params: WorkerParameters,
  private val database: AppDatabase,
  private val authenticatorHelper: AuthenticatorHelper,
  private val accountManager: AccountManager,
  private val accountSessionRepository: AccountSessionRepository,
) : CoroutineWorker(context, params) {

  override suspend fun doWork(): Result {
    return try {
      val account = authenticatorHelper.getAccount(accountManager)
      if (account != null) {
        accountSessionRepository.fetchSession()
        Result.success()
      } else {
        Result.failure()
      }
    } catch (e: Exception) {
      e.printStackTrace()
      Result.retry()
    }
  }
}

const val SESSION_WORKER_UUID = "f31857cc-330d-41e8-b32d-960f82ff0b53"

fun Context.scheduleAccountSessionWorker() {
  val workRequest = OneTimeWorkRequestBuilder<AccountSessionWorker>()
    .setConstraints(Constraints(requiredNetworkType = NetworkType.CONNECTED))
    .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
    .setId(UUID.fromString(SESSION_WORKER_UUID))
    .build()
  WorkManager.Companion
    .getInstance(this)
    .enqueueUniqueWork(
      uniqueWorkName = SESSION_WORKER_UUID,
      existingWorkPolicy = ExistingWorkPolicy.REPLACE,
      request = workRequest,
    )
}
