package net.techandgraphics.wastemanagement.ui.screen.company.client.location

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeGestures
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastemanagement.ui.DottedBorderBox
import net.techandgraphics.wastemanagement.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastemanagement.ui.screen.account4Preview
import net.techandgraphics.wastemanagement.ui.screen.company.AccountInfoView
import net.techandgraphics.wastemanagement.ui.screen.company.CompanyInfoTopAppBarView
import net.techandgraphics.wastemanagement.ui.screen.company4Preview
import net.techandgraphics.wastemanagement.ui.screen.companyLocationWithDemographic4Preview
import net.techandgraphics.wastemanagement.ui.screen.demographicArea4Preview
import net.techandgraphics.wastemanagement.ui.screen.demographicStreet4Preview
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme

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
        bottomBar = {
          BottomAppBar(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface,
            tonalElevation = 0.dp
          ) {
            DottedBorderBox(
              modifier = Modifier
                .padding(8.dp)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary.copy(.06f))
                .clickable { }) {
              Text(text = "Change Location")
            }
          }
        },
        contentWindowInsets = WindowInsets.safeGestures
      ) {

        LazyColumn(
          contentPadding = it,
          modifier = Modifier.padding(vertical = 32.dp)
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

          items(state.demographics) { entity ->
            OutlinedCard(
              modifier = Modifier.padding(vertical = 4.dp),
              colors = CardDefaults.elevatedCardColors()
            ) {
              Row(
                modifier = Modifier
                  .clickable { }
                  .fillMaxWidth()
                  .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {

                RadioButton(selected = true, onClick = {

                })

                Column(
                  modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .weight(1f)
                ) {

                  Text(
                    text = entity.demographicArea.name,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.MiddleEllipsis
                  )

                  Text(
                    text = entity.demographicStreet.name,
                    style = MaterialTheme.typography.titleMedium
                  )

                }


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
  WasteManagementTheme {
    CompanyClientLocationScreen(
      state = CompanyClientLocationState.Success(
        company = company4Preview,
        account = account4Preview,
        accountDemographicArea = demographicArea4Preview,
        accountDemographicStreet = demographicStreet4Preview,
        demographics = (1..5).map { companyLocationWithDemographic4Preview },
        demographic = companyLocationWithDemographic4Preview
      ),
      onEvent = {}
    )
  }
}
