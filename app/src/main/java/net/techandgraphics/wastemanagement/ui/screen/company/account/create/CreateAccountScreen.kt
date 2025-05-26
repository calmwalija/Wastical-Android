package net.techandgraphics.wastemanagement.ui.screen.company.account.create

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.techandgraphics.wastemanagement.R
import net.techandgraphics.wastemanagement.data.local.database.account.AccountTitle
import net.techandgraphics.wastemanagement.toAmount
import net.techandgraphics.wastemanagement.ui.InputField
import net.techandgraphics.wastemanagement.ui.screen.client.payment.appState
import net.techandgraphics.wastemanagement.ui.screen.company.account.create.CreateAccountEvent.Create.Button
import net.techandgraphics.wastemanagement.ui.screen.company.account.create.CreateAccountEvent.Create.Input
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable fun CreateAccountScreen(
  state: CreateAccountState,
  channel: Flow<CreateAccountChannel>,
  onEvent: (CreateAccountEvent) -> Unit
) {

  val context = LocalContext.current
  var showTitle by remember { mutableStateOf(false) }
  var showStreet by remember { mutableStateOf(false) }
  var showPlan by remember { mutableStateOf(false) }
  var addAltContact by remember { mutableStateOf(false) }

  var isProcessing by remember { mutableStateOf(false) }
  val hapticFeedback = LocalHapticFeedback.current


  val lifecycleOwner = LocalLifecycleOwner.current
  LaunchedEffect(key1 = channel) {
    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {},
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
      BottomAppBar(containerColor = Color.Transparent) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
          ElevatedButton(
            shape = RoundedCornerShape(8),
            modifier = Modifier.fillMaxWidth(.9f),
            onClick = { onEvent(Button.Submit) }) {
            Box {
              if (isProcessing) CircularProgressIndicator(modifier = Modifier.size(16.dp)) else {
                Text(text = "Create Account", modifier = Modifier.padding(8.dp))
              }
            }
          }
        }
      }
    },
    contentWindowInsets = ScaffoldDefaults
      .contentWindowInsets
      .exclude(WindowInsets.navigationBars)
      .exclude(WindowInsets.ime),
  ) {

    LazyColumn(
      modifier = Modifier
        .padding(it)
        .padding(horizontal = 16.dp)
    ) {
      item {
        Text(
          text = "Create Account",
          fontWeight = FontWeight.Bold,
          style = MaterialTheme.typography.headlineSmall,
          modifier = Modifier.padding(vertical = 24.dp)
        )
      }

      item {
        ElevatedCard(modifier = Modifier.padding(8.dp)) {
          Column(modifier = Modifier.padding(24.dp)) {
            InputField(
              imageVector = Icons.Outlined.AccountCircle,
              value = state.account.firstname,
              prompt = "type firstname",
              onValueChange = { onEvent(Input.Info(it, Input.Type.FirstName)) },
              keyboardType = KeyboardType.Text,
              leadingView = {
                Column(modifier = Modifier.padding(end = 8.dp)) {
                  Text(text = state.account.title.title)
                  DropdownMenu(showTitle, onDismissRequest = { showTitle = false }) {
                    AccountTitle.entries.forEach { title ->
                      DropdownMenuItem(text = { Text(text = title.title) }, onClick = {
                        onEvent(Input.Info(title.name, Input.Type.Title))
                        showTitle = false
                      })
                    }
                  }
                }
              },
              onClickAction = { showTitle = true }
            )

            InputField(
              painterResource = R.drawable.ic_account,
              value = state.account.lastname,
              prompt = "type lastname",
              onValueChange = { onEvent(Input.Info(it, Input.Type.Lastname)) },
              keyboardType = KeyboardType.Text,
            )

            InputField(
              painterResource = R.drawable.ic_tag,
              value = state.account.contact,
              prompt = "type contact number",
              onValueChange = { onEvent(Input.Info(it, Input.Type.Contact)) },
              keyboardType = KeyboardType.Phone,
              maskTransformation = "XXX-XXX-XXX",
              trailingView = {
                IconButton(
                  onClick = { addAltContact = true },
                  enabled = addAltContact.not()
                ) {
                  Icon(Icons.Default.Add, null)
                }
              })


            AnimatedVisibility(addAltContact) {
              InputField(
                painterResource = R.drawable.ic_alt_phone,
                value = state.account.altContact,
                prompt = "type alt contact number",
                onValueChange = { onEvent(Input.Info(it, Input.Type.AltContact)) },
                keyboardType = KeyboardType.Phone,
                maskTransformation = "XXX-XXX-XXX",
                trailingView = {
                  IconButton(onClick = { addAltContact = false }) {
                    Icon(Icons.Default.Close, null)
                  }
                })
            }

            InputField(
              painterResource = R.drawable.ic_payment,
              value = state.account.paymentPlan?.fee?.toAmount() ?: "",
              prompt = "",
              onValueChange = { onEvent(Input.Info(it, Input.Type.Plan)) },
              leadingView = {
                Column(modifier = Modifier.padding(end = 4.dp)) {
                  state.account.paymentPlan?.let { Text(text = it.period.name.plus(" @")) }
                  DropdownMenu(showPlan, onDismissRequest = { showPlan = false }) {
                    state.appState.paymentPlans.forEach { plan ->
                      DropdownMenuItem(
                        text = { Text(text = plan.fee.toAmount()) },
                        onClick = {
                          onEvent(Input.Info(plan, Input.Type.Plan))
                          showPlan = false
                        })
                    }
                  }
                }


              },
              readOnly = true,
              onClickAction = { showPlan = true })


            InputField(
              painterResource = R.drawable.ic_house,
              value = state.account.street?.name ?: "",
              prompt = "",
              onValueChange = { onEvent(Input.Info(it, Input.Type.Street)) },
              leadingView = {
                DropdownMenu(showStreet, onDismissRequest = { showStreet = false }) {
                  state.account.companyStreets.forEach { street ->
                    DropdownMenuItem(text = { Text(text = street.name) }, onClick = {
                      onEvent(Input.Info(street, Input.Type.Street))
                      showStreet = false
                    })
                  }
                }
              },
              readOnly = true,
              onClickAction = { showStreet = true })

            Spacer(modifier = Modifier.height(16.dp))


          }
        }
      }

    }
  }
}

@Preview(showBackground = true) @Composable private fun CreateAccountScreenPreview() {
  WasteManagementTheme {
    CreateAccountScreen(
      state = CreateAccountState(
        appState = appState(LocalContext.current)
      ), channel = flow { }, onEvent = {})
  }
}
