package net.techandgraphics.quantcal.ui.screen.client.payment

import android.app.Activity
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.techandgraphics.quantcal.data.remote.payment.PaymentType
import net.techandgraphics.quantcal.domain.model.relations.PaymentMethodWithGatewayAndPlanUiModel
import net.techandgraphics.quantcal.toAmount
import net.techandgraphics.quantcal.toast
import net.techandgraphics.quantcal.ui.screen.LoadingIndicatorView
import net.techandgraphics.quantcal.ui.screen.account4Preview
import net.techandgraphics.quantcal.ui.screen.company.CompanyInfoTopAppBarView
import net.techandgraphics.quantcal.ui.screen.company4Preview
import net.techandgraphics.quantcal.ui.screen.paymentMethodWithGatewayAndPlan4Preview
import net.techandgraphics.quantcal.ui.screen.paymentPlan4Preview
import net.techandgraphics.quantcal.ui.theme.QuantcalTheme
import java.io.File
import java.time.ZonedDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientPaymentScreen(
  state: ClientPaymentState,
  channel: Flow<ClientPaymentChannel>,
  onEvent: (ClientPaymentEvent) -> Unit,
) {


  when (state) {
    ClientPaymentState.Loading -> LoadingIndicatorView()
    is ClientPaymentState.Success -> {

      val scrollState = rememberLazyListState()
      var loading by remember { mutableStateOf(false) }
      var paymentItem by remember { mutableStateOf<PaymentMethodWithGatewayAndPlanUiModel?>(null) }
      var showPaymentScreenshotDialog by remember { mutableStateOf(false) }

      val isPaymentMethodCash = state.paymentMethods
        .filter { PaymentType.Cash == PaymentType.valueOf(it.gateway.type) }
        .any { it.method.isSelected }

      val context = LocalContext.current

      val imagePickerLauncher =
        rememberLauncherForActivityResult(
          ActivityResultContracts.PickVisualMedia()
        ) {
          onEvent(ClientPaymentEvent.Button.ShowCropView(true))
          onEvent(ClientPaymentEvent.Button.ImageUri(it))
        }

      val uCropLauncher =
        rememberLauncherForActivityResult(
          ActivityResultContracts.StartActivityForResult()
        ) { result ->
          when (result.resultCode) {
            Activity.RESULT_OK -> {
              val resultUri = UCrop.getOutput(result.data!!)
              resultUri?.let { uri ->
                onEvent(ClientPaymentEvent.Button.ShowCropView(false))
                onEvent(ClientPaymentEvent.Button.ImageUri(uri))
                onEvent(ClientPaymentEvent.Button.ScreenshotAttached)
                showPaymentScreenshotDialog = false
                paymentItem = null
              }
            }

            Activity.RESULT_CANCELED ->  onEvent(ClientPaymentEvent.Button.ShowCropView(false))

            else -> onEvent(ClientPaymentEvent.Button.ShowCropView(false))
          }
        }

      fun cropImageView(sourceUri: Uri) {
        val destinationUri = Uri.fromFile(File(context.cacheDir, "${state.timestamp}.jpg"))
        val uCropIntent = UCrop.of(sourceUri, destinationUri)
          .withAspectRatio(3f, 2f)
          .withMaxResultSize(800, 800)
          .getIntent(context)
        uCropLauncher.launch(uCropIntent)
      }

      LaunchedEffect(state.showCropView) {
        if (state.showCropView) state.imageUri?.let { cropImageView(it) }
      }


      if (showPaymentScreenshotDialog && paymentItem != null) {
        ModalBottomSheet(onDismissRequest = { showPaymentScreenshotDialog = false }) {
          ClientPaymentScreenShotView(
            item = paymentItem!!,
            onProceed = { imagePickerLauncher.launch(PickVisualMediaRequest()) },
          )
        }
      }

      val lifecycleOwner = LocalLifecycleOwner.current
      LaunchedEffect(key1 = channel) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
          channel.collect { event ->
            when (event) {
              is ClientPaymentChannel.Pay.Failure ->
                onEvent(ClientPaymentEvent.Response(false, event.error.message))

              ClientPaymentChannel.Pay.Success ->
                onEvent(ClientPaymentEvent.Response(true, null))

            }
          }
        }
      }
      Scaffold(
        topBar = {
          CompanyInfoTopAppBarView(state.company) {
            onEvent(ClientPaymentEvent.GoTo.BackHandler)
          }
        },
        bottomBar = {
          BottomAppBar {
            Row(
              modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "Total",
                  style = MaterialTheme.typography.labelMedium
                )

                val animatedSum by animateIntAsState(
                  targetValue = state.monthsCovered.times(state.paymentPlan.fee),
                  animationSpec = tween(
                    delayMillis = 1_000,
                    durationMillis = 1_000,
                  )
                )

                Text(
                  text = animatedSum.toAmount(),
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.fillMaxWidth(.4f)
                )

              }
              Spacer(modifier = Modifier.weight(1f))
              Button(
                enabled = loading.not(),
                onClick = {
                  if (state.screenshotAttached.not() && isPaymentMethodCash.not()) {
                    state.paymentMethods
                      .firstOrNull { it.method.isSelected }
                      ?.let {
                        showPaymentScreenshotDialog = true
                        paymentItem = it
                        return@Button
                      }
                    context.toast("Something went wrong, please try again")
                    onEvent(ClientPaymentEvent.GoTo.BackHandler)
                    return@Button
                  }
                  onEvent(ClientPaymentEvent.Button.Submit)
                  loading = true
                },
                colors = ButtonDefaults.buttonColors(
                  containerColor = MaterialTheme.colorScheme.primary.copy(.3f)
                ),
                modifier = Modifier.fillMaxWidth(.6f),
              ) {
                if (loading) Row(verticalAlignment = Alignment.CenterVertically) {
                  CircularProgressIndicator(modifier = Modifier.size(16.dp))
                } else Text(
                  text = "Submit",
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                )
              }
            }

          }

        },
      ) {
        LazyColumn(
          state = scrollState,
          contentPadding = it,
          modifier = Modifier.padding(vertical = 32.dp, horizontal = 16.dp)
        ) {

          item {
            Text(
              text = "Send Payment Screenshot",
              style = MaterialTheme.typography.headlineSmall,
              modifier = Modifier.padding(bottom = 32.dp)
            )
          }


          item {
            Text(
              text = "Payment Plan",
              modifier = Modifier.padding(8.dp)
            )
          }

          item { ClientPaymentPlanView(state, onEvent) }

          item { Spacer(modifier = Modifier.height(24.dp)) }

          item {
            Text(
              text = "Payment Method Used",
              modifier = Modifier.padding(8.dp)
            )
          }
          items(state.paymentMethods) { item ->
            ClientPaymentMethodView(item) { event ->
              when (event) {
                is ClientPaymentEvent.Button.PaymentMethod -> {
                  onEvent(event)
                  if (PaymentType.valueOf(event.item.gateway.type) != PaymentType.Cash) {
                    paymentItem = event.item
                    showPaymentScreenshotDialog = true
                  }
                }

                else -> onEvent(event)
              }
            }
          }

          if (isPaymentMethodCash.not()) {
            item { Spacer(modifier = Modifier.height(24.dp)) }
            item {
              Text(
                text = "Payment Reference",
                modifier = Modifier.padding(8.dp)
              )
            }
            item {
              ClientPaymentReferenceView(state) { event ->
                when (event) {
                  ClientPaymentEvent.Button.AttachScreenshot -> imagePickerLauncher.launch(
                    PickVisualMediaRequest()
                  )

                  else -> onEvent(event)
                }
              }
            }
          }

          item { Spacer(modifier = Modifier.height(24.dp)) }
        }
      }
    }
  }
}


@Preview
@Composable
private fun ClientPaymentScreenPreview() {
  QuantcalTheme {
    ClientPaymentScreen(
      state = clientPaymentStateSuccess(),
      channel = flow { },
      onEvent = {}
    )
  }
}

fun clientPaymentStateSuccess() = ClientPaymentState.Success(
  account = account4Preview,
  company = company4Preview,
  paymentPlan = paymentPlan4Preview,
  paymentMethods = listOf(paymentMethodWithGatewayAndPlan4Preview),
  timestamp = ZonedDateTime.now().toEpochSecond()
)
