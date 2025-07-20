package net.techandgraphics.quantcal.ui.screen.auth.phone.otp


import android.content.IntentFilter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.phone.SmsRetriever
import net.techandgraphics.quantcal.broadcasts.otp.OtpBroadcastReceiver

sealed interface OtpListenerEvent {
  data class OTPReceived(val opt: String?) : OtpListenerEvent
  data object OTPTimeOut : OtpListenerEvent
}

@Composable
fun OtpListener(onEvent: (OtpListenerEvent) -> Unit) {

  val context = LocalContext.current
  val currentOnEvent by rememberUpdatedState(onEvent)
  val smsReceiver = remember { OtpBroadcastReceiver() }

  LaunchedEffect(Unit) {
    smsReceiver.initOTPListener(object : OtpBroadcastReceiver.OTPReceiveListener {
      override fun onOTPReceived(otp: String?) =
        currentOnEvent(OtpListenerEvent.OTPReceived(otp))

      override fun onOTPTimeOut() =
        currentOnEvent(OtpListenerEvent.OTPTimeOut)

    })
  }

  DisposableEffect(context) {
    val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
    ContextCompat.registerReceiver(
      context,
      smsReceiver,
      intentFilter,
      ContextCompat.RECEIVER_EXPORTED
    )

    onDispose {
      context.unregisterReceiver(smsReceiver)
    }
  }
}
