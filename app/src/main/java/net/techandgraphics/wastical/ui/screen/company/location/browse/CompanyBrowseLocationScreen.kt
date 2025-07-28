package net.techandgraphics.wastical.ui.screen.company.location.browse

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.data.local.database.dashboard.payment.MonthYear
import net.techandgraphics.wastical.getToday
import net.techandgraphics.wastical.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastical.ui.screen.company.CompanyInfoTopAppBarView
import net.techandgraphics.wastical.ui.screen.company4Preview
import net.techandgraphics.wastical.ui.screen.payment4CurrentLocationMonth4Preview
import net.techandgraphics.wastical.ui.theme.Muted
import net.techandgraphics.wastical.ui.theme.WasticalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyBrowseLocationScreen(
  state: CompanyBrowseLocationState,
  onEvent: (CompanyBrowseLocationEvent) -> Unit,
) {

  when (state) {
    CompanyBrowseLocationState.Loading -> LoadingIndicatorView()
    is CompanyBrowseLocationState.Success ->
      Scaffold(
        topBar = {
          CompanyInfoTopAppBarView(state.company) {
            onEvent(CompanyBrowseLocationEvent.Button.BackHandler)
          }
        },
      ) {
        LazyColumn(
          contentPadding = it,
          modifier = Modifier.padding(16.dp)
        ) {
          item {
            Row(
              modifier = Modifier.padding(start = 16.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "Browse Location",
                  style = MaterialTheme.typography.headlineMedium,
                )
                Text(
                  text = "Which location are you looking for ?",
                  style = MaterialTheme.typography.bodyMedium,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                  color = Muted
                )
              }
            }
          }

          item { Spacer(modifier = Modifier.height(16.dp)) }

          item { CompanyPaymentPerLocationSearchView(state, onEvent) }

          item { Spacer(modifier = Modifier.height(8.dp)) }

          items(state.payment4CurrentLocationMonth) { CompanyBrowseLocationView(it, onEvent) }

        }
      }
  }

}


@Preview
@Composable
private fun CompanyBrowseLocationScreenPreview() {
  WasticalTheme {
    CompanyBrowseLocationScreen(
      state = companyBrowseLocationStateSuccess(),
      onEvent = {}
    )
  }
}

fun companyBrowseLocationStateSuccess() = CompanyBrowseLocationState.Success(
  payment4CurrentLocationMonth = listOf(payment4CurrentLocationMonth4Preview),
  company = company4Preview,
  monthYear = MonthYear(getToday().month, getToday().year)
)
