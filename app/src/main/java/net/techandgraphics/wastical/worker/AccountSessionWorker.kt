package net.techandgraphics.wastical.worker

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
import net.techandgraphics.wastical.account.AuthenticatorHelper
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.data.local.database.account.session.AccountSessionRepository
import net.techandgraphics.wastical.getAccount
import net.techandgraphics.wastical.worker.WorkerUuid.ACCOUNT_SESSION_WORKER
import java.util.UUID
import java.util.concurrent.TimeUnit

@HiltWorker class AccountSessionWorker @AssistedInject constructor(
  @Assisted val context: Context,
  @Assisted params: WorkerParameters,
  private val database: AppDatabase,
  private val authenticatorHelper: AuthenticatorHelper,
  private val accountManager: AccountManager,
  private val repository: AccountSessionRepository,
) : CoroutineWorker(context, params) {

  override suspend fun doWork(): Result {
    return try {
      val account = authenticatorHelper.getAccount(accountManager)
      if (account != null) {
        repository.purseData(repository.fetch(account.id)) { _, _ -> }
        Result.success()
      } else {
        Result.failure()
      }
    } catch (e: Exception) {
      e.printStackTrace()
      Result.failure()
    }
  }
}

fun Context.scheduleAccountSessionWorker() {
  val workRequest = OneTimeWorkRequestBuilder<AccountSessionWorker>()
    .setConstraints(Constraints(requiredNetworkType = NetworkType.CONNECTED))
    .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
    .setId(UUID.fromString(ACCOUNT_SESSION_WORKER))
    .build()
  WorkManager.Companion
    .getInstance(this)
    .enqueueUniqueWork(
      uniqueWorkName = ACCOUNT_SESSION_WORKER,
      existingWorkPolicy = ExistingWorkPolicy.REPLACE,
      request = workRequest,
    )
}
