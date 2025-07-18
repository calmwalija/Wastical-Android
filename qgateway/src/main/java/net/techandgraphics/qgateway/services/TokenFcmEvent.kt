package net.techandgraphics.qgateway.services

import android.content.Context
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.techandgraphics.qgateway.worker.scheduleOtpWorker

class TokenFcmEvent(
  private val context: Context,
  private val coroutineScope: CoroutineScope,
  private val remoteMessage: RemoteMessage,
) {
  fun onEvent() = coroutineScope.launch {
    when {
      remoteMessage.data["event"]
        ?.contains("opt") == true -> {
        context.scheduleOtpWorker()
      }
    }
  }
}
