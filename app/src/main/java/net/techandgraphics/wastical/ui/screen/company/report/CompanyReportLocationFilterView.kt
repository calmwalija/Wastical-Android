package net.techandgraphics.wastical.ui.screen.company.report

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.data.local.database.dashboard.account.DemographicItem
import net.techandgraphics.wastical.ui.theme.WasticalTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CompanyReportLocationFilterView(
  filters: Set<DemographicItem>,
  demographicItems: List<DemographicItem>,
  onEvent: (CompanyReportEvent.Button.LocationDialog) -> Unit,
) {

  val scrollState = rememberLazyListState()

  Column(
    modifier = Modifier
      .padding(horizontal = 8.dp)
      .fillMaxWidth()
  ) {
    Text(
      text = "Select Location",
      modifier = Modifier
        .padding(vertical = 8.dp)
        .fillMaxWidth(),
      style = MaterialTheme.typography.headlineSmall,
      textAlign = TextAlign.Center
    )

    LazyColumn(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
        .padding(horizontal = 8.dp),
      state = scrollState
    ) {
      items(demographicItems) { item ->
        FilterChip(
          selected = filters.contains(item),
          onClick = { onEvent(CompanyReportEvent.Button.LocationDialog.Pick(item)) },
          label = {
            Text(text = item.theArea.plus(", ${item.theStreet}"))
          },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
          ),
          modifier = Modifier.padding(horizontal = 4.dp)
        )
      }
    }


    Row(
      modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth(),
      horizontalArrangement = Arrangement.Center
    ) {
      Button(
        enabled = filters.isNotEmpty(),
        modifier = Modifier.weight(1f),
        onClick = { onEvent(CompanyReportEvent.Button.LocationDialog.Proceed) }) {
        Box {
          Text(text = "Proceed")
        }
      }

      Spacer(modifier = Modifier.width(8.dp))

      OutlinedButton(
        onClick = { onEvent(CompanyReportEvent.Button.LocationDialog.Close) }) {
        Box {
          Text(text = "Cancel")
        }
      }

    }
  }
}

@Preview(showBackground = true)
@Composable
fun CompanyReportLocationFilterPreview() {
  WasticalTheme {
    CompanyReportLocationFilterView(
      filters = companyReportStateSuccess().demographicFilters,
      demographicItems = companyReportStateSuccess().demographics
    ) {}
  }
}
