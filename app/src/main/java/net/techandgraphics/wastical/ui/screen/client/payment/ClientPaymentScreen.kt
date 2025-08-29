package net.techandgraphics.wastical.ui.screen.client.payment

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearEasing
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.techandgraphics.wastical.data.remote.payment.PaymentType
import net.techandgraphics.wastical.domain.model.relations.PaymentMethodWithGatewayAndPlanUiModel
import net.techandgraphics.wastical.toAmount
import net.techandgraphics.wastical.toast
import net.techandgraphics.wastical.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastical.ui.screen.account4Preview
import net.techandgraphics.wastical.ui.screen.company.CompanyInfoTopAppBarView
import net.techandgraphics.wastical.ui.screen.company4Preview
import net.techandgraphics.wastical.ui.screen.paymentMethodWithGatewayAndPlan4Preview
import net.techandgraphics.wastical.ui.screen.paymentPlan4Preview
import net.techandgraphics.wastical.ui.theme.WasticalTheme
import java.time.YearMonth
import java.time.ZonedDateTime
import java.time.format.TextStyle
import java.util.Locale

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

      var loading by remember { mutableStateOf(false) }
      var paymentItem by remember { mutableStateOf<PaymentMethodWithGatewayAndPlanUiModel?>(null) }
      var showPaymentScreenshotDialog by remember { mutableStateOf(false) }
      var showImageCropper by remember { mutableStateOf(false) }

      val isPaymentMethodCash = state.paymentMethods
        .filter { PaymentType.Cash == PaymentType.valueOf(it.gateway.type) }
        .any { it.method.isSelected }

      val context = LocalContext.current

      val proofPickerLauncher =
        rememberLauncherForActivityResult(
          ActivityResultContracts.OpenDocument()
        ) { uri ->
          if (uri == null) return@rememberLauncherForActivityResult
          val type = context.contentResolver.getType(uri) ?: ""
          if (type.startsWith("image/")) {
            showImageCropper = true
            onEvent(ClientPaymentEvent.Button.ImageUri(uri))
          } else if (type.equals("application/pdf", ignoreCase = true)) {
            onEvent(ClientPaymentEvent.Button.ImageUri(uri))
            onEvent(ClientPaymentEvent.Button.ScreenshotAttached)
            showPaymentScreenshotDialog = false
            paymentItem = null
          }
        }


      if (showPaymentScreenshotDialog && paymentItem != null) {
        ModalBottomSheet(
          onDismissRequest = { showPaymentScreenshotDialog = false },
          sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
          ClientPaymentScreenShotView(
            item = paymentItem!!,
            onProceed = { proofPickerLauncher.launch(arrayOf("image/*", "application/pdf")) },
          )
        }
      }

      if (showImageCropper) {
        ImageCropperScreen(
          imageUri = state.imageUri,
          onCropComplete = { croppedUri ->
            onEvent(ClientPaymentEvent.Button.ImageUri(croppedUri))
            onEvent(ClientPaymentEvent.Button.ScreenshotAttached)
            showImageCropper = false
            showPaymentScreenshotDialog = false
            paymentItem = null
          },
          onDismissRequest = {
            showImageCropper = false
          }
        )
      }

      val lifecycleOwner = LocalLifecycleOwner.current
      LaunchedEffect(key1 = channel) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
          channel.collect { event ->
            when (event) {

              ClientPaymentChannel.Pay.Success -> onEvent(ClientPaymentEvent.GoTo.BackHandler)

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
                val animatedSum by animateIntAsState(
                  targetValue = state.monthsCovered.times(state.paymentPlan.fee),
                  animationSpec = tween(
                    delayMillis = 120,
                    durationMillis = 380,
                  )
                )

                val blinkAlpha = remember { Animatable(1f) }
                LaunchedEffect(animatedSum) {
                  repeat(2) {
                    blinkAlpha.animateTo(0.25f, animationSpec = tween(durationMillis = 100, easing = LinearEasing))
                    blinkAlpha.animateTo(1f, animationSpec = tween(durationMillis = 120, easing = LinearEasing))
                  }
                }

                Text(
                  text = "Total",
                  style = MaterialTheme.typography.labelMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                AnimatedContent(targetState = animatedSum, label = "total-amount") { value ->
                  Text(
                    text = value.toAmount(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                      .fillMaxWidth(.6f)
                      .alpha(blinkAlpha.value)
                  )
                }
                Text(
                  text = "${state.monthsCovered} × ${state.paymentPlan.fee.toAmount()} per month",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                  containerColor = MaterialTheme.colorScheme.primary.copy(.7f)
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

        val contentColor = MaterialTheme.colorScheme.onSecondaryContainer


        LazyColumn(
          contentPadding = it,
          modifier = Modifier.padding(16.dp)
        ) {

          item {
            BasicText(
              text = "Send Proof Of Payment",
              style = MaterialTheme.typography.headlineSmall,
              color = ColorProducer { contentColor },
              modifier = Modifier
                .padding(end = 16.dp)
                .padding(bottom = 32.dp),
              maxLines = 1,
              autoSize = TextAutoSize.StepBased(
                maxFontSize = MaterialTheme.typography.headlineSmall.fontSize
              )
            )
          }

          if (state.monthsOutstanding > 0) {
            item { ClientPaymentBalanceView(state, context) }
            item { Spacer(modifier = Modifier.height(24.dp)) }
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
              text = "Months Covered",
              modifier = Modifier.padding(8.dp)
            )
          }

          item {
            Card(colors = CardDefaults.elevatedCardColors()) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(16.dp)
              ) {
                val base = run {
                  if (state.outstandingMonths.isNotEmpty()) {
                    val first = state.outstandingMonths.first()
                    YearMonth.of(first.year, first.month)
                  } else {
                    val today = ZonedDateTime.now()
                    val billingYm = if (today.dayOfMonth >= state.company.billingDate) {
                      YearMonth.of(today.year, today.month)
                    } else {
                      YearMonth.of(today.year, today.month).minusMonths(1)
                    }
                    billingYm.plusMonths(1)
                  }
                }
                MonthsCoveredPreview(
                  base = base,
                  count = state.monthsCovered
                )
              }
            }
          }

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
                  ClientPaymentEvent.Button.AttachScreenshot ->
                    proofPickerLauncher.launch(arrayOf("image/*", "application/pdf"))

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


@Composable
private fun MonthsCoveredPreview(base: YearMonth, count: Int) {
  val months = (0 until count).map { idx -> base.plusMonths(idx.toLong()) }
  val first = months.firstOrNull()
  val last = months.lastOrNull()

  Column(modifier = Modifier.padding(horizontal = 8.dp)) {
    if (first != null && last != null) {
      val headerText = if (count > 1) {
        first.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
          .plus(" ${first.year} - ")
          .plus(last.month.getDisplayName(TextStyle.FULL, Locale.getDefault()))
          .plus(" ${last.year}")
      } else {
        first.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
          .plus(" ${first.year}")
      }
      Text(
        text = headerText,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(bottom = 8.dp)
      )
    }

    FlowRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      months.forEach { ym ->
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
            .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
          Text(
            text = ym.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
              .plus(" ${ym.year}"),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
          )
        }
      }
    }
  }
}

@Preview
@Composable
private fun ClientPaymentScreenPreview() {
  WasticalTheme {
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
