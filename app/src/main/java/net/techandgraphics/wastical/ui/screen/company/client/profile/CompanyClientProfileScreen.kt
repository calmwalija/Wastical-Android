package net.techandgraphics.wastical.ui.screen.company.client.profile

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import net.techandgraphics.wastical.toast
import net.techandgraphics.wastical.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastical.ui.screen.SnackbarThemed
import net.techandgraphics.wastical.ui.screen.account4Preview
import net.techandgraphics.wastical.ui.screen.company.AccountInfoEvent
import net.techandgraphics.wastical.ui.screen.company.AccountInfoView
import net.techandgraphics.wastical.ui.screen.company.CompanyInfoTopAppBarView
import net.techandgraphics.wastical.ui.screen.company4Preview
import net.techandgraphics.wastical.ui.screen.companyLocationWithDemographic4Preview
import net.techandgraphics.wastical.ui.theme.WasticalTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyClientProfileScreen(
  state: CompanyClientProfileState,
  channel: Flow<CompanyClientProfileChannel>,
  onEvent: (CompanyClientProfileEvent) -> Unit,
) {

  val context = LocalContext.current
  val hapticFeedback = LocalHapticFeedback.current

  val lifecycleOwner = LocalLifecycleOwner.current
  LaunchedEffect(key1 = channel) {
    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
      channel.collect { event ->
        when (event) {

          is CompanyClientProfileChannel.Revoke.Error ->
            context.toast(event.error.message)

          CompanyClientProfileChannel.Revoke.Success -> {
            context.toast("Your request was submitted")
            onEvent(CompanyClientProfileEvent.Goto.BackHandler)
          }

          CompanyClientProfileChannel.NewAccount -> {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.Reject)
            context.toast("Opening New Account Before Sync Is Prohibited")
            onEvent(CompanyClientProfileEvent.Goto.BackHandler)
          }
        }
      }
    }
  }

  when (state) {
    CompanyClientProfileState.Loading -> LoadingIndicatorView()
    is CompanyClientProfileState.Success -> {


      val snackbarHostState = remember { SnackbarHostState() }
      val scope = rememberCoroutineScope()
      var showWarning by remember { mutableStateOf(false) }


      if (showWarning) {
        ModalBottomSheet(
          onDismissRequest = { showWarning = false },
          sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
          CompanyClientProfileRevokeView(onProceedWithCaution = {
            showWarning = false
            scope.launch {
              snackbarHostState.showSnackbar(
                message = "Please confirm the account revoke request for this client ?",
                actionLabel = "Confirm",
                duration = SnackbarDuration.Short
              ).also { result ->
                when (result) {
                  SnackbarResult.Dismissed -> Unit
                  SnackbarResult.ActionPerformed -> onEvent(CompanyClientProfileEvent.Option.Revoke)
                }
              }
            }
          }) { showWarning = false }
        }
      }


      Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) { SnackbarThemed(it) } },
        topBar = {
          CompanyInfoTopAppBarView(state.company) {
            onEvent(CompanyClientProfileEvent.Goto.BackHandler)
          }
        },
      ) {

        LazyColumn(
          contentPadding = it,
          modifier = Modifier.padding(16.dp)
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

          // Billing section
          item {
            Text(
              text = "Billing",
              style = MaterialTheme.typography.titleMedium,
              modifier = Modifier.padding(bottom = 12.dp)
            )
          }
          item {
            Card(
              modifier = Modifier.padding(horizontal = 4.dp),
              elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
              colors = CardDefaults.cardColors(),
              shape = MaterialTheme.shapes.large
            ) {
              Column {
                SectionRow(
                  title = "Invoice",
                  iconRes = profileItems.first { it.event == CompanyClientProfileEvent.Option.Invoice }.drawableRes
                ) {
                  if (state.payments.isEmpty()) {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    context.toast("No invoices available")
                  } else onEvent(CompanyClientProfileEvent.Option.Invoice)
                }
                HorizontalDivider()
                SectionRow(
                  title = "Pending Payments",
                  iconRes = profileItems.first { it.event == CompanyClientProfileEvent.Option.Pending }.drawableRes,
                  badgeCount = state.pending.size.takeIf { it > 0 }
                ) {
                  if (state.pending.isEmpty()) {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    context.toast("No pending payments available")
                  } else onEvent(CompanyClientProfileEvent.Option.Pending)
                }
                HorizontalDivider()
                SectionRow(
                  title = "Payment History",
                  iconRes = profileItems.first { it.event == CompanyClientProfileEvent.Option.History }.drawableRes,
                  badgeCount = state.payments.size.takeIf { it > 0 }
                ) {
                  if (state.payments.isEmpty()) {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    context.toast("No payment history available")
                  } else onEvent(CompanyClientProfileEvent.Option.History)
                }
              }
            }
          }

          // Quick actions grid
          item { Spacer(modifier = Modifier.height(24.dp)) }
          item {
            Text(
              text = "Actions",
              style = MaterialTheme.typography.titleMedium,
              modifier = Modifier.padding(bottom = 12.dp)
            )
          }
          item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
              Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ActionTile(
                  title = profileItems.first { it.event == CompanyClientProfileEvent.Option.Info }.title,
                  iconRes = profileItems.first { it.event == CompanyClientProfileEvent.Option.Info }.drawableRes,
                  modifier = Modifier.weight(1f)
                ) { onEvent(CompanyClientProfileEvent.Option.Info) }

                ActionTile(
                  title = profileItems.first { it.event == CompanyClientProfileEvent.Option.Location }.title,
                  iconRes = profileItems.first { it.event == CompanyClientProfileEvent.Option.Location }.drawableRes,
                  modifier = Modifier.weight(1f)
                ) { onEvent(CompanyClientProfileEvent.Option.Location) }
              }
              Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ActionTile(
                  title = profileItems.first { it.event == CompanyClientProfileEvent.Option.Payment }.title,
                  iconRes = profileItems.first { it.event == CompanyClientProfileEvent.Option.Payment }.drawableRes,
                  modifier = Modifier.weight(1f)
                ) { onEvent(CompanyClientProfileEvent.Option.Payment) }

                ActionTile(
                  title = profileItems.first { it.event == CompanyClientProfileEvent.Option.Plan }.title,
                  iconRes = profileItems.first { it.event == CompanyClientProfileEvent.Option.Plan }.drawableRes,
                  modifier = Modifier.weight(1f)
                ) { onEvent(CompanyClientProfileEvent.Option.Plan) }
              }
            }
          }

          // Danger zone
          item { Spacer(modifier = Modifier.height(24.dp)) }
          item {
            OutlinedCard(
              modifier = Modifier
                .padding(horizontal = 4.dp)
                .clickable { showWarning = true },
              shape = MaterialTheme.shapes.large
            ) {
              Row(
                modifier = Modifier
                  .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Row {
                  Icon(
                    painter = painterResource(profileItems.first { it.event == CompanyClientProfileEvent.Option.Revoke }.drawableRes),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(24.dp)
                  )
                  Text(
                    text = profileItems.first { it.event == CompanyClientProfileEvent.Option.Revoke }.title,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 16.dp)
                  )
                }
                Icon(
                  Icons.AutoMirrored.Filled.KeyboardArrowRight,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.error
                )
              }
            }
          }
        }
      }
    }
  }
}


@Composable
private fun SectionRow(
  title: String,
  iconRes: Int,
  badgeCount: Int? = null,
  onClick: () -> Unit,
) {
  Row(
    modifier = Modifier
      .padding(horizontal = 16.dp, vertical = 14.dp)
      .clickable { onClick() }
  ) {
    BadgedBox(badge = {
      if ((badgeCount ?: 0) > 0) Badge { Text(text = badgeCount.toString()) }
    }) {
      Icon(
        painter = painterResource(iconRes),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(24.dp)
      )
    }
    Text(
      text = title,
      modifier = Modifier
        .padding(start = 16.dp)
        .weight(1f)
    )
    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null)
    Spacer(modifier = Modifier.width(8.dp))
  }
}

@Composable
private fun ActionTile(
  title: String,
  iconRes: Int,
  modifier: Modifier = Modifier,
  onClick: () -> Unit,
) {
  Card(
    onClick = onClick,
    modifier = modifier,
    colors = CardDefaults.elevatedCardColors(),
    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
    shape = MaterialTheme.shapes.large
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Icon(
        painter = painterResource(iconRes),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(24.dp)
      )
      Spacer(modifier = Modifier.height(12.dp))
      Text(text = title, style = MaterialTheme.typography.bodyMedium)
    }
  }
}

@Composable
private fun StatCard(
  title: String,
  value: String,
  iconRes: Int,
  modifier: Modifier = Modifier,
  onClick: () -> Unit,
) {
  Card(
    onClick = onClick,
    modifier = modifier,
    colors = CardDefaults.cardColors(),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    shape = MaterialTheme.shapes.large
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row {
        Icon(
          painter = painterResource(iconRes),
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = title, style = MaterialTheme.typography.labelLarge)
      }
      Spacer(modifier = Modifier.height(6.dp))
      Text(text = value, style = MaterialTheme.typography.headlineSmall)
    }
  }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CompanyClientProfileScreenPreview() {
  WasticalTheme {
    CompanyClientProfileScreen(
      state = CompanyClientProfileState.Success(
        company = company4Preview,
        account = account4Preview,
        demographic = companyLocationWithDemographic4Preview
      ),
      channel = flow { }
    ) {}
  }
}
