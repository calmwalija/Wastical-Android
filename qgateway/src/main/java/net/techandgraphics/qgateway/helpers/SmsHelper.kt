package net.techandgraphics.qgateway.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.telephony.SmsManager
import android.telephony.SubscriptionManager

object SmsHelper {
  private fun getSubscriptionsManager(context: Context): SubscriptionManager =
    context.getSystemService(SubscriptionManager::class.java)

  enum class SentStatus { Success, Error, Slot }

  @SuppressLint("MissingPermission")
  @Suppress("DEPRECATION")
  fun send(
    context: Context,
    contact: String,
    message: String,
    simSlotIndex: Int = 0,
    onEvent: (SentStatus) -> Unit,
  ) {
    try {
      val subscriptionInfoList = getSubscriptionsManager(context).activeSubscriptionInfoList
      if (subscriptionInfoList != null && simSlotIndex < subscriptionInfoList.size) {
        val subscriptionId = subscriptionInfoList[simSlotIndex].subscriptionId
        SmsManager
          .getSmsManagerForSubscriptionId(subscriptionId)
          .sendTextMessage(
            contact,
            null,
            message,
            null,
            null,
          )
        onEvent(SentStatus.Success)
      } else {
        onEvent(SentStatus.Slot)
      }
    } catch (e: Exception) {
      e.printStackTrace()
      onEvent(SentStatus.Error)
    }
  }
}
