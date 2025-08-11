package net.techandgraphics.wastical.ui.screen.company.report

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.techandgraphics.wastical.R
import net.techandgraphics.wastical.data.local.database.dashboard.account.DemographicItem
import net.techandgraphics.wastical.data.local.database.dashboard.payment.MonthYear
import net.techandgraphics.wastical.toast
import net.techandgraphics.wastical.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastical.ui.screen.account4Preview
import net.techandgraphics.wastical.ui.screen.company.CompanyInfoTopAppBarView
import net.techandgraphics.wastical.ui.screen.company4Preview
import net.techandgraphics.wastical.ui.theme.WasticalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun CompanyReportScreen(
  state: CompanyReportState,
  channel: Flow<CompanyReportChannel>,
  onEvent: (CompanyReportEvent) -> Unit,
) {

  var contentHeight by remember { mutableIntStateOf(0) }
  var showMonthDialog by remember { mutableStateOf(false) }
  var showLocationDialog by remember { mutableStateOf(false) }
  var eventToProceedWith by remember { mutableStateOf<CompanyReportEvent.Button.Report?>(null) }
  val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val months4AccPay = remember { mutableStateListOf<MonthYear?>(null) }
  var isAccPay by remember { mutableStateOf(false) }
  val context = LocalContext.current
  val indicators = remember { mutableStateMapOf<CompanyReportEvent.Button.Report, Boolean>() }


  val lifecycleOwner = LocalLifecycleOwner.current
  LaunchedEffect(key1 = channel) {
    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
      channel.collect { event ->
        indicators.forEach { indicators[it.key] = false }
        when (event) {
          CompanyReportChannel.Pdf.Error -> context.toast("Failed to create PDF, please try again")
          CompanyReportChannel.Pdf.Success -> Unit
        }
      }
    }
  }

  when (state) {
    CompanyReportState.Loading -> LoadingIndicatorView()
    is CompanyReportState.Success -> Scaffold(
      topBar = {
        CompanyInfoTopAppBarView(state.company) {
          onEvent(CompanyReportEvent.Goto.BackHandler)
        }
      },
    ) {

      if (showMonthDialog) {
        ModalBottomSheet(
          onDismissRequest = {
            eventToProceedWith?.let { event -> indicators[event] = false }
            showMonthDialog = false
          }, sheetState = modalBottomSheetState
        ) {
          months4AccPay.clear()
          months4AccPay.addAll(if (isAccPay) state.monthAccountsCreated else state.allMonthPayments)
          CompanyReportMonthFilterView(
            filters = state.filters,
            items = months4AccPay.toList().mapNotNull { items -> items }) { event ->
            when (event) {
              CompanyReportEvent.Button.MonthDialog.Close -> {
                eventToProceedWith?.let { event -> indicators[event] = false }
                showMonthDialog = false
              }

              CompanyReportEvent.Button.MonthDialog.Proceed -> eventToProceedWith?.let { withEvent ->
                showMonthDialog = false
                onEvent(withEvent)
              }

              is CompanyReportEvent.Button.MonthDialog.PickMonth -> onEvent(event)
            }
          }
        }
      }

      if (showLocationDialog) {
        ModalBottomSheet(
          onDismissRequest = {
            eventToProceedWith?.let { event -> indicators[event] = false }
            showLocationDialog = false
          }, sheetState = modalBottomSheetState,
          dragHandle = {},
          sheetGesturesEnabled = false
        ) {
          CompanyReportLocationFilterView(
            filters = state.demographicFilters,
            demographicItems = state.demographics,
          ) { event ->
            when (event) {
              CompanyReportEvent.Button.LocationDialog.Close -> {
                eventToProceedWith?.let { event -> indicators[event] = false }
                showLocationDialog = false
              }

              CompanyReportEvent.Button.LocationDialog.Proceed -> eventToProceedWith?.let { withEvent ->
                showLocationDialog = false
                onEvent(withEvent)
              }


              is CompanyReportEvent.Button.LocationDialog.Pick -> onEvent(event)
            }
          }
        }
      }

      LazyColumn(
        contentPadding = it, modifier = Modifier.padding(16.dp)
      ) {
        item {
          Text(
            text = "Reports",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 32.dp)
          )
        }


        item {
          listOf(
            CompanyReportItem(
              drawableRes = R.drawable.ic_list_active,
              label = "Active Clients Report",
              event = CompanyReportEvent.Button.Report.ActiveClient
            ),
            CompanyReportItem(
              drawableRes = R.drawable.ic_person_add,
              label = "New Clients Report",
              event = CompanyReportEvent.Button.Report.NewClient
            ),
            CompanyReportItem(
              drawableRes = R.drawable.ic_location,
              label = "Location-based Reports",
              event = CompanyReportEvent.Button.Report.LocationBased
            ),
            CompanyReportItem(
              drawableRes = R.drawable.ic_list_inactive,
              label = "Client Disengagement Report",
              event = CompanyReportEvent.Button.Report.ClientDisengagement
            ),
            CompanyReportItem(
              drawableRes = R.drawable.ic_payment,
              label = "Paid Payment Report",
              event = CompanyReportEvent.Button.Report.PaidPayment
            ),
            CompanyReportItem(
              drawableRes = R.drawable.ic_close,
              label = "Missed Payment Report",
              event = CompanyReportEvent.Button.Report.MissedPayment
            ),
            CompanyReportItem(
              drawableRes = R.drawable.ic_fast_forward,
              label = "Overpayment Report",
              event = CompanyReportEvent.Button.Report.Overpayment
            ),
            CompanyReportItem(
              drawableRes = R.drawable.ic_balance,
              label = "Outstanding Balance Report",
              event = CompanyReportEvent.Button.Report.OutstandingBalance
            ),
            CompanyReportItem(
              drawableRes = R.drawable.ic_bar_chart,
              label = "Revenue Summary Report",
              event = CompanyReportEvent.Button.Report.RevenueSummary
            ),
            CompanyReportItem(
              drawableRes = R.drawable.ic_compare_arrows,
              label = "Plan Performance Report",
              event = CompanyReportEvent.Button.Report.PlanPerformance
            ),
            CompanyReportItem(
              drawableRes = R.drawable.ic_house,
              label = "Location Collection Report",
              event = CompanyReportEvent.Button.Report.LocationCollection
            ),
            CompanyReportItem(
              drawableRes = R.drawable.ic_database_upload,
              label = "Upfront Payments Report",
              event = CompanyReportEvent.Button.Report.UpfrontPaymentsDetail
            ),
            CompanyReportItem(
              drawableRes = R.drawable.ic_list,
              label = "Payment Aging Report",
              event = CompanyReportEvent.Button.Report.PaymentAging
            )
          ).forEach { item ->
            CompanyReportItemView(
              showIndicator = indicators[item.event] ?: false,
              item = item
            ) { event ->

              when (event) {
                is CompanyReportEvent.Button.Report -> {

                  indicators[event] = true
                  eventToProceedWith = event

                  when (event) {

                    CompanyReportEvent.Button.Report.ActiveClient -> onEvent(event)

                    CompanyReportEvent.Button.Report.NewClient -> {
                      isAccPay = true
                      showMonthDialog = true
                    }

                    CompanyReportEvent.Button.Report.PaidPayment -> {
                      isAccPay = false
                      showMonthDialog = true
                    }

                    CompanyReportEvent.Button.Report.MissedPayment -> {
                      isAccPay = false
                      showMonthDialog = true
                    }


                    CompanyReportEvent.Button.Report.OutstandingBalance -> onEvent(event)
                    CompanyReportEvent.Button.Report.LocationBased -> showLocationDialog = true
                    CompanyReportEvent.Button.Report.Overpayment -> onEvent(event)
                    CompanyReportEvent.Button.Report.ClientDisengagement -> onEvent(event)
                    CompanyReportEvent.Button.Report.RevenueSummary -> {
                      isAccPay = false; showMonthDialog = true
                    }

                    CompanyReportEvent.Button.Report.PaymentMethodBreakdown -> {
                      isAccPay = false; showMonthDialog = true
                    }

                    CompanyReportEvent.Button.Report.PlanPerformance -> {
                      isAccPay = false; showMonthDialog = true
                    }

                    CompanyReportEvent.Button.Report.LocationCollection -> {
                      isAccPay = false; showMonthDialog = true
                    }

                    CompanyReportEvent.Button.Report.GatewaySuccess -> {
                      isAccPay = false; showMonthDialog = true
                    }

                    CompanyReportEvent.Button.Report.UpfrontPaymentsDetail -> {
                      isAccPay = false; showMonthDialog = true
                    }

                    CompanyReportEvent.Button.Report.PaymentAging -> onEvent(event)
                  }
                }

                CompanyReportEvent.Button.MonthDialog.Proceed -> onEvent(event)
                is CompanyReportEvent.Button.MonthDialog.PickMonth -> onEvent(event)
                else -> onEvent(event)
              }
            }
          }
        }
      }
    }
  }
}


@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable private fun CompanyReportScreenPreview() {
  WasticalTheme {
    CompanyReportScreen(
      state = companyReportStateSuccess(),
      channel = flow { }
    ) {}
  }
}

fun companyReportStateSuccess() = CompanyReportState.Success(
  company = company4Preview,
  accounts = (1..5).map { account4Preview },
  demographics = (1L..7).map {
    DemographicItem(
      theStreet = "Ipsum",
      locationId = it,
      theArea = "Lorem",
      theAreaId = 1L,
      theStreetId = 1L
    )
  },
  allMonthPayments = (1..10).mapIndexed { index, _ ->
    val cIndex = index.plus(1)
    MonthYear(cIndex, 2025)
  })
