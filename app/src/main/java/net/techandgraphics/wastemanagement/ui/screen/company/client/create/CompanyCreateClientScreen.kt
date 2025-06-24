package net.techandgraphics.wastemanagement.ui.screen.company.client.create

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeGestures
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
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
import net.techandgraphics.wastemanagement.R
import net.techandgraphics.wastemanagement.data.local.database.account.AccountTitle
import net.techandgraphics.wastemanagement.toAmount
import net.techandgraphics.wastemanagement.toast
import net.techandgraphics.wastemanagement.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastemanagement.ui.screen.company.CompanyInfoTopAppBarView
import net.techandgraphics.wastemanagement.ui.screen.company.client.create.CompanyCreateClientEvent.Input
import net.techandgraphics.wastemanagement.ui.screen.company4Preview
import net.techandgraphics.wastemanagement.ui.screen.companyLocationWithDemographic4Preview
import net.techandgraphics.wastemanagement.ui.screen.paymentPlan4Preview
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme
import net.techandgraphics.wastemanagement.ui.transformation.CountryCodeMaskTransformation


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
      var addAltContact by remember { mutableStateOf(false) }
      var isProcessing by remember { mutableStateOf(false) }
      val hapticFeedback = LocalHapticFeedback.current

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
            }
          }
        }
      }


      Scaffold(
        topBar = {
          CompanyInfoTopAppBarView(state.company) {
            onEvent(CompanyCreateClientEvent.Goto.BackHandler)
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
              text = "Create Account",
              style = MaterialTheme.typography.headlineSmall,
              modifier = Modifier.padding(bottom = 32.dp)
            )
          }


          item {
            Text(
              text = "Personal Information",
              style = MaterialTheme.typography.titleMedium,
              modifier = Modifier.padding(bottom = 16.dp)
            )
          }

          item {
            var showTitle by remember { mutableStateOf(false) }
            OutlinedCard(
              onClick = { showTitle = true },
              shape = RoundedCornerShape(8),
              border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
              modifier = Modifier.padding(vertical = 8.dp)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 16.dp)
              ) {

                Icon(
                  painterResource(R.drawable.ic_title),
                  contentDescription = null,
                  modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .size(28.dp),
                  tint = MaterialTheme.colorScheme.primary
                )

                Text(
                  text = "Title",
                  modifier = Modifier.padding(horizontal = 16.dp)
                )
                Icon(
                  Icons.AutoMirrored.Filled.KeyboardArrowRight,
                  contentDescription = null,
                  modifier = Modifier.size(20.dp)
                )
                Text(
                  text = state.title.title,
                  modifier = Modifier
                    .padding(start = 16.dp)
                    .fillMaxWidth(),
                )

                DropdownMenu(showTitle, onDismissRequest = { showTitle = false }) {
                  AccountTitle.entries.forEach {
                    DropdownMenuItem(text = { Text(text = it.title) }, onClick = {
                      showTitle = false
                      onEvent(Input.Info(it.name, Input.Type.Title))
                    })
                  }
                }
              }
            }
          }

          item {
            OutlinedTextField(
              value = state.firstname,
              onValueChange = { onEvent(Input.Info(it, Input.Type.FirstName)) },
              modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth(),
              label = { Text(text = "input firstname") },
              keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
              leadingIcon = {
                Icon(
                  painterResource(R.drawable.ic_supervisor_account),
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary
                )
              }
            )
          }

          item {
            OutlinedTextField(
              value = state.lastname,
              onValueChange = { onEvent(Input.Info(it, Input.Type.Lastname)) },
              modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth(),
              label = { Text(text = "input lastname") },
              keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
              leadingIcon = {
                Icon(
                  painterResource(R.drawable.ic_account),
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary
                )
              }
            )
          }


          item {
            OutlinedTextField(
              value = state.contact,
              onValueChange = { onEvent(Input.Info(it, Input.Type.Contact)) },
              modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth(),
              label = { Text(text = "input contact number") },
              visualTransformation = CountryCodeMaskTransformation("XXX-XXX-XXX"),
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
              trailingIcon = {
                if (addAltContact.not())
                  IconButton(onClick = { addAltContact = true }) {
                    Icon(Icons.Default.Add, null)
                  }
              },
              leadingIcon = {
                Icon(
                  painterResource(R.drawable.ic_tag),
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary
                )
              }
            )
          }

          if (addAltContact)
            item {
              OutlinedTextField(
                value = state.altContact,
                onValueChange = { onEvent(Input.Info(it, Input.Type.AltContact)) },
                modifier = Modifier
                  .padding(vertical = 8.dp)
                  .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                visualTransformation = CountryCodeMaskTransformation("XXX-XXX-XXX"),
                label = { Text(text = "input alt contact number") },
                trailingIcon = {
                  IconButton(onClick = { addAltContact = false }) {
                    Icon(Icons.Default.Close, null)
                  }
                },
                leadingIcon = {
                  Icon(
                    painterResource(R.drawable.ic_alt_phone),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                  )
                }
              )
            }



          item {
            Text(
              text = "Location Information",
              style = MaterialTheme.typography.titleMedium,
              modifier = Modifier.padding(top = 32.dp, bottom = 16.dp)
            )
          }

          item {
            var showLocations by remember { mutableStateOf(false) }
            OutlinedCard(
              onClick = { showLocations = true },
              shape = RoundedCornerShape(8),
              border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 16.dp)
              ) {

                Icon(
                  painterResource(R.drawable.ic_house),
                  contentDescription = null,
                  modifier = Modifier.padding(horizontal = 8.dp),
                  tint = MaterialTheme.colorScheme.primary
                )

                Text(
                  text = "Location",
                  modifier = Modifier.padding(horizontal = 16.dp)
                )
                Icon(
                  Icons.AutoMirrored.Filled.KeyboardArrowRight,
                  contentDescription = null,
                  modifier = Modifier.size(20.dp)
                )
                Text(
                  text = if (state.companyLocationId == -1L)
                    state.demographics.first().demographicStreet.name else {
                    state.demographics.first { it.location.id == state.companyLocationId }.demographicStreet.name
                  },
                  modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                  maxLines = 1,
                  overflow = TextOverflow.MiddleEllipsis
                )
                DropdownMenu(showLocations, onDismissRequest = { showLocations = false }) {
                  state.demographics.forEach {
                    DropdownMenuItem(text = { Text(text = it.demographicStreet.name) }, onClick = {
                      showLocations = false
                      onEvent(Input.Info(it.location.id, Input.Type.Location))
                    })
                  }
                }
              }
            }
          }


          item {
            Text(
              text = "Payment Plan Information",
              style = MaterialTheme.typography.titleMedium,
              modifier = Modifier.padding(top = 32.dp, bottom = 16.dp)
            )
          }

          itemsIndexed(state.paymentPlans) { index, paymentPlan ->
            OutlinedCard(
              modifier = Modifier.padding(vertical = 4.dp),
              colors = CardDefaults.elevatedCardColors()
            ) {
              Row(
                modifier = Modifier
                  .clickable { onEvent(Input.Info(paymentPlan.id, Input.Type.Plan)) }
                  .fillMaxWidth()
                  .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {

                RadioButton(
                  selected = paymentPlan.id == state.planId,
                  onClick = { onEvent(Input.Info(paymentPlan.id, Input.Type.Plan)) }
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
            }
          }

          item {
            Row(
              modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth(),
              horizontalArrangement = Arrangement.Center
            ) {
              ElevatedButton(
                enabled = state.lastname.trim().isNotEmpty(),
                shape = RoundedCornerShape(8),
                modifier = Modifier.fillMaxWidth(),
                onClick = { onEvent(CompanyCreateClientEvent.Button.Submit) }) {
                Box {
                  if (isProcessing) CircularProgressIndicator(modifier = Modifier.size(16.dp)) else {
                    Text(text = "Create Account", modifier = Modifier.padding(8.dp))
                  }
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
  WasteManagementTheme {
    CompanyCreateClientScreen(
      state = CompanyCreateClientState.Success(
        demographics = (1..4).map { companyLocationWithDemographic4Preview },
        paymentPlans = (1..4).map { paymentPlan4Preview },
        company = company4Preview
      ),
      channel = flow { },
      onEvent = {})
  }
}
