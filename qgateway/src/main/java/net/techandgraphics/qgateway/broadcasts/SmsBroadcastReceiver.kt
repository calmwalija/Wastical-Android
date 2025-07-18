package net.techandgraphics.qgateway.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.techandgraphics.qgateway.data.local.database.sms.SmsEntity
import net.techandgraphics.qgateway.di.AppEntryPoint
import net.techandgraphics.qgateway.worker.scheduleSmsWorker

class SmsBroadcastReceiver : BroadcastReceiver() {

  override fun onReceive(context: Context, intent: Intent?) {
    if (intent?.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
      val appEntryPoint = EntryPointAccessors.fromApplication(context, AppEntryPoint::class.java)
      val database = appEntryPoint.aQgatewayDatabase()
      val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
      for (msg in messages) {
        msg.originatingAddress ?: "unknown"
        val message = msg.messageBody
        val asList = message.split(":")
        println("onReceive: $message")
        if (asList.size != 4) return
        runCatching {
          SmsEntity(
            message = message,
            uuid = asList[0],
            hashable = asList[1],
            timestamp = asList[2].toLong(),
            contact = asList[3],
          )
        }.onSuccess { sms ->
          CoroutineScope(Dispatchers.IO + Job()).launch {
            database.smsDao.insert(sms)
            context.scheduleSmsWorker()
          }
        }.onFailure { it.printStackTrace() }
      }
    }
  }
}
