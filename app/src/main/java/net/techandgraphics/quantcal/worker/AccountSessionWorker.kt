package net.techandgraphics.quantcal.worker

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
      database.withTransaction { invoke() }
      Result.success()
    } catch (e: Exception) {
      e.printStackTrace()
      Result.retry()
    }
  }

  private suspend operator fun invoke() {
    authenticatorHelper.getAccount(accountManager)
      ?.let { account ->
        database.withTransaction {
          accountSessionRepository.purseData(
            accountSessionRepository.fetch(account.id),
          ) { _, _ -> }
        }
        return
      }
    throw IllegalStateException()
  }
}

private const val WORKER_UUID = "f31857cc-330d-41e8-b32d-960f82ff0b53"

fun Context.scheduleAccountSessionWorker() {
  val workRequest = OneTimeWorkRequestBuilder<AccountSessionWorker>()
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
