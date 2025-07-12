package net.techandgraphics.quantcal.ui.screen.company.client.profile

import android.content.res.Configuration
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import net.techandgraphics.quantcal.toast
import net.techandgraphics.quantcal.ui.screen.LoadingIndicatorView
import net.techandgraphics.quantcal.ui.screen.account4Preview
import net.techandgraphics.quantcal.ui.screen.company.AccountInfoEvent
import net.techandgraphics.quantcal.ui.screen.company.AccountInfoView
import net.techandgraphics.quantcal.ui.screen.company.CompanyInfoTopAppBarView
import net.techandgraphics.quantcal.ui.screen.company4Preview
import net.techandgraphics.quantcal.ui.screen.companyLocationWithDemographic4Preview
import net.techandgraphics.quantcal.ui.theme.QuantcalTheme


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
            onEvent(CompanyClientProfileEvent.Goto.BackHandler)
          }
        },
      ) {

        LazyColumn(
          contentPadding = it,
          modifier = Modifier.padding(vertical = 32.dp, horizontal = 8.dp)
        ) {

          item {
            Text(
              text = "Client Profile",
              style = MaterialTheme.typography.headlineSmall,
              modifier = Modifier.padding(bottom = 32.dp)
            )
          }


          item {
            AccountInfoView(state.account, state.demographic) { event ->
              when (event) {
                is AccountInfoEvent.Location ->
                  onEvent(CompanyClientProfileEvent.Goto.Location(event.id))

                is AccountInfoEvent.Phone ->
                  onEvent(CompanyClientProfileEvent.Button.Phone(event.contact))
              }
            }
          }

          item { Spacer(modifier = Modifier.height(24.dp)) }


          itemsIndexed(profileItems) { index, item ->
            if (item.event is CompanyClientProfileEvent.Option.Pending && state.pending.isEmpty()) return@itemsIndexed
            if (item.event is CompanyClientProfileEvent.Option.Invoice && state.payments.isEmpty()) return@itemsIndexed
            Card(
              modifier = Modifier.padding(4.dp),
              shape = CircleShape,
              colors = CardDefaults.elevatedCardColors(),
              onClick = {
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
              Row(modifier = Modifier.padding(16.dp)) {
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
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                  )
                }
                Text(
                  text = item.title,
                  modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
                )
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null)
                Spacer(modifier = Modifier.width(8.dp))
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
private fun CompanyClientProfileScreenPreview() {
  QuantcalTheme {
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
