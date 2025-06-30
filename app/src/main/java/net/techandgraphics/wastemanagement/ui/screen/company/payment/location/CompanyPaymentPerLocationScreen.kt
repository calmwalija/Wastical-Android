package net.techandgraphics.wastemanagement.ui.screen.company.payment.location

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastemanagement.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastemanagement.ui.screen.company.CompanyInfoTopAppBarView
import net.techandgraphics.wastemanagement.ui.screen.company4Preview
import net.techandgraphics.wastemanagement.ui.screen.payment4CurrentLocationMonth4Preview
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyPaymentPerLocationScreen(
  state: CompanyPaymentPerLocationState,
  onEvent: (CompanyPaymentPerLocationEvent) -> Unit,
) {

  when (state) {
    CompanyPaymentPerLocationState.Loading -> LoadingIndicatorView()
    is CompanyPaymentPerLocationState.Success ->
      Scaffold(
        topBar = {
          CompanyInfoTopAppBarView(state.company) {
            onEvent(CompanyPaymentPerLocationEvent.Button.BackHandler)
          }
        },
      ) {
        LazyColumn(
          contentPadding = it,
          modifier = Modifier.padding(vertical = 32.dp, horizontal = 4.dp)
        ) {
          item {
            Text(
              text = "Payment as per Location",
              style = MaterialTheme.typography.headlineSmall,
            )

            Text(
              text = "# of displayed locations is ${state.payment4CurrentLocationMonth.size}",
              color = MaterialTheme.colorScheme.primary,
              style = MaterialTheme.typography.bodyMedium,
              modifier = Modifier.padding(bottom = 24.dp)
            )

          }

          item {
            CompanyPaymentPerLocationSearchView(state, onEvent)
          }
          item { Spacer(modifier = Modifier.height(8.dp)) }

          items(state.payment4CurrentLocationMonth) {
            CompanyPaymentPerLocationView(it, onEvent)
          }
        }
      }
  }

}


@Preview
@Composable
private fun CompanyPaymentPerLocationScreenPreview() {
  WasteManagementTheme {
    CompanyPaymentPerLocationScreen(
      state = companyPaymentPerLocationStateSuccess(),
      onEvent = {}
    )
  }
}

fun companyPaymentPerLocationStateSuccess() = CompanyPaymentPerLocationState.Success(
  payment4CurrentLocationMonth = listOf(payment4CurrentLocationMonth4Preview),
  company = company4Preview,
)
