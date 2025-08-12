package net.techandgraphics.wastical.ui.screen.company.client.create

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import net.techandgraphics.wastical.data.local.database.account.AccountTitle
import net.techandgraphics.wastical.domain.model.account.AccountInfoUiModel
import net.techandgraphics.wastical.toAmount
import net.techandgraphics.wastical.toast
import net.techandgraphics.wastical.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastical.ui.screen.SnackbarThemed
import net.techandgraphics.wastical.ui.screen.company.CompanyInfoTopAppBarView
import net.techandgraphics.wastical.ui.screen.company.client.CompanyClientConflictDialog
import net.techandgraphics.wastical.ui.screen.company.client.create.CompanyCreateClientEvent.Input
import net.techandgraphics.wastical.ui.screen.company4Preview
import net.techandgraphics.wastical.ui.screen.companyLocationWithDemographic4Preview
import net.techandgraphics.wastical.ui.screen.paymentPlan4Preview
import net.techandgraphics.wastical.ui.theme.Green
import net.techandgraphics.wastical.ui.theme.Muted
import net.techandgraphics.wastical.ui.theme.WasticalTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable fun CompanyCreateClientScreen(
  state: CompanyCreateClientState,
  channel: Flow<CompanyCreateClientChannel>,
  onEvent: (CompanyCreateClientEvent) -> Unit,
) {

  when (state) {
    CompanyCreateClientState.Loading -> LoadingIndicatorView()
    is CompanyCreateClientState.Success -> {

      val context = LocalContext.current
      var isUnique by remember { mutableStateOf(true) }
      val accounts = remember { mutableStateListOf<AccountInfoUiModel>() }
      val hapticFeedback = LocalHapticFeedback.current
      val snackbarHostState = remember { SnackbarHostState() }
      val scope = rememberCoroutineScope()

      val textFieldDefaults = TextFieldDefaults.colors(
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        focusedPlaceholderColor = Muted,
        unfocusedPlaceholderColor = Muted
      )


      val lifecycleOwner = LocalLifecycleOwner.current
      LaunchedEffect(key1 = channel) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
          channel.collectLatest { event ->
            when (event) {
              is CompanyCreateClientChannel.Error -> {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                context.toast(event.error.message)
              }

              is CompanyCreateClientChannel.Success ->
                onEvent(CompanyCreateClientEvent.Goto.BackHandler)

              is CompanyCreateClientChannel.Input.Unique.Conflict -> {
                accounts.clear()
                accounts.addAll(event.accounts)
                isUnique = false
              }

              CompanyCreateClientChannel.Input.Unique.Ok -> isUnique = true
            }
          }
        }
      }


      if (isUnique.not()) {
        hapticFeedback.performHapticFeedback(HapticFeedbackType.Reject)
        ModalBottomSheet(onDismissRequest = {
          isUnique = true
          onEvent(Input.Info("", Input.Type.Contact))
        }) {
          CompanyClientConflictDialog(accounts) {
            isUnique = true
            onEvent(Input.Info("", Input.Type.Contact))
          }
        }
      }


      Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) { SnackbarThemed(it) } },
        topBar = {
          CompanyInfoTopAppBarView(state.company) {
            onEvent(CompanyCreateClientEvent.Goto.BackHandler)
          }
        },
      ) {

        LazyColumn(
          contentPadding = it,
          modifier = Modifier.padding(16.dp)
        ) {

          item {
            Text(
              text = "Create Account",
              style = MaterialTheme.typography.headlineSmall,
              modifier = Modifier.padding(bottom = 16.dp)
            )
          }

          item {
            CompanyCreateLocationView(state.demographic)
          }

          item {
            var showTitle by remember { mutableStateOf(false) }
            Column(modifier = Modifier) {
              var fSize by remember { mutableIntStateOf(0) }
              LaunchedEffect(state.firstname) {
                fSize = state.firstname.length
              }
              Text(
                text = "First Name",
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.primary
              )

              TextField(
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                  Box(modifier = Modifier.padding(start = 8.dp)) {
                    TextButton(onClick = { showTitle = true }) {
                      Text(text = state.title.title)
                      DropdownMenu(
                        showTitle,
                        onDismissRequest = { showTitle = false }) {
                        AccountTitle.entries.forEach { title ->
                          DropdownMenuItem(
                            text = { Text(text = title.title) },
                            onClick = {
                              showTitle = false
                              onEvent(Input.Info(title.name, Input.Type.Title))
                            })
                        }
                      }
                    }
                  }
                },
                placeholder = { Text(text = state.firstname.trim().ifEmpty { "Input Firstname" }) },
                shape = RoundedCornerShape(8),
                maxLines = 1,
                value = state.firstname,
                onValueChange = { newValue ->
                  if (newValue.length < 25)
                    onEvent(Input.Info(newValue, Input.Type.FirstName))
                  else hapticFeedback.performHapticFeedback(HapticFeedbackType.Reject)
                },
                colors = textFieldDefaults,
                keyboardOptions = KeyboardOptions(
                  capitalization = KeyboardCapitalization.Words,
                  imeAction = ImeAction.Next
                ),
                trailingIcon = {
                  Text(
                    text = "$fSize/24",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(end = 24.dp, start = 8.dp)
                  )
                },
              )
            }
          }


          item { Spacer(modifier = Modifier.height(24.dp)) }


          item {
            Column(modifier = Modifier) {
              var lSize by remember { mutableIntStateOf(0) }
              LaunchedEffect(state.lastname) {
                lSize = state.lastname.length
              }
              Text(
                text = "Last Name",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp),
              )
              TextField(
                leadingIcon = {
                  Icon(
                    imageVector = if (lSize < 3) Icons.Rounded.Close else Icons.Rounded.Check,
                    contentDescription = null,
                    tint = if (lSize < 3) Color.Red else Green
                  )
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = state.lastname.trim().ifEmpty { "Input Lastname" }) },
                shape = RoundedCornerShape(8),
                maxLines = 1,
                value = state.lastname,
                onValueChange = { newValue ->
                  if (newValue.length < 25)
                    onEvent(Input.Info(newValue, Input.Type.Lastname))
                  else hapticFeedback.performHapticFeedback(HapticFeedbackType.Reject)
                },
                colors = textFieldDefaults,
                keyboardOptions = KeyboardOptions(
                  capitalization = KeyboardCapitalization.Words,
                  imeAction = ImeAction.Next
                ),
                trailingIcon = {
                  Text(
                    text = "$lSize/24",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(end = 24.dp, start = 8.dp)
                  )
                },
              )
            }
          }


          item { Spacer(modifier = Modifier.height(24.dp)) }

          item {
            Column(modifier = Modifier) {
              var cSize by remember { mutableIntStateOf(0) }
              LaunchedEffect(state.contact) {
                cSize = state.contact.length
              }
              Text(
                text = "Contact",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
              )
              TextField(
                leadingIcon = {
                  Icon(
                    imageVector = if (isUnique && cSize > 8) Icons.Rounded.Check else Icons.Rounded.Close,
                    contentDescription = null,
                    tint = if (isUnique && cSize > 8) Green else Color.Red
                  )
                },
                maxLines = 1,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = state.contact.trim().ifEmpty { "Input Contact" }) },
                shape = RoundedCornerShape(8),
                value = state.contact,
                onValueChange = { newValue ->
                  if (newValue.length <= 10)
                    onEvent(Input.Info(newValue.trim(), Input.Type.Contact))
                  else hapticFeedback.performHapticFeedback(HapticFeedbackType.Reject)
                },
                colors = textFieldDefaults,
                trailingIcon = {
                  Text(
                    text = "$cSize/10",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(end = 24.dp, start = 8.dp)
                  )
                },
                keyboardOptions = KeyboardOptions(
                  keyboardType = KeyboardType.Number,
                  imeAction = ImeAction.Done
                )
              )
            }
          }

          item {
            Text(
              text = "Payment Plan",
              style = MaterialTheme.typography.titleMedium,
              modifier = Modifier.padding(top = 32.dp, bottom = 16.dp),
              color = MaterialTheme.colorScheme.primary,
            )
          }

          item {

            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
              expanded = expanded,
              onExpandedChange = { onExpandedChange -> expanded = onExpandedChange }
            ) {
              val plan = state.paymentPlans.find { plan -> plan.id == state.planId }
                ?: return@ExposedDropdownMenuBox
              TextField(
                value = plan.fee.toAmount(),
                shape = RoundedCornerShape(8),
                onValueChange = {},
                readOnly = true,
                placeholder = { Text(text = plan.fee.toAmount()) },
                colors = textFieldDefaults,
                trailingIcon = {
                  ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                  .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                  .fillMaxWidth()
                  .onFocusChanged { focusState ->
                  }
              )

              ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
              ) {
                state.paymentPlans.forEachIndexed { index, paymentPlan ->
                  DropdownMenuItem(
                    text = {
                      Row(
                        modifier = Modifier
                          .clickable {
                            expanded = false
                            onEvent(Input.Info(paymentPlan.id, Input.Type.Plan))
                          }
                          .fillMaxWidth()
                          .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                      ) {

                        RadioButton(
                          selected = paymentPlan.id == state.planId,
                          onClick = {
                            expanded = false
                            onEvent(Input.Info(paymentPlan.id, Input.Type.Plan))
                          }
                        )

                        Column(
                          modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .weight(1f)
                        ) {

                          Text(
                            text = "Payment Plan ${index.plus(1)}",
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.MiddleEllipsis
                          )

                          Text(
                            text = paymentPlan.name,
                            style = MaterialTheme.typography.titleMedium
                          )

                        }

                        Box(
                          modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .background(MaterialTheme.colorScheme.secondary.copy(.1f))
                            .fillMaxHeight(.05f)
                            .width(1.dp)
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                          Text(
                            text = paymentPlan.fee.toAmount(),
                            color = MaterialTheme.colorScheme.primary
                          )

                          Text(
                            text = paymentPlan.period.name,
                            style = MaterialTheme.typography.bodySmall
                          )
                        }

                      }
                    },
                    onClick = {
                      expanded = false
                    }
                  )
                }
              }
            }
          }


          item {
            Row(
              modifier = Modifier
                .padding(vertical = 32.dp)
                .fillMaxWidth(),
              horizontalArrangement = Arrangement.Center
            ) {
              Button(
                enabled = state.lastname.trim().length > 2 && isUnique,
                modifier = Modifier.weight(1f),
                onClick = {
                  scope.launch {
                    snackbarHostState.showSnackbar(
                      message = "Please confirm the account creation request ?",
                      actionLabel = "Confirm",
                      duration = SnackbarDuration.Short
                    ).also { result ->
                      when (result) {
                        SnackbarResult.Dismissed -> Unit
                        SnackbarResult.ActionPerformed ->
                          onEvent(CompanyCreateClientEvent.Button.Submit)
                      }
                    }
                  }
                }) {
                Box {
                  Text(text = "Create Account")
                }
              }

              Spacer(modifier = Modifier.width(8.dp))

              OutlinedButton(
                onClick = { onEvent(CompanyCreateClientEvent.Goto.BackHandler) }) {
                Box {
                  Text(text = "Cancel")
                }
              }

            }
          }

        }
      }
    }
  }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CompanyCreateClientScreenPreview() {
  WasticalTheme {
    CompanyCreateClientScreen(
      state = CompanyCreateClientState.Success(
        demographic = companyLocationWithDemographic4Preview,
        paymentPlans = (1..4).map { paymentPlan4Preview },
        company = company4Preview
      ),
      channel = flow { },
      onEvent = {})
  }
}
