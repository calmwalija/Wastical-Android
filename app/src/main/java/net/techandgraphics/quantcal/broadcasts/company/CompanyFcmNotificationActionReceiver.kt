package net.techandgraphics.quantcal.broadcasts.company

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.techandgraphics.quantcal.data.local.database.toPaymentRequestEntity
import net.techandgraphics.quantcal.data.remote.account.HttpOperation
import net.techandgraphics.quantcal.data.remote.payment.PaymentStatus
import net.techandgraphics.quantcal.di.AppEntryPoint
import net.techandgraphics.quantcal.services.company.CompanyFcmNotificationAction
import net.techandgraphics.quantcal.worker.company.payment.fcm.CompanyFetchLatestPaymentWorker.Companion.PAYMENT_ID
import net.techandgraphics.quantcal.worker.company.payment.scheduleCompanyPaymentRequestWorker

class CompanyFcmNotificationActionReceiver : BroadcastReceiver() {

  private val coroutineScope = CoroutineScope(Dispatchers.IO + Job())

  override fun onReceive(context: Context, intent: Intent) {
    val appEntryPoint = EntryPointAccessors.fromApplication(context, AppEntryPoint::class.java)
    val database = appEntryPoint.appDatabase()
    val notificationManager =
      context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    when (intent.action) {
      CompanyFcmNotificationAction.Approve.name -> {
        val paymentId = intent.getLongExtra(PAYMENT_ID, -1L)
        if (paymentId == -1L) return
        coroutineScope.launch {
          try {
            val cachePayment = database
              .paymentDao
              .get(paymentId)
              .toPaymentRequestEntity(httpOperation = HttpOperation.Put)
              .copy(status = PaymentStatus.Approved.name)
            database.paymentRequestDao.insert(cachePayment)
            notificationManager.cancel(paymentId.toInt())
            context.scheduleCompanyPaymentRequestWorker()
          } catch (e: Exception) {
            e.printStackTrace()
          }
        }
      }

      CompanyFcmNotificationAction.Verify.name -> {
        Log.e("Notification", "Verify button clicked")
      }
    }
  }
}
