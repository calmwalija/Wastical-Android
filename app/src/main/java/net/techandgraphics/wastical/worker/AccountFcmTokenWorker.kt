package net.techandgraphics.wastical.worker

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
import net.techandgraphics.wastical.account.AuthenticatorHelper
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.data.local.database.toAccountFcmTokenEntity
import net.techandgraphics.wastical.data.remote.account.AccountApi
import net.techandgraphics.wastical.data.remote.toAccountFcmTokenRequest
import net.techandgraphics.wastical.getAccount
import java.util.UUID
import java.util.concurrent.TimeUnit

@HiltWorker class AccountFcmTokenWorker @AssistedInject constructor(
  @Assisted val context: Context,
  @Assisted params: WorkerParameters,
  private val database: AppDatabase,
  private val accountApi: AccountApi,
  private val authenticatorHelper: AuthenticatorHelper,
  private val accountManager: AccountManager,
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
        database.accountFcmTokenDao.query()
          .filterNot { it.sync.not() }
          .map { it.toAccountFcmTokenRequest(account.id) }
          .onEach {
            val fcmTokenResponse = accountApi.fcmToken(it)
            database.withTransaction {
              with(database.accountFcmTokenDao) {
                deleteAll()
                insert(fcmTokenResponse.toAccountFcmTokenEntity())
              }
            }
          }
        return
      }
    throw IllegalStateException()
  }
}

private const val WORKER_UUID = "f21f75c5-ffa6-4187-b522-1d47fe1ba930"

fun Context.scheduleAccountFcmTokenWorker() {
  val workRequest = OneTimeWorkRequestBuilder<AccountFcmTokenWorker>()
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
