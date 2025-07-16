package net.techandgraphics.quantcal.services

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.techandgraphics.quantcal.data.local.database.AppDatabase
import net.techandgraphics.quantcal.data.local.database.account.token.AccountFcmTokenEntity
import net.techandgraphics.quantcal.worker.scheduleAccountFcmTokenWorker
import javax.inject.Inject

@AndroidEntryPoint
class FcmService : FirebaseMessagingService() {

  @Inject lateinit var database: AppDatabase

  private val coroutineScope = CoroutineScope(Dispatchers.IO + Job())

  override fun onMessageReceived(p0: RemoteMessage) {
    super.onMessageReceived(p0)
  }

  override fun onNewToken(token: String) {
    coroutineScope.launch {
      database.accountFcmTokenDao.upsert(AccountFcmTokenEntity(token = token))
      scheduleAccountFcmTokenWorker()
    }
    super.onNewToken(token)
  }
}
