package net.techandgraphics.wastemanagement.ui.screen.payment

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.mr0xf00.easycrop.CropError
import com.mr0xf00.easycrop.CropResult
import com.mr0xf00.easycrop.crop
import com.mr0xf00.easycrop.rememberImageCropper
import com.mr0xf00.easycrop.ui.ImageCropperDialog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.techandgraphics.wastemanagement.toAmount
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
  state: PaymentState,
  channel: Flow<PaymentChannel>,
  onEvent: (PaymentEvent) -> Unit
) {

  val scrollState = rememberLazyListState()
  val hapticFeedback = LocalHapticFeedback.current
  val context = LocalContext.current

  val imageCropper = rememberImageCropper()
  val cropState = imageCropper.cropState


  var showCropDialog by remember { mutableStateOf(false) }


  fun convertToSoftwareBitmap(hardwareBitmap: Bitmap): Bitmap {
    return hardwareBitmap.copy(Bitmap.Config.ARGB_8888, true)
  }


  fun recognizeTextFromImage(bitmap: Bitmap, onResult: (String) -> Unit) {
    val image = InputImage.fromBitmap(bitmap, 0)
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    recognizer.process(image)
      .addOnSuccessListener { visionText ->
        onResult(visionText.text)
      }
      .addOnFailureListener { e ->
        onResult("Error: ${e.message}")
      }
  }

  LaunchedEffect(state.bitmapImage) {
    state.bitmapImage?.let {
      val result = imageCropper.crop(bmp = convertToSoftwareBitmap(it).asImageBitmap())
      when (result) {
        CropResult.Cancelled -> {
          Log.e("TAG", "Cancelled: ")
        }

        is CropError -> {
          Log.e("TAG", "CropError: ")
        }

        is CropResult.Success -> {
          Log.e("TAG", "Success: ")
          val bitmap = result.bitmap.asAndroidBitmap()
          recognizeTextFromImage(bitmap) {
            println("*************** $it")
            onEvent(PaymentEvent.Button.ImageBitmap(bitmap))
            onEvent(PaymentEvent.Button.Pay(it))
            showCropDialog = false
          }
        }

      }
    }
  }

  if (cropState != null && showCropDialog) ImageCropperDialog(state = cropState)


  val launcherForActivityResult =
    rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
      uri?.let {
        val bitmap = if (Build.VERSION.SDK_INT < 28) {
          MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        } else {
          val source = ImageDecoder.createSource(context.contentResolver, uri)
          ImageDecoder.decodeBitmap(source)
        }
        onEvent(PaymentEvent.Button.ImageBitmap(bitmap))
        showCropDialog = true
      }
    }


  val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
  LaunchedEffect(key1 = channel) {
    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
      channel.collect { event ->
        when (event) {
          else -> {
            TODO()
          }
        }
      }
    }
  }


  Scaffold(
    topBar = {
      TopAppBar(
        title = {},
        navigationIcon = {
          IconButton(onClick = { }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
          }
        },
        modifier = Modifier.shadow(0.dp),
        colors = TopAppBarDefaults.topAppBarColors()
      )
    },
    bottomBar = {
      Surface(shadowElevation = 10.dp, tonalElevation = 1.dp) {
        Row(
          modifier = Modifier
            .padding(vertical = 24.dp, horizontal = 16.dp)
            .fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Total",
              style = MaterialTheme.typography.titleSmall
            )
            state.paymentPlans.firstOrNull()?.let { paymentPlan ->

              val animatedSum by animateIntAsState(
                targetValue = state.numberOfMonths.times(paymentPlan.fee),
                animationSpec = tween(
                  delayMillis = 1_000,
                  durationMillis = 1_000,
                )
              )

              Text(
                text = animatedSum.toAmount(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth(.4f)
              )
            }
          }
          Spacer(modifier = Modifier.weight(1f))
          Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth(.8f),
          ) {
            Text(
              text = "Make Payment",
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSecondary
            )
          }
        }
      }
    },
    contentWindowInsets = ScaffoldDefaults
      .contentWindowInsets
      .exclude(WindowInsets.navigationBars)
      .exclude(WindowInsets.ime),
  ) {
    Box(modifier = Modifier.padding(it)) {
      LazyColumn(
        state = scrollState,
        modifier = Modifier
          .padding(horizontal = 16.dp)
      ) {
        item {
          Text(
            text = "Payment",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = 16.dp)
          )
        }

        item { PaymentPlanView(state, onEvent) }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { PaymentMethodView(state, onEvent) }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item {
          PaymentReferenceView(state) { event ->
            when (event) {
              PaymentEvent.Button.Screenshot -> launcherForActivityResult.launch("image/*")
              else -> onEvent(event)
            }
          }
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
      }
    }
  }


}


@Preview
@Composable
private fun PaymentScreenPreview() {
  WasteManagementTheme {
    PaymentScreen(
      state = PaymentState(
        paymentPlans = listOf(paymentPlan),
        paymentMethods = listOf(paymentMethod, paymentMethod),
        imageLoader = imageLoader(LocalContext.current)
      ),
      channel = flow { },
      onEvent = {}
    )
  }
}
