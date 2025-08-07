package net.techandgraphics.wastical.ui.screen.company.report

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.data.local.database.dashboard.payment.MonthYear
import net.techandgraphics.wastical.toMonthName
import net.techandgraphics.wastical.ui.theme.WasticalTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CompanyReportMonthFilterView(
  filters: Set<MonthYear>,
  items: List<MonthYear>,
  onEvent: (CompanyReportEvent) -> Unit,
) {

  Column {
    Column(
      modifier = Modifier
        .padding(horizontal = 8.dp)
        .fillMaxWidth()
    ) {
      Text(
        text = "Select Months",
        modifier = Modifier
          .padding(vertical = 8.dp)
          .fillMaxWidth(),
        style = MaterialTheme.typography.headlineSmall,
        textAlign = TextAlign.Center
      )

      FlowRow(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 8.dp)
      ) {

        (items).forEach { item ->
          FilterChip(
            selected = filters.contains(item),
            onClick = { onEvent(CompanyReportEvent.Button.MonthDialog.PickMonth(item)) },
            label = {
              Text(
                text =
                  item.month.toMonthName()
                    .plus(" ")
                    .plus(item.year)
              )
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
          onClick = { onEvent(CompanyReportEvent.Button.MonthDialog.Proceed) }) {
          Box {
            Text(text = "Proceed")
          }
        }

        Spacer(modifier = Modifier.width(8.dp))

        OutlinedButton(
          onClick = { onEvent(CompanyReportEvent.Button.MonthDialog.Close) }) {
          Box {
            Text(text = "Cancel")
          }
        }

      }

    }
  }
}

@Preview(showBackground = true)
@Composable
fun CompanyReportMonthFilterPreview() {
  WasticalTheme {
    CompanyReportMonthFilterView(
      filters = companyReportStateSuccess().filters,
      items = companyReportStateSuccess().allMonthPayments
    ) {}
  }
}
