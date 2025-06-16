package net.techandgraphics.wastemanagement.ui.screen.company.payment.location

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
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
          TopAppBar(
            title = { CompanyInfoTopAppBarView(state.company) },
            navigationIcon = {
              IconButton(onClick = { onEvent(CompanyPaymentPerLocationEvent.Button.BackHandler) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
              }
            },
            modifier = Modifier.shadow(0.dp),
            colors = TopAppBarDefaults.topAppBarColors()
          )
        },
      ) {

        Column(
          modifier = Modifier
            .padding(top = 16.dp)
            .padding(horizontal = 16.dp)
            .padding(it)
        ) {
          Text(
            text = "Payment per Location",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
              .fillMaxWidth()
              .padding(bottom = 8.dp)
              .padding(vertical = 8.dp, horizontal = 8.dp),
          )
          LazyColumn {
            items(state.payment4CurrentLocationMonth) {
              CompanyPaymentPerLocationView(it, onEvent)
            }
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
