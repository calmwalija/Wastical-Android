@file:Suppress("DEPRECATION")

package net.techandgraphics.wastemanagement.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

class SmsBroadcastReceiver : BroadcastReceiver() {

  private var otpReceiver: OTPReceiveListener? = null

  fun initOTPListener(receiver: OTPReceiveListener) {
    this.otpReceiver = receiver
  }

  override fun onReceive(context: Context, intent: Intent) {
    if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
      val extras = intent.extras
      val status = extras?.get(SmsRetriever.EXTRA_STATUS) as Status

      when (status.statusCode) {
        CommonStatusCodes.SUCCESS -> {
          var otp: String = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String
          Log.e("OTP_Message", otp)
          otpReceiver?.onOTPReceived(extractOtp(otp))
        }

        CommonStatusCodes.TIMEOUT -> otpReceiver?.onOTPTimeOut()
      }
    }
  }

  private fun extractOtp(message: String): String? {
    val otpRegex = Regex("\\d{6}")
    return otpRegex.find(message)?.value
  }

  interface OTPReceiveListener {

    fun onOTPReceived(otp: String?)

    fun onOTPTimeOut()
  }
}
