package net.techandgraphics.wastemanagement.ui.screen.company.client.location

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastemanagement.R
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
          TopAppBar(
            title = { CompanyInfoTopAppBarView(state.company) },
            navigationIcon = {
              IconButton(onClick = { }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
              }
            },
            modifier = Modifier.shadow(0.dp),
            colors = TopAppBarDefaults.topAppBarColors()
          )
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
        }
      ) {
        Column(modifier = Modifier.padding(it)) {

          Text(
            text = "Change Location",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
          )

          AccountInfoView(state.account)

          ElevatedCard(
            modifier = Modifier
              .padding(top = 16.dp, bottom = 8.dp)
              .padding(horizontal = 24.dp)
          ) {
            Row(
              modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                painterResource(R.drawable.ic_location),
                contentDescription = null,
                modifier = Modifier
                  .size(62.dp)
                  .padding(8.dp),
                tint = MaterialTheme.colorScheme.secondary
              )

              Column {
                Text(
                  text = "Current Location",
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.secondary
                )
                Text(text = state.accountDemographicArea.name)
                Text(
                  text = state.accountDemographicStreet.name,
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.primary
                )
              }
            }
          }

          LazyColumn(contentPadding = PaddingValues(8.dp)) {
            items(state.demographics) { entity ->
              OutlinedCard(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
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
}


@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun CompanyClientLocationScreenPreview() {
  WasteManagementTheme {
    CompanyClientLocationScreen(
      state = CompanyClientLocationState.Success(
        company = company4Preview,
        account = account4Preview,
        accountDemographicArea = demographicArea4Preview,
        accountDemographicStreet = demographicStreet4Preview,
        demographics = (1..5).map { companyLocationWithDemographic4Preview }
      ),
      onEvent = {}
    )
  }
}
