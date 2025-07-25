package net.techandgraphics.wcompanion.services

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.techandgraphics.wcompanion.data.local.database.QgatewayDatabase
import net.techandgraphics.wcompanion.data.local.database.token.FcmTokenEntity
import net.techandgraphics.wcompanion.worker.scheduleFcmTokenWorker
import javax.inject.Inject

@AndroidEntryPoint
class FcmService : FirebaseMessagingService() {

  @Inject lateinit var database: QgatewayDatabase

  private val coroutineScope = CoroutineScope(Dispatchers.IO + Job())

  override fun onMessageReceived(remoteMessage: RemoteMessage) {
    TokenFcmEvent(this, coroutineScope, remoteMessage).onEvent()
    super.onMessageReceived(remoteMessage)
  }

  override fun onNewToken(token: String) {
    coroutineScope.launch {
      database.fcmTokenDao.upsert(FcmTokenEntity(token = token))
      Log.e("TAG", "onNewToken: 🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥" + token)
      scheduleFcmTokenWorker()
    }
    super.onNewToken(token)
  }
}
