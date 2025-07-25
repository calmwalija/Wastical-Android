package net.techandgraphics.wastical.ui.screen.company.client.location

import android.content.res.Configuration
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import net.techandgraphics.wastical.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastical.ui.screen.SnackbarThemed
import net.techandgraphics.wastical.ui.screen.account4Preview
import net.techandgraphics.wastical.ui.screen.company.AccountInfoEvent
import net.techandgraphics.wastical.ui.screen.company.AccountInfoView
import net.techandgraphics.wastical.ui.screen.company.CompanyInfoTopAppBarView
import net.techandgraphics.wastical.ui.screen.company4Preview
import net.techandgraphics.wastical.ui.screen.companyLocation4Preview
import net.techandgraphics.wastical.ui.screen.companyLocationWithDemographic4Preview
import net.techandgraphics.wastical.ui.screen.demographicArea4Preview
import net.techandgraphics.wastical.ui.screen.demographicStreet4Preview
import net.techandgraphics.wastical.ui.theme.WasticalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyClientLocationScreen(
  state: CompanyClientLocationState,
  onEvent: (CompanyClientLocationEvent) -> Unit,
) {

  val snackbarHostState = remember { SnackbarHostState() }
  val scope = rememberCoroutineScope()

  when (state) {
    CompanyClientLocationState.Loading -> LoadingIndicatorView()
    is CompanyClientLocationState.Success ->
      Scaffold(
        snackbarHost = {
          SnackbarHost(hostState = snackbarHostState) { SnackbarThemed(it) }
        },
        topBar = {
          CompanyInfoTopAppBarView(state.company) {
            onEvent(CompanyClientLocationEvent.Goto.BackHandler)
          }
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


          item {
            AccountInfoView(state.account, state.demographic) { event ->
              when (event) {
                is AccountInfoEvent.Location ->
                  onEvent(CompanyClientLocationEvent.Goto.Location(event.id))

                is AccountInfoEvent.Phone ->
                  onEvent(CompanyClientLocationEvent.Button.Phone(event.contact))
              }
            }
          }

          item { Spacer(modifier = Modifier.height(16.dp)) }

          items(state.demographics) { model ->
            CompanyClientLocationItem(
              modifier = Modifier.animateItem(),
              location = state.companyLocation,
              model = model,
            ) { event ->
              when (event) {
                is CompanyClientLocationEvent.Button.Change ->
                  scope.launch {
                    snackbarHostState.showSnackbar(
                      message = "Please confirm the location change request for this client ?",
                      actionLabel = "Confirm",
                      duration = SnackbarDuration.Short
                    ).also { result ->
                      when (result) {
                        SnackbarResult.Dismissed -> Unit
                        SnackbarResult.ActionPerformed -> onEvent(event)
                      }
                    }
                  }

                else -> onEvent(event)
              }
            }
          }
        }
      }
  }
}


@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CompanyClientLocationScreenPreview() {
  WasticalTheme {
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
