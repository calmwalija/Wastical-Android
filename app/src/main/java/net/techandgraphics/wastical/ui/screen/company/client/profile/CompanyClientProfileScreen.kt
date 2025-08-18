package net.techandgraphics.wastical.ui.screen.company.client.profile

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.core.text.isDigitsOnly
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
  templates: Flow<List<Pair<String, String>>> = flow { emit(emptyList()) },
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
      var showMenuOptions by remember { mutableStateOf(false) }
      var showBroadcast by remember { mutableStateOf(false) }
      var customTitle by remember { mutableStateOf("") }
      var customBody by remember { mutableStateOf("") }


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
          CompanyInfoTopAppBarView(
            state.company,
            navActions = {
              IconButton(onClick = { showMenuOptions = true }) {
                Icon(
                  imageVector = Icons.Rounded.MoreVert,
                  contentDescription = null
                )
                DropdownMenu(
                  expanded = showMenuOptions,
                  onDismissRequest = { showMenuOptions = false }) {
                  DropdownMenuItem(
                    text = { Text(text = "Remove Client") },
                    onClick = { showWarning = true; showMenuOptions = false }
                  )
                  DropdownMenuItem(
                    text = { Text(text = "Send Notification") },
                    onClick = { showBroadcast = true; showMenuOptions = false }
                  )
                }
              }
            }) {
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
          item {
            Text(
              text = "Actions",
              style = MaterialTheme.typography.titleMedium,
              modifier = Modifier.padding(bottom = 12.dp)
            )
          }

          item {
            OutlinedCard(
              modifier = Modifier.padding(horizontal = 4.dp),
              elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            ) {
              Column {
                actionItems.forEach { item ->
                  SectionRow(
                    title = item.title,
                    iconRes = item.drawableRes,
                  ) {
                    when (item.event) {
                      CompanyClientProfileEvent.Option.Revoke -> showWarning = true
                      else -> onEvent(item.event)
                    }
                  }
                  HorizontalDivider()
                }
              }
            }
          }

          item { Spacer(modifier = Modifier.height(24.dp)) }

          item {
            Text(
              text = "Connect",
              style = MaterialTheme.typography.titleMedium,
              modifier = Modifier.padding(bottom = 12.dp)
            )
          }

          item {
            OutlinedCard(
              modifier = Modifier.padding(horizontal = 4.dp),
              elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            ) {
              Column {
                connectItems.forEach { item ->
                  when (item.event) {
                    CompanyClientProfileEvent.Option.Call,
                    CompanyClientProfileEvent.Option.WhatsApp,
                      ->
                      if (state.account.username.isDigitsOnly().not()) return@forEach

                    else -> Unit
                  }
                  SectionRow(
                    title = item.title,
                    iconRes = item.drawableRes,
                    onClick = {
                      when (item.event) {
                        CompanyClientProfileEvent.Option.WhatsApp ->
                          onEvent(CompanyClientProfileEvent.Goto.WhatsApp(state.account.username))

                        CompanyClientProfileEvent.Option.Call ->
                          onEvent(CompanyClientProfileEvent.Goto.Call(state.account.username))

                        else -> onEvent(item.event)
                      }
                    }
                  )
                  HorizontalDivider()
                }
              }
            }
          }

          item { Spacer(modifier = Modifier.height(24.dp)) }

          item {
            Text(
              text = "Billing",
              style = MaterialTheme.typography.titleMedium,
              modifier = Modifier.padding(bottom = 12.dp)
            )
          }

          item {
            OutlinedCard(
              modifier = Modifier.padding(horizontal = 4.dp),
              elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            ) {
              Column {
                billingItems.forEach { item ->
                  val badgeCount = when (item.event) {
                    CompanyClientProfileEvent.Option.Pending ->
                      state.pending.size.takeIf { count -> count > 0 } ?: 0

                    else -> null
                  }
                  SectionRow(
                    title = item.title,
                    iconRes = item.drawableRes,
                    badgeCount = badgeCount
                  ) {
                    when (item.event) {
                      CompanyClientProfileEvent.Option.History -> {
                        if (state.payments.isEmpty()) {
                          hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                          context.toast("No payment history available")
                        } else onEvent(item.event)
                      }

                      CompanyClientProfileEvent.Option.Pending -> {
                        if (state.pending.isEmpty()) {
                          hapticFeedback.performHapticFeedback(HapticFeedbackType.Reject)
                          context.toast("No pending payments available")
                        } else onEvent(item.event)
                      }

                      CompanyClientProfileEvent.Option.Invoice -> {
                        if (state.payments.isEmpty()) {
                          hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                          context.toast("No invoices available")
                        } else onEvent(item.event)
                      }

                      else -> Unit
                    }
                  }
                  HorizontalDivider()
                }
              }
            }
          }

        }
      }

      if (showBroadcast) {
        ModalBottomSheet(onDismissRequest = { showBroadcast = false }) {
          Text(
            text = "Select a template or create custom",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
          )
          val templatePairs = templates.collectAsState(initial = emptyList()).value
          val theTemplates = if (templatePairs.isEmpty()) listOf(
            "Welcome" to "Welcome to our service! We're glad to have you onboard.",
            "Outstanding Balance" to "You have an outstanding balance. Please make a payment at your earliest convenience."
          ) else templatePairs
          theTemplates.forEach { (t, b) ->
            androidx.compose.material3.Card(
              onClick = {
                onEvent(CompanyClientProfileEvent.Broadcast.Send(t, b))
                showBroadcast = false
              },
              modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .fillMaxWidth(),
              colors = CardDefaults.elevatedCardColors()
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Text(text = t)
                Spacer(Modifier.height(4.dp))
                Text(text = b)
              }
            }
          }
          Spacer(Modifier.height(16.dp))
          Text(
            text = "Custom message",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
          )
          OutlinedTextField(
            value = customTitle,
            onValueChange = { customTitle = it },
            label = { Text("Title") },
            modifier = Modifier
              .padding(horizontal = 16.dp, vertical = 8.dp)
              .fillMaxWidth()
          )
          OutlinedTextField(
            value = customBody,
            onValueChange = { customBody = it },
            label = { Text("Body") },
            modifier = Modifier
              .padding(horizontal = 16.dp)
              .fillMaxWidth()
          )
          Row(
            modifier = Modifier
              .padding(16.dp)
              .fillMaxWidth(),
            horizontalArrangement = Arrangement.End
          ) {
            androidx.compose.material3.Button(
              enabled = customTitle.isNotBlank() && customBody.isNotBlank(),
              onClick = {
                onEvent(CompanyClientProfileEvent.Broadcast.Send(customTitle, customBody))
                showBroadcast = false
                customTitle = ""
                customBody = ""
              }
            ) { Text("Send") }
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
      .clickable { onClick() }
      .padding(16.dp)
  ) {
    BadgedBox(badge = {
      badgeCount?.let { Badge { Text(text = badgeCount.toString()) } }
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
