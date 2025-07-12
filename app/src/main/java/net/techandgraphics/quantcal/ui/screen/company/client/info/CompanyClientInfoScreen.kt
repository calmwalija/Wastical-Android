package net.techandgraphics.quantcal.ui.screen.company.client.info

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import net.techandgraphics.quantcal.data.local.database.account.AccountTitle
import net.techandgraphics.quantcal.toast
import net.techandgraphics.quantcal.ui.screen.LoadingIndicatorView
import net.techandgraphics.quantcal.ui.screen.SnackbarThemed
import net.techandgraphics.quantcal.ui.screen.account4Preview
import net.techandgraphics.quantcal.ui.screen.company.AccountInfoView
import net.techandgraphics.quantcal.ui.screen.company.CompanyInfoTopAppBarView
import net.techandgraphics.quantcal.ui.screen.company.client.info.CompanyClientInfoEvent.Button
import net.techandgraphics.quantcal.ui.screen.company.client.info.CompanyClientInfoEvent.Input
import net.techandgraphics.quantcal.ui.screen.company4Preview
import net.techandgraphics.quantcal.ui.screen.companyLocationWithDemographic4Preview
import net.techandgraphics.quantcal.ui.theme.Muted
import net.techandgraphics.quantcal.ui.theme.QuantcalTheme

@Composable fun CompanyClientInfoScreen(
  state: CompanyClientInfoState,
  channel: Flow<CompanyClientInfoChannel>,
  onEvent: (CompanyClientInfoEvent) -> Unit,
) {

  when (state) {
    CompanyClientInfoState.Loading -> LoadingIndicatorView()
    is CompanyClientInfoState.Success -> {

      val context = LocalContext.current

      val lifecycleOwner = LocalLifecycleOwner.current
      LaunchedEffect(key1 = channel) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
          channel.collect { event ->
            when (event) {
              is CompanyClientInfoChannel.Submit.Error ->
                context.toast(event.error.message)

              CompanyClientInfoChannel.Submit.Success -> {
                context.toast("Your request was submitted")
                onEvent(Button.BackHandler)
              }
            }
          }
        }
      }

      val textFieldDefaults = TextFieldDefaults.colors(
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        focusedPlaceholderColor = Muted,
        unfocusedPlaceholderColor = Muted
      )

      var showTitle by remember { mutableStateOf(false) }
      val snackbarHostState = remember { SnackbarHostState() }
      val scope = rememberCoroutineScope()


      Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) { SnackbarThemed(it) } },
        topBar = {
          CompanyInfoTopAppBarView(state.company) {
            onEvent(Button.BackHandler)
          }
        },
      ) {

        LazyColumn(
          contentPadding = it, modifier = Modifier.padding(vertical = 32.dp, horizontal = 8.dp)
        ) {

          item {
            Text(
              text = "Edit Profile",
              style = MaterialTheme.typography.headlineSmall,
              modifier = Modifier.padding(bottom = 32.dp)
            )
          }


          item { AccountInfoView(state.oldAccount, state.demographic, { }) }

          item { Spacer(modifier = Modifier.height(24.dp)) }

          item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
              var fSize by remember { mutableIntStateOf(0) }
              LaunchedEffect(state.account.firstname) {
                fSize = state.account.firstname.length
              }
              Text(
                text = "First Name",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp),
              )

              TextField(
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                  Box(modifier = Modifier.padding(start = 8.dp)) {
                    TextButton(onClick = { showTitle = true }) {
                      Text(
                        text = state.account.title.title,
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.titleMedium
                      )

                      DropdownMenu(expanded = showTitle, onDismissRequest = { showTitle = false }) {
                        AccountTitle.entries.filterNot { title -> title == state.account.title }
                          .forEach { title ->
                            DropdownMenuItem(
                              text = { Text(text = title.title) },
                              onClick = {
                                onEvent(
                                  Input.Type(
                                    newValue = title.name,
                                    ofType = Input.OfType.Title
                                  )
                                )
                                showTitle = false
                              },
                            )
                          }
                      }
                    }
                  }
                },
                placeholder = { Text(text = state.account.firstname) },
                shape = RoundedCornerShape(24),
                maxLines = 1,
                value = state.account.firstname,
                onValueChange = { newValue ->
                  if (newValue.length < 62)
                    onEvent(
                      Input.Type(
                        newValue = newValue, ofType = Input.OfType.FName
                      )
                    )
                },
                colors = textFieldDefaults,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                trailingIcon = {
                  Text(
                    text = "$fSize/62",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(end = 24.dp, start = 8.dp)
                  )
                },
              )
            }
          }

          item { Spacer(modifier = Modifier.height(24.dp)) }

          item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
              var lSize by remember { mutableIntStateOf(0) }
              LaunchedEffect(state.account.lastname) {
                lSize = state.account.lastname.length
              }
              Text(
                text = "Last Name",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp),
              )
              TextField(
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = state.account.lastname) },
                shape = RoundedCornerShape(24),
                maxLines = 1,
                value = state.account.lastname,
                onValueChange = { newValue ->
                  if (newValue.length < 62)
                    onEvent(
                      Input.Type(
                        newValue = newValue, ofType = Input.OfType.LName
                      )
                    )
                },
                colors = textFieldDefaults,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                trailingIcon = {
                  Text(
                    text = "$lSize/62",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(end = 24.dp, start = 8.dp)
                  )
                },
              )
            }
          }

          item { Spacer(modifier = Modifier.height(24.dp)) }

          item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
              var cSize by remember { mutableIntStateOf(0) }
              LaunchedEffect(state.account.username) {
                cSize = state.account.username.length
              }
              Text(
                text = "Phone Number",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
              )
              TextField(
                maxLines = 1,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = state.account.username) },
                shape = RoundedCornerShape(24),
                value = state.account.username,
                onValueChange = { newValue ->
                  if (newValue.length <= 10)
                    onEvent(
                      Input.Type(
                        newValue = newValue, ofType = Input.OfType.Contact
                      )
                    )
                },
                colors = textFieldDefaults,
                trailingIcon = {
                  Text(
                    text = "$cSize/10",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(end = 24.dp, start = 8.dp)
                  )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
              )
            }
          }

          item { Spacer(modifier = Modifier.height(24.dp)) }

          item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {

              var eSize by remember { mutableIntStateOf(0) }
              LaunchedEffect(state.account.email) { eSize = (state.account.email ?: "").length }

              Text(
                text = "Email Address",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
              )
              TextField(
                trailingIcon = {
                  Text(
                    text = "$eSize/48",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(end = 24.dp, start = 8.dp)
                  )
                },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1,
                placeholder = { Text(text = state.account.email ?: "") },
                shape = RoundedCornerShape(24),
                value = state.account.email ?: "",
                onValueChange = { newValue ->
                  if (newValue.length < 48)
                    onEvent(
                      Input.Type(
                        newValue = newValue, ofType = Input.OfType.Email
                      )
                    )
                },
                colors = textFieldDefaults,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
              )
            }
          }

          item { Spacer(modifier = Modifier.height(48.dp)) }

          item {
            Row(
              modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
              horizontalArrangement = Arrangement.Center
            ) {
              Button(
                modifier = Modifier.weight(1f),
                onClick = {
                  scope.launch {
                    snackbarHostState.showSnackbar(
                      message = "Please confirm the account change request for this client ?",
                      actionLabel = "Confirm",
                      duration = SnackbarDuration.Short
                    ).also { result ->
                      when (result) {
                        SnackbarResult.Dismissed -> Unit
                        SnackbarResult.ActionPerformed -> onEvent(Button.Submit)
                      }
                    }
                  }
                }) {
                Box {
                  Text(text = "Change Account Info")
                }
              }

              Spacer(modifier = Modifier.width(8.dp))

              OutlinedButton(
                onClick = { onEvent(Button.BackHandler) }) {
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


@Preview @Composable private fun CompanyClientInfoScreenPreview() {
  QuantcalTheme {
    CompanyClientInfoScreen(
      state = companyClientInfoStateSuccess(),
      channel = flow { }
    ) {}
  }
}

fun companyClientInfoStateSuccess() = CompanyClientInfoState.Success(
  company = company4Preview,
  account = account4Preview,
  demographic = companyLocationWithDemographic4Preview,
)
