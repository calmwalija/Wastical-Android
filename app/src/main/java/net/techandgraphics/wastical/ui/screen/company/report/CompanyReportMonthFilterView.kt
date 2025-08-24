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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.data.local.database.dashboard.payment.MonthYear
import net.techandgraphics.wastical.toShortMonthName
import net.techandgraphics.wastical.ui.theme.WasticalTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CompanyReportMonthFilterView(
  filters: Set<MonthYear>,
  items: List<MonthYear>,
  onEvent: (CompanyReportEvent.Button.MonthDialog) -> Unit,
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

      val groupedByYear = items.groupBy { it.year }.toSortedMap(compareByDescending { it })

      LazyColumn(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
          .padding(horizontal = 8.dp),
      ) {
        items(groupedByYear.toList()) { (year, months) ->
          val selectedCount = months.count { filters.contains(it) }
          Card(
            modifier = Modifier
              .padding(bottom = 16.dp)
              .fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(
                text = year.toString(),
                style = MaterialTheme.typography.titleMedium,
              )
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "Selected: $selectedCount/${months.size}",
                  style = MaterialTheme.typography.bodyMedium,
                  modifier = Modifier.padding(end = 4.dp),
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Checkbox(
                    checked = selectedCount == months.size && months.isNotEmpty(),
                    onCheckedChange = { isChecked ->
                      if (isChecked) {
                        months.forEach {
                          if (!filters.contains(it)) onEvent(
                            CompanyReportEvent.Button.MonthDialog.PickMonth(
                              it
                            )
                          )
                        }
                      } else {
                        months.forEach {
                          if (filters.contains(it)) onEvent(
                            CompanyReportEvent.Button.MonthDialog.PickMonth(
                              it
                            )
                          )
                        }
                      }
                    },
                    modifier = Modifier.scale(1.2f)
                  )
                }
              }
            }
            Column(modifier = Modifier.padding(8.dp)) {
              FlowRow(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
              ) {
                months.sortedBy { it.month }.forEach { item ->
                  val isSelected = filters.contains(item)
                  FilterChip(
                    selected = isSelected,
                    onClick = { onEvent(CompanyReportEvent.Button.MonthDialog.PickMonth(item)) },
                    label = { Text(text = item.month.toShortMonthName()) },
                    colors = FilterChipDefaults.filterChipColors(
                      selectedContainerColor = MaterialTheme.colorScheme.primary,
                    ),
                    modifier = Modifier.padding(horizontal = 2.dp),
                    shape = CircleShape
                  )
                }
              }
            }
          }
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
