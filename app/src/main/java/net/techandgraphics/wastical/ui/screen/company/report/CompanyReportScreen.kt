package net.techandgraphics.wastical.ui.screen.company.report

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.R
import net.techandgraphics.wastical.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastical.ui.screen.account4Preview
import net.techandgraphics.wastical.ui.screen.company.CompanyInfoTopAppBarView
import net.techandgraphics.wastical.ui.screen.company4Preview
import net.techandgraphics.wastical.ui.screen.demographicStreet4Preview
import net.techandgraphics.wastical.ui.theme.WasticalTheme

@Composable fun CompanyReportScreen(
  state: CompanyReportState,
  onEvent: (CompanyReportEvent) -> Unit,
) {

  var contentHeight by remember { mutableIntStateOf(0) }

  when (state) {
    CompanyReportState.Loading -> LoadingIndicatorView()
    is CompanyReportState.Success -> Scaffold(
      topBar = {
        CompanyInfoTopAppBarView(state.company) {
          onEvent(CompanyReportEvent.Goto.BackHandler)
        }
      },
    ) {
      LazyColumn(
        contentPadding = it,
        modifier = Modifier.padding(16.dp)
      ) {
        item {
          Text(
            text = "Reports",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 32.dp)
          )
        }

        item {
          Row(modifier = Modifier.fillMaxWidth()) {
            Column(
              modifier = Modifier
                .height(with(LocalDensity.current) { contentHeight.toDp() })
                .weight(1f)
            ) {
              Card(
                modifier = Modifier
                  .padding(8.dp)
                  .fillMaxSize(),
              ) {
                Column(
                  modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Icon(
                    painterResource(R.drawable.ic_bar_chart),
                    contentDescription = null,
                    modifier = Modifier.size(72.dp),
                    tint = MaterialTheme.colorScheme.primary
                  )
                  Text(text = "Activity")
                }
              }
            }

            Column(
              modifier = Modifier
                .onGloballyPositioned { layoutCoordinates ->
                  contentHeight = layoutCoordinates.size.height
                }
                .weight(1f)) {
              Column {
                OutlinedCard(modifier = Modifier.padding(8.dp)) {
                  Column(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                  ) {
                    Icon(
                      painterResource(R.drawable.ic_account),
                      contentDescription = null,
                      modifier = Modifier.size(32.dp),
                      tint = MaterialTheme.colorScheme.primary
                    )
                    Text(text = "Clients")
                    Text(
                      text = state.accounts.size.toString(),
                      style = MaterialTheme.typography.bodyMedium
                    )
                  }
                }

                OutlinedCard(modifier = Modifier.padding(8.dp)) {
                  Column(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                  ) {
                    Icon(
                      painterResource(R.drawable.ic_house),
                      contentDescription = null,
                      modifier = Modifier.size(32.dp),
                      tint = MaterialTheme.colorScheme.primary
                    )
                    Text(text = "Locations")
                    Text(
                      text = state.demographics.size.toString(),
                      style = MaterialTheme.typography.bodyMedium
                    )
                  }
                }
              }
            }
          }
        }

        item {
          Text(
            text = "Export Information",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 16.dp)
          )
        }

        item {
          listOf(
            ExportInformationItem(
              label = "Clients Report",
              event = CompanyReportEvent.Button.Export.Client
            ),
            ExportInformationItem(
              label = "Collected Payments",
              event = CompanyReportEvent.Button.Export.Collected
            ),
            ExportInformationItem(
              label = "Outstanding Payments",
              event = CompanyReportEvent.Button.Export.Outstanding
            ),
            ExportInformationItem(
              label = "Payment Coverage",
              event = CompanyReportEvent.Button.Export.Coverage
            ),
          ).forEach { item ->
            ExportInformationItem(item) { onEvent(item.event) }
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


@Composable
fun ExportInformationItem(
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
          text = item.label,
          style = MaterialTheme.typography.bodyMedium
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


@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable private fun CompanyReportScreenPreview() {
  WasticalTheme {
    CompanyReportScreen(
      state = companyReportStateSuccess(), onEvent = {})
  }
}

fun companyReportStateSuccess() = CompanyReportState.Success(
  company = company4Preview,
  accounts = (1..5).map { account4Preview },
  demographics = (1..7).map { demographicStreet4Preview }
)
