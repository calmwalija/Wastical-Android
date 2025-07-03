package net.techandgraphics.quantcal.ui.screen.company.client.location

import android.content.res.Configuration
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
import net.techandgraphics.quantcal.ui.screen.LoadingIndicatorView
import net.techandgraphics.quantcal.ui.screen.account4Preview
import net.techandgraphics.quantcal.ui.screen.company.AccountInfoView
import net.techandgraphics.quantcal.ui.screen.company.CompanyInfoTopAppBarView
import net.techandgraphics.quantcal.ui.screen.company4Preview
import net.techandgraphics.quantcal.ui.screen.companyLocation4Preview
import net.techandgraphics.quantcal.ui.screen.companyLocationWithDemographic4Preview
import net.techandgraphics.quantcal.ui.screen.demographicArea4Preview
import net.techandgraphics.quantcal.ui.screen.demographicStreet4Preview
import net.techandgraphics.quantcal.ui.theme.QuantcalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyClientLocationScreen(
  state: CompanyClientLocationState,
  onEvent: (CompanyClientLocationEvent) -> Unit,
) {
  when (state) {
    CompanyClientLocationState.Loading -> LoadingIndicatorView()
    is CompanyClientLocationState.Success ->
      Scaffold(
        topBar = {
          CompanyInfoTopAppBarView(state.company) {}
        },
      ) {

        LazyColumn(
          contentPadding = it,
          modifier = Modifier.padding(vertical = 32.dp, horizontal = 8.dp)
        ) {
          item {
            Text(
              text = "Change Location",
              style = MaterialTheme.typography.headlineSmall,
              modifier = Modifier.padding(bottom = 32.dp)
            )
          }

          item { AccountInfoView(state.account, state.demographic) }

          item { Spacer(modifier = Modifier.height(16.dp)) }

          items(state.demographics) {
            CompanyClientLocationItem(
              modifier = Modifier.animateItem(),
              location = state.companyLocation,
              model = it,
              onEvent = onEvent
            )
          }
        }
      }
  }
}


@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CompanyClientLocationScreenPreview() {
  QuantcalTheme {
    CompanyClientLocationScreen(
      state = companyClientLocationStateSuccess(),
      onEvent = {}
    )
  }
}


fun companyClientLocationStateSuccess() = CompanyClientLocationState.Success(
  company = company4Preview,
  account = account4Preview,
  accountDemographicArea = demographicArea4Preview,
  accountDemographicStreet = demographicStreet4Preview,
  demographics = (1..5).map { companyLocationWithDemographic4Preview },
  demographic = companyLocationWithDemographic4Preview,
  companyLocation = companyLocation4Preview
)
