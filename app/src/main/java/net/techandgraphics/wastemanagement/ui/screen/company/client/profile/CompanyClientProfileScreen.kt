package net.techandgraphics.wastemanagement.ui.screen.company.client.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastemanagement.toast
import net.techandgraphics.wastemanagement.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastemanagement.ui.screen.account4Preview
import net.techandgraphics.wastemanagement.ui.screen.company.AccountInfoView
import net.techandgraphics.wastemanagement.ui.screen.company.CompanyInfoTopAppBarView
import net.techandgraphics.wastemanagement.ui.screen.company4Preview
import net.techandgraphics.wastemanagement.ui.screen.companyLocationWithDemographic4Preview
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyClientProfileScreen(
  state: CompanyClientProfileState,
  onEvent: (CompanyClientProfileEvent) -> Unit,
) {
  when (state) {
    CompanyClientProfileState.Loading -> LoadingIndicatorView()
    is CompanyClientProfileState.Success -> {

      val context = LocalContext.current
      val hapticFeedback = LocalHapticFeedback.current

      Scaffold(
        topBar = {
          CompanyInfoTopAppBarView(state.company) {
            onEvent(CompanyClientProfileEvent.Button.BackHandler)
          }
        },
        contentWindowInsets = WindowInsets.safeContent
      ) {

        LazyColumn(
          contentPadding = it,
          modifier = Modifier.padding(vertical = 32.dp)
        ) {

          item {
            Text(
              text = "Client Info",
              style = MaterialTheme.typography.headlineSmall,
              modifier = Modifier.padding(bottom = 32.dp)
            )
          }


          item { AccountInfoView(state.account, state.demographic) }

          itemsIndexed(profileItems) { index, item ->
            Column(modifier = Modifier.clickable {
              when (item.event) {
                CompanyClientProfileEvent.Option.History -> {
                  if (state.payments.isEmpty()) {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    context.toast("No payment history available")
                  } else onEvent(item.event)
                }

                CompanyClientProfileEvent.Option.Pending -> {
                  if (state.pending.isEmpty()) {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    context.toast("No pending payments available")
                  } else onEvent(item.event)
                }

                else -> onEvent(item.event)
              }
            }) {
              Row(
                modifier = Modifier.padding(
                  vertical = 32.dp,
                  horizontal = 8.dp
                )
              ) {
                BadgedBox(badge = {
                  when (item.event) {
                    CompanyClientProfileEvent.Option.History ->
                      Badge { Text(text = state.payments.size.toString()) }

                    CompanyClientProfileEvent.Option.Pending ->
                      Badge { Text(text = state.pending.size.toString()) }

                    else -> Unit
                  }
                }) {
                  Icon(
                    painterResource(item.drawableRes),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                  )
                }
                Text(
                  text = item.title,
                  modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
                )
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null)
              }

              HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
            }
          }
        }
      }
    }
  }
}


@Preview
@Composable
private fun CompanyClientProfileScreenPreview() {
  WasteManagementTheme {
    CompanyClientProfileScreen(
      state = CompanyClientProfileState.Success(
        company = company4Preview,
        account = account4Preview,
        demographic = companyLocationWithDemographic4Preview
      ),
      onEvent = {}
    )
  }
}
