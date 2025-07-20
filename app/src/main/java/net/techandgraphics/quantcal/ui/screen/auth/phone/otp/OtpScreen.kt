package net.techandgraphics.quantcal.ui.screen.auth.phone.otp

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Base64
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.auth.api.phone.SmsRetriever
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.techandgraphics.quantcal.R
import net.techandgraphics.quantcal.toast
import net.techandgraphics.quantcal.ui.screen.LoadingIndicatorView
import net.techandgraphics.quantcal.ui.theme.QuantcalTheme
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun OtpScreen(
  state: OtpState,
  channel: Flow<OtpChannel>,
  onEvent: (OtpEvent) -> Unit,
) {

  var opt by remember { mutableStateOf("") }
  val context = LocalContext.current

  val lifecycleOwner = LocalLifecycleOwner.current
  LaunchedEffect(key1 = channel) {
    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
      channel.collect { event ->
        when (event) {
          OtpChannel.Success -> onEvent(OtpEvent.Goto.Home)
          is OtpChannel.Error -> context.toast(event.error.message)
        }
      }
    }
  }


  when (state) {
    OtpState.Loading -> LoadingIndicatorView()
    is OtpState.Success -> {

      LaunchedEffect(Unit) {
        SmsRetriever.getClient(context)
          .startSmsRetriever()
          .addOnSuccessListener { onEvent(OtpEvent.Timer.Start) }
          .addOnFailureListener { onEvent(OtpEvent.Timer.Failed) }
      }


      OtpListener { event ->
        when (event) {
          is OtpListenerEvent.OTPReceived ->
            event.opt?.let { onEvent(OtpEvent.Otp(it)) }

          OtpListenerEvent.OTPTimeOut -> onEvent(OtpEvent.Timer.TimedOut)
        }
      }

      val minutes = TimeUnit.MILLISECONDS.toMinutes(state.timeLeft) % 60
      val seconds = TimeUnit.MILLISECONDS.toSeconds(state.timeLeft) % 60

      Scaffold { contentPadding ->
        LazyColumn(
          contentPadding = contentPadding,
          modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {

          item {
            Icon(
              painter = painterResource(R.drawable.ic_sms),
              contentDescription = null,
              modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
                .size(82.dp),
              tint = MaterialTheme.colorScheme.secondary,
            )
          }

          item {
            Text(
              text = "OTP Verification",
              fontWeight = FontWeight.Bold,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              style = MaterialTheme.typography.titleLarge,
              modifier = Modifier
                .padding(bottom = 8.dp)
                .fillMaxWidth(),
              textAlign = TextAlign.Center,
              color = MaterialTheme.colorScheme.primary
            )
          }

          item {
            Text(
              text = "Enter the 4 digit code sent to ${state.phone}",
              style = MaterialTheme.typography.bodyMedium,
              modifier = Modifier.fillMaxWidth(),
              textAlign = TextAlign.Center
            )
          }

          item {
            Text(
              text = String.format(
                locale = Locale.getDefault(),
                format = "%02d:%02d", minutes, seconds
              ),
              style = MaterialTheme.typography.titleLarge,
              modifier = Modifier.padding(top = 16.dp)
            )
          }

          item { Spacer(modifier = Modifier.height(8.dp)) }

          item { OtpInput { opt = it } }

          item {
            Button(
              modifier = Modifier.fillMaxWidth(.7f),
              onClick = { onEvent(OtpEvent.Otp(opt)) }
            ) {
              Box {
                Text(text = "Verify")
              }
            }
          }
        }
      }
    }
  }

}


object SmsRetrieverHashHelper {

  fun getAppSignatures(context: Context): List<String> {
    val appSignatures = mutableListOf<String>()

    val packageName = context.packageName
    val packageManager = context.packageManager

    try {
      val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val info =
          packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
        info.signingInfo?.apkContentsSigners
      } else {
        @Suppress("DEPRECATION")
        val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
        @Suppress("DEPRECATION")
        info.signatures
      }

      if (signatures != null) {
        for (signature in signatures) {
          val signatureBytes = signature.toByteArray()
          val hash = hash(packageName, signatureBytes)
          if (hash != null) {
            appSignatures.add(hash)
          }
        }
      }

    } catch (e: Exception) {
      Log.e("SmsRetrieverHashHelper", "Package not found", e)
    }

    return appSignatures
  }

  private fun hash(packageName: String, signature: ByteArray): String? {
    return try {
      val messageDigest = MessageDigest.getInstance("SHA-256")
      val input = "$packageName ${Base64.encodeToString(signature, Base64.NO_WRAP)}"
      messageDigest.update(input.toByteArray(Charsets.UTF_8))
      val hashSignature = messageDigest.digest()
      val base64Hash = Base64.encodeToString(hashSignature, Base64.NO_WRAP).substring(0, 11)
      base64Hash
    } catch (e: NoSuchAlgorithmException) {
      Log.e("SmsRetrieverHashHelper", "hash: NoSuchAlgorithm", e)
      null
    }
  }
}

@Composable private fun OtpInput(
  otpLength: Int = 4,
  onOtpComplete: (String) -> Unit,
) {

  val otpValues = remember { mutableStateListOf(*Array(otpLength) { "" }) }
  val keyboardController = LocalSoftwareKeyboardController.current

  fun handleOtpInput(input: String, index: Int) {
    if (input.length == 1 && input.all { it.isDigit() }) {
      otpValues[index] = input
      if (index < otpLength - 1) {
        otpValues[index + 1] = ""
      }
      if (otpValues.all { it.isNotEmpty() }) {
        keyboardController?.hide()
        onOtpComplete(otpValues.joinToString(""))
      }
    }
  }

  fun handleOtpDelete() {
    val lastFilledIndex = otpValues.indexOfLast { it.isNotEmpty() }
    if (lastFilledIndex >= 0) {
      otpValues[lastFilledIndex] = ""
    }
  }

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(10.dp),
    modifier = Modifier.padding(16.dp)
  ) {
    Row(
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      otpValues.forEachIndexed { index, value ->
        val scale by animateFloatAsState(
          targetValue = if (value.isNotEmpty()) 1.1f else 1f,
          animationSpec = tween(150)
        )
        OutlinedTextField(
          value = value,
          onValueChange = {},
          modifier = Modifier
            .padding(bottom = 16.dp)
            .weight(1f)
            .height(60.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .border(2.dp, MaterialTheme.colorScheme.secondary),
          textStyle = LocalTextStyle.current.copy(
            textAlign = TextAlign.Center,
            fontSize = 20.sp
          ),
          singleLine = true,
          enabled = false,
          colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = MaterialTheme.colorScheme.secondary
          )
        )
      }
    }

    OtpNumberPadView { char ->
      if (char == "⌫") handleOtpDelete() else {
        val firstEmptyIndex = otpValues.indexOfFirst { it.isEmpty() }
        if (firstEmptyIndex >= 0) {
          handleOtpInput(char, firstEmptyIndex)
        }
      }
    }

  }
}

@Preview(showBackground = true)
@Composable
private fun OptScreenPreview() {
  QuantcalTheme {
    OtpScreen(
      state = OtpState.Success(phone = "999112233"),
      channel = flow { },
      onEvent = {}
    )
  }
}
