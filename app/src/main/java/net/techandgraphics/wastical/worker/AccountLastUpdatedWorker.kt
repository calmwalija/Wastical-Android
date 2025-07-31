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
import com.google.common.net.HttpHeaders
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import net.techandgraphics.wastical.account.AuthenticatorHelper
import net.techandgraphics.wastical.data.local.Preferences
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.data.local.database.account.session.AccountSessionRepository
import net.techandgraphics.wastical.data.remote.LastUpdatedApi
import net.techandgraphics.wastical.data.remote.getLastUpdatedTimestamp
import net.techandgraphics.wastical.getAccount
import java.net.HttpURLConnection
import java.util.UUID
import java.util.concurrent.TimeUnit

@HiltWorker class AccountLastUpdatedWorker @AssistedInject constructor(
  @Assisted val context: Context,
  @Assisted params: WorkerParameters,
  private val database: AppDatabase,
  private val preferences: Preferences,
  private val accountManager: AccountManager,
  private val lastUpdatedApi: LastUpdatedApi,
  private val authenticatorHelper: AuthenticatorHelper,
  private val accountSessionRepository: AccountSessionRepository,
) : CoroutineWorker(context, params) {

  override suspend fun doWork(): Result {
    return try {
      val account = authenticatorHelper.getAccount(accountManager)
      if (account != null) {
        val lastUpdated = getLastUpdatedTimestamp(database, account.id)
        val response = lastUpdatedApi.since(account.id, lastUpdated)
        when (response.code()) {
          HttpURLConnection.HTTP_OK -> {
            response.body()?.let { serverResponse ->
              accountSessionRepository.purseData(serverResponse) { _, _ -> }
              val requestUrl = response.raw().request.url.toString()
              response.headers()[HttpHeaders.ETAG]
                ?.let { newEtag -> preferences.put<String>(requestUrl, newEtag) }
            }
            Result.retry()
          }

          HttpURLConnection.HTTP_NOT_MODIFIED -> Result.success()

          else -> throw IllegalStateException("HTTP_ERROR_CODE : ${response.code()}")
        }
      } else {
        Result.failure()
      }
    } catch (e: Exception) {
      e.printStackTrace()
      Result.retry()
    }
  }
}

const val LAST_UPDATED_WORKER_UUID = "f31857cc-330d-41e8-b32d-960f82ff0b53"

fun Context.scheduleAccountLastUpdatedWorker() {
  val workRequest = OneTimeWorkRequestBuilder<AccountLastUpdatedWorker>()
    .setConstraints(Constraints(requiredNetworkType = NetworkType.CONNECTED))
    .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
    .setId(UUID.fromString(LAST_UPDATED_WORKER_UUID))
    .build()
  WorkManager.Companion
    .getInstance(this)
    .enqueueUniqueWork(
      uniqueWorkName = LAST_UPDATED_WORKER_UUID,
      existingWorkPolicy = ExistingWorkPolicy.REPLACE,
      request = workRequest,
    )
}
