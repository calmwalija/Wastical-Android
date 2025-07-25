package net.techandgraphics.wcompanion.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import dagger.hilt.android.EntryPointAccessors
import net.techandgraphics.wcompanion.di.AppEntryPoint

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
      }
    }
  }
}
