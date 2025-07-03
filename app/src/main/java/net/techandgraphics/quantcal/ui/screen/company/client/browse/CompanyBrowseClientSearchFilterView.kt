package net.techandgraphics.quantcal.ui.screen.company.client.browse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.quantcal.ui.screen.company4Preview
import net.techandgraphics.quantcal.ui.theme.QuantcalTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CompanyBrowseClientSearchFilterView(
  state: CompanyBrowseClientState.Success,
  onEvent: (CompanyBrowseClientListEvent) -> Unit,
) {

  Column {
    Column(
      modifier = Modifier
        .padding(horizontal = 8.dp)
        .fillMaxWidth()
    ) {
      Text(
        text = "Search Filter",
        modifier = Modifier
          .padding(vertical = 8.dp)
          .fillMaxWidth(),
        style = MaterialTheme.typography.headlineSmall,
        textAlign = TextAlign.Center
      )

      FlowRow(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(horizontal = 8.dp)
      ) {

        (state.demographicAreas).forEach { item ->
          FilterChip(
            selected = state.filters.contains(item.id),
            onClick = { onEvent(CompanyBrowseClientListEvent.Button.FilterBy(item.id)) },
            label = { Text(item.name) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primary,
            ),
            modifier = Modifier.padding(horizontal = 4.dp)
          )
        }

      }

    }
  }


}

@Preview(showBackground = true)
@Composable
fun CompanyBrowseClientSearchFilterPreview() {
  QuantcalTheme {
    CompanyBrowseClientSearchFilterView(
      state = CompanyBrowseClientState.Success(
        company = company4Preview,
      )
    ) {}
  }
}
