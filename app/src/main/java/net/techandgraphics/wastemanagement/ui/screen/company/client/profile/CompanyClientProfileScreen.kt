package net.techandgraphics.wastemanagement.ui.screen.company.client.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyClientProfileScreen(
  state: CompanyClientProfileState,
  onEvent: (CompanyClientProfileEvent) -> Unit,
) {

  Scaffold(
    topBar = {
      TopAppBar(
        title = {},
        navigationIcon = {
          IconButton(
            onClick = { },
          ) {
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
        .padding(16.dp)
        .padding(it)
    ) {

      Text(
        text = "Client Profile",
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.padding(start = 8.dp, bottom = 16.dp)
      )

      when (state) {
        CompanyClientProfileState.Loading -> LoadingIndicatorView()
        is CompanyClientProfileState.Success -> CompanyClientProfileSuccess(state, onEvent)
      }

    }
  }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable private fun CompanyClientProfileSuccess(
  state: CompanyClientProfileState.Success,
  onEvent: (CompanyClientProfileEvent) -> Unit,
) {

  val account = state.account
  val context = LocalContext.current
  val hapticFeedback = LocalHapticFeedback.current

  AccountInfoView(account)

  LazyColumn {
    itemsIndexed(profileItems) { index, item ->
      Column(modifier = Modifier.clickable {
        when (item.event) {
          CompanyClientProfileEvent.Option.History -> {
            if (index == 2) {
              if (state.payments.isEmpty()) {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                context.toast("No payment history available")
              } else onEvent(item.event)
            } else onEvent(item.event)
          }

          CompanyClientProfileEvent.Option.Payment -> onEvent(item.event)
          CompanyClientProfileEvent.Option.Plan -> onEvent(item.event)
          else -> Unit
        }
      }) {
        Row(modifier = Modifier.padding(32.dp)) {
          BadgedBox(badge = {
            if (index == 2) Badge { Text(text = state.payments.size.toString()) }
          }) {
            Icon(painterResource(item.drawableRes), null)
          }
          Text(
            text = item.title,
            modifier = Modifier
              .padding(start = 16.dp)
              .weight(1f)
          )
          Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null)
        }

        if (index.plus(1) < profileItems.size)
          HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
      }
    }
  }
}


@Preview
@Composable
private fun CompanyClientProfileScreenPreview() {
  WasteManagementTheme {
    CompanyClientProfileScreen(
      state = CompanyClientProfileState.Success(account4Preview),
      onEvent = {}
    )
  }
}
