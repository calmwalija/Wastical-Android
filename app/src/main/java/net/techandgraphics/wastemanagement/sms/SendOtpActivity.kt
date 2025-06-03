@file:Suppress("DEPRECATION")

package net.techandgraphics.wastemanagement.sms


import android.Manifest
import android.content.Context
import android.content.ContextWrapper
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.SmsManager
import android.telephony.SubscriptionManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.phone.SmsRetriever


class SendOtpActivity(context: Context) : ContextWrapper(context) {

  fun startSmsRetriever() =
    SmsRetriever.getClient(this)
      .startSmsRetriever()
      .addOnSuccessListener {
        Log.e("SmsRetriever", "Started listening for SMS 🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥")
      }
      .addOnFailureListener {
        Log.e("SmsRetriever", "Failed to start listening ❌❌❌❌❌❌❌", it)
      }


  @Composable
  fun OnSmsBroadcastReceiver() {

    val context = LocalContext.current
    val smsReceiver = remember {
      SmsBroadcastReceiver().apply {
        this.initOTPListener(object : SmsBroadcastReceiver.OTPReceiveListener {
          override fun onOTPReceived(otp: String?) {
            Log.e("OTP", "✅✅✅✅✅✅✅ Received OTP: $otp")
          }

          override fun onOTPTimeOut() {
            Log.e("OTP", "❌❌❌❌❌ onOTPTimeOut")
          }

        })

      }

    }

    DisposableEffect(key1 = context) {
      val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
      ContextCompat.registerReceiver(
        context,
        smsReceiver,
        intentFilter,
        null,
        null,
        ContextCompat.RECEIVER_NOT_EXPORTED
      )
      onDispose {
        Log.e("TAG", "OnSmsBroadcastReceiver: onDispose")
        context.unregisterReceiver(smsReceiver)
      }
    }

  }


  @Composable
  fun SmsSenderScreen() {
    val context = this@SendOtpActivity
    val smsPermission = Manifest.permission.SEND_SMS

    var phoneNumber by remember { mutableStateOf("0980127004") }
    var messageText by remember {
      mutableStateOf(
        """
<#> Your OTP code is 123456
bAHcNsdkTEG
    """.trimIndent()
      )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
      ActivityResultContracts.RequestPermission()
    ) { isGranted ->
      if (!isGranted) {
        Toast.makeText(context, "SMS permission is required", Toast.LENGTH_SHORT).show()
      }
    }

    LaunchedEffect(Unit) {
      if (ContextCompat.checkSelfPermission(
          this@SendOtpActivity,
          smsPermission
        ) != PackageManager.PERMISSION_GRANTED
      ) {
        permissionLauncher.launch(smsPermission)
      }
    }

    Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
          value = phoneNumber,
          onValueChange = { phoneNumber = it },
          label = { Text("Phone Number") },
          keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = messageText,
          onValueChange = { messageText = it },
          label = { Text("Message") },
          modifier = Modifier.fillMaxWidth()
        )


        Button(
          onClick = {
            if (ContextCompat.checkSelfPermission(
                context,
                smsPermission
              ) == PackageManager.PERMISSION_GRANTED
            ) {

              val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val subscriptionId = SubscriptionManager.getDefaultSmsSubscriptionId()
                SmsManager.getSmsManagerForSubscriptionId(subscriptionId)
              } else {
                SmsManager.getDefault()
              }

              try {
                smsManager.sendTextMessage(phoneNumber, null, messageText, null, null)
                Toast.makeText(context, "SMS sent", Toast.LENGTH_SHORT).show()
              } catch (e: Exception) {
                Toast.makeText(context, "Failed to send SMS: ${e.message}", Toast.LENGTH_LONG)
                  .show()
              }
            } else {
              permissionLauncher.launch(smsPermission)
            }
          },
          modifier = Modifier.padding(top = 16.dp)
        ) {
          Text("Send SMS")
        }


      }
    }
  }
}
