package net.techandgraphics.wastical.ui.screen.company.payment.pay

import android.content.Context
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
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
import androidx.compose.foundation.lazy.rememberLazyListState
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
import net.techandgraphics.wastical.data.local.database.dashboard.payment.MonthYear
import net.techandgraphics.wastical.data.remote.payment.PaymentType
import net.techandgraphics.wastical.toAmount
import net.techandgraphics.wastical.toPluralMonth
import net.techandgraphics.wastical.toast
import net.techandgraphics.wastical.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastical.ui.screen.account4Preview
import net.techandgraphics.wastical.ui.screen.company.CompanyInfoTopAppBarView
import net.techandgraphics.wastical.ui.screen.company4Preview
import net.techandgraphics.wastical.ui.screen.companyLocationWithDemographic4Preview
import net.techandgraphics.wastical.ui.screen.imageLoader
import net.techandgraphics.wastical.ui.screen.paymentMethodWithGatewayAndPlan4Preview
import net.techandgraphics.wastical.ui.screen.paymentPlan4Preview
import net.techandgraphics.wastical.ui.theme.WasticalTheme
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyMakePaymentScreen(
  state: CompanyMakePaymentState,
  channel: Flow<CompanyMakePaymentChannel>,
  onEvent: (CompanyMakePaymentEvent) -> Unit,
) {

  val scrollState = rememberLazyListState()
  var loading by remember { mutableStateOf(false) }
  val context = LocalContext.current
  val contentColor = MaterialTheme.colorScheme.onSecondaryContainer


  val lifecycleOwner = LocalLifecycleOwner.current
  LaunchedEffect(key1 = channel) {
    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
      channel.collect { event ->
        loading = false
        when (event) {
          CompanyMakePaymentChannel.Pay.Success -> {
            context.toast("Payment request submitted")
            onEvent(CompanyMakePaymentEvent.GoTo.BackHandler)
          }
        }
      }
    }
  }


  when (state) {
    CompanyMakePaymentState.Loading -> LoadingIndicatorView()
    is CompanyMakePaymentState.Success ->
      Scaffold(
        topBar = {
          CompanyInfoTopAppBarView(state.company) {
            onEvent(CompanyMakePaymentEvent.GoTo.BackHandler)
          }
        },
        bottomBar = {
          BottomAppBar(containerColor = MaterialTheme.colorScheme.surface) {

            val isPaymentMethodCash = state.paymentMethods
              .filter { it.gateway.type == PaymentType.Cash.name }
              .any { it.method.isSelected }

            Row(
              modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                val animatedSum by animateIntAsState(
                  targetValue = state.numberOfMonths.times(state.paymentPlan.fee),
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
                AnimatedContent(targetState = animatedSum, label = "total-amount-company") { value ->
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
                  text = "${state.numberOfMonths} × ${state.paymentPlan.fee.toAmount()} per month",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
              }
              Spacer(modifier = Modifier.weight(1f))
              Button(
                enabled = (state.screenshotAttached && loading.not()) || (isPaymentMethodCash && loading.not()),
                onClick = {
                  onEvent(CompanyMakePaymentEvent.Button.RecordPayment)
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
          modifier = Modifier.padding(16.dp)
        ) {


          item {
            BasicText(
              text = "Proof Of Payment",
              style = MaterialTheme.typography.headlineSmall,
              color = ColorProducer { contentColor },
              modifier = Modifier
                .padding(end = 16.dp)
                .padding(bottom = 32.dp),
              maxLines = 1,
              autoSize = TextAutoSize.StepBased(
                maxFontSize = MaterialTheme.typography.headlineMedium.fontSize
              )
            )
          }

          item { CompanyMakePaymentClientView(state.demographic, state.account, onEvent) }
          item { Spacer(modifier = Modifier.height(24.dp)) }

          if (state.monthsOutstanding > 0) {

            item {
              val remainingAfterPayment =
                (state.monthsOutstanding - state.numberOfMonths).coerceAtLeast(0)
              val quantity = context.toPluralMonth(remainingAfterPayment)
              val due = context.toPluralMonth(state.monthsOutstanding)
              Card(
                colors = CardDefaults.cardColors(
                  containerColor = MaterialTheme.colorScheme.errorContainer,
                  contentColor = MaterialTheme.colorScheme.onErrorContainer,
                )
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = "Outstanding balance",
                      style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                      text = "$due due",
                      style = MaterialTheme.typography.bodyMedium,
                    )
                    if (state.outstandingMonths.isNotEmpty()) {
                      Spacer(modifier = Modifier.height(8.dp))
                      FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                      ) {
                        state.outstandingMonths.forEach { ym ->
                          Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                              .clip(RoundedCornerShape(16.dp))
                              .background(MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.1f))
                              .padding(horizontal = 10.dp, vertical = 4.dp)
                          ) {
                            Text(
                              text = java.time.Month.of(ym.month)
                                .getDisplayName(TextStyle.SHORT, Locale.getDefault())
                                .plus(" ${ym.year}"),
                              style = MaterialTheme.typography.labelSmall,
                            )
                          }
                        }
                      }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    if (remainingAfterPayment > 0) {
                      Text(
                        text = "Remaining after this payment: $quantity",
                        style = MaterialTheme.typography.labelLarge,
                      )
                    } else {
                      Text(
                        text = "Fully covered with this payment",
                        style = MaterialTheme.typography.labelLarge,
                      )
                    }
                  }
                }
              }
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
          }

          item { CompanyMakePaymentPlanView(state, onEvent) }

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
                MonthsCoveredPreview(
                  base = YearMonth.of(state.workingMonthYear.year, state.workingMonthYear.month),
                  count = state.numberOfMonths
                )
              }
            }
          }

          item { Spacer(modifier = Modifier.height(24.dp)) }
          item { CompanyMakePaymentMethodView(state, onEvent) }
          item { Spacer(modifier = Modifier.height(24.dp)) }
          item {
            if (state.paymentMethods
                .filter { it.gateway.type == PaymentType.Cash.name }
                .any { it.method.isSelected.not() }
            ) CompanyMakePaymentReferenceView(state, onEvent)
          }
          item { Spacer(modifier = Modifier.height(24.dp)) }
        }
      }
  }
}


@Preview
@Composable
private fun CompanyMakePaymentScreenPreview() {
  WasticalTheme {
    CompanyMakePaymentScreen(
      state = companySuccessState(LocalContext.current),
      channel = flow { }
    ) {}
  }
}


fun companySuccessState(context: Context) = CompanyMakePaymentState.Success(
  account = account4Preview,
  paymentPlan = paymentPlan4Preview,
  paymentMethods = listOf(
    paymentMethodWithGatewayAndPlan4Preview,
    paymentMethodWithGatewayAndPlan4Preview
  ),
  imageLoader = imageLoader(context),
  company = company4Preview,
  demographic = companyLocationWithDemographic4Preview,
  executedBy = account4Preview,
  workingMonthYear = MonthYear(9, 2025)
)

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
