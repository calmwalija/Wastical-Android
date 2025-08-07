package net.techandgraphics.wastical.ui.screen.company.report

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.R
import net.techandgraphics.wastical.data.local.database.dashboard.payment.MonthYear
import net.techandgraphics.wastical.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastical.ui.screen.account4Preview
import net.techandgraphics.wastical.ui.screen.company.CompanyInfoTopAppBarView
import net.techandgraphics.wastical.ui.screen.company4Preview
import net.techandgraphics.wastical.ui.screen.demographicStreet4Preview
import net.techandgraphics.wastical.ui.theme.WasticalTheme

@OptIn(ExperimentalMaterial3Api::class) @Composable fun CompanyReportScreen(
  state: CompanyReportState,
  onEvent: (CompanyReportEvent) -> Unit,
) {

  var contentHeight by remember { mutableIntStateOf(0) }
  var showMonthDialog by remember { mutableStateOf(false) }
  var eventToProceedWith by remember { mutableStateOf<CompanyReportEvent?>(null) }
  val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val months4AccPay = remember { mutableStateListOf<MonthYear?>(null) }
  var isAccPay by remember { mutableStateOf(false) }

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
          onDismissRequest = { showMonthDialog = false }, sheetState = modalBottomSheetState
        ) {
          months4AccPay.clear()
          months4AccPay.addAll(if (isAccPay) state.monthAccountsCreated else state.allMonthPayments)
          CompanyReportMonthFilterView(
            filters = state.filters,
            items = months4AccPay.toList().mapNotNull { items -> items }) { event ->
            when (event) {
              CompanyReportEvent.Button.MonthDialog.Close -> showMonthDialog = false
              CompanyReportEvent.Button.MonthDialog.Proceed -> eventToProceedWith?.let { withEvent ->
                showMonthDialog = false
                onEvent(withEvent)
                eventToProceedWith = null
              }

              else -> onEvent(event)
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
            ExportInformationItem(
              label = "New Clients Report", event = CompanyReportEvent.Button.Export.NewAccount
            ),
            ExportInformationItem(
              label = "All Active Clients Report", event = CompanyReportEvent.Button.Export.Client
            ),
            ExportInformationItem(
              label = "Collected Payments Report",
              event = CompanyReportEvent.Button.Export.Collected
            ),
            ExportInformationItem(
              label = "Outstanding Payments Report",
              event = CompanyReportEvent.Button.Export.Outstanding
            ),
            ExportInformationItem(
              label = "Payment Coverage Report", event = CompanyReportEvent.Button.Export.Coverage
            ),
            ExportInformationItem(
              label = "Payment Plan Distribution Report",
              event = CompanyReportEvent.Button.Export.Coverage
            ),
            ExportInformationItem(
              label = "Payment Method Usage Report",
              event = CompanyReportEvent.Button.Export.Coverage
            ),
            ExportInformationItem(
              label = "Location-based Reports", event = CompanyReportEvent.Button.Export.Coverage
            ),
            ExportInformationItem(
              label = "Revenue Summary Report", event = CompanyReportEvent.Button.Export.Coverage
            ),
            ExportInformationItem(
              label = "Account Disengagement Report",
              event = CompanyReportEvent.Button.Export.Coverage
            ),
            ExportInformationItem(
              label = "Overpayment Report", event = CompanyReportEvent.Button.Export.Coverage
            ),
            ExportInformationItem(
              label = "Missed Payment History", event = CompanyReportEvent.Button.Export.Coverage
            ),
            ExportInformationItem(
              label = "Average Revenue Per Client",
              event = CompanyReportEvent.Button.Export.Coverage
            ),
            ExportInformationItem(
              label = "Service Coverage Gap Analysis",
              event = CompanyReportEvent.Button.Export.Coverage
            ),
            ExportInformationItem(
              label = "Client Retention/Churn Risk Report",
              event = CompanyReportEvent.Button.Export.Coverage
            ),
            ExportInformationItem(
              label = "Company Service Coverage Report",
              event = CompanyReportEvent.Button.Export.Coverage
            ),
            ExportInformationItem(
              label = "Revenue Forecast Report", event = CompanyReportEvent.Button.Export.Coverage
            ),

            ExportInformationItem(
              label = "Client Contact Directory", event = CompanyReportEvent.Button.Export.Coverage
            ),

            ).forEach { item ->
            ExportInformationItem(item) { event ->
              when (event) {
                CompanyReportEvent.Button.Export.Client -> onEvent(event)

                CompanyReportEvent.Button.Export.NewAccount -> {
                  isAccPay = true
                  showMonthDialog = true
                  eventToProceedWith = event
                }

                CompanyReportEvent.Button.Export.Collected -> onEvent(event)
                CompanyReportEvent.Button.Export.Coverage -> onEvent(event)
                CompanyReportEvent.Button.Export.Geographic -> onEvent(event)
                CompanyReportEvent.Button.Export.Outstanding -> onEvent(event)
                CompanyReportEvent.Button.Export.Plan -> onEvent(event)
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

data class ExportInformationItem(
  val label: String,
  val event: CompanyReportEvent,
)


@Composable fun ExportInformationItem(
  item: ExportInformationItem,
  onEvent: (CompanyReportEvent) -> Unit,
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 8.dp, vertical = 4.dp),
    shape = CircleShape,
    colors = CardDefaults.elevatedCardColors(),
    onClick = { onEvent(item.event) }) {
    Row(
      modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {

      Image(
        painterResource(R.drawable.ic_invoice),
        contentDescription = null,
        modifier = Modifier
          .size(24.dp)
          .padding(2.dp)
      )

      Column(
        modifier = Modifier
          .weight(1f)
          .padding(horizontal = 8.dp)
      ) {
        Text(
          text = item.label, style = MaterialTheme.typography.bodyMedium
        )
      }

      Icon(
        Icons.AutoMirrored.Filled.KeyboardArrowRight,
        contentDescription = null,
        modifier = Modifier.size(20.dp),
        tint = MaterialTheme.colorScheme.primary
      )
      Spacer(modifier = Modifier.width(8.dp))

    }
  }
}


@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES) @Composable private fun CompanyReportScreenPreview() {
  WasticalTheme {
    CompanyReportScreen(
      state = companyReportStateSuccess(), onEvent = {})
  }
}

fun companyReportStateSuccess() = CompanyReportState.Success(
  company = company4Preview,
  accounts = (1..5).map { account4Preview },
  demographics = (1..7).map { demographicStreet4Preview },
  allMonthPayments = (1..10).mapIndexed { index, _ ->
    val cIndex = index.plus(1)
    MonthYear(cIndex, 2025)
  })
