package net.techandgraphics.wastical.ui.screen.company.client.location

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import net.techandgraphics.wastical.R
import net.techandgraphics.wastical.toLocation
import net.techandgraphics.wastical.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastical.ui.screen.SnackbarThemed
import net.techandgraphics.wastical.ui.screen.account4Preview
import net.techandgraphics.wastical.ui.screen.company.CompanyInfoTopAppBarView
import net.techandgraphics.wastical.ui.screen.company4Preview
import net.techandgraphics.wastical.ui.screen.companyLocation4Preview
import net.techandgraphics.wastical.ui.screen.companyLocationWithDemographic4Preview
import net.techandgraphics.wastical.ui.screen.demographicArea4Preview
import net.techandgraphics.wastical.ui.screen.demographicStreet4Preview
import net.techandgraphics.wastical.ui.theme.WasticalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyClientLocationScreen(
  state: CompanyClientLocationState,
  onEvent: (CompanyClientLocationEvent) -> Unit,
) {

  val snackbarHostState = remember { SnackbarHostState() }
  val scope = rememberCoroutineScope()

  when (state) {
    CompanyClientLocationState.Loading -> LoadingIndicatorView()
    is CompanyClientLocationState.Success -> {
      val currentId: Long = state.companyLocation.demographicStreetId
      val selectedIdState = remember { mutableStateOf(currentId) }
      val selectedAreaIdState = remember { mutableStateOf<Long?>(null) }
      Scaffold(
        snackbarHost = {
          SnackbarHost(hostState = snackbarHostState) { SnackbarThemed(it) }
        },
        topBar = {
          CompanyInfoTopAppBarView(state.company) {
            onEvent(CompanyClientLocationEvent.Goto.BackHandler)
          }
        },
        bottomBar = {
          Surface(tonalElevation = 0.dp) {
            Row(modifier = Modifier.padding(16.dp)) {
              Button(
                onClick = {
                  val target =
                    state.demographics.firstOrNull { it.demographicStreet.id == selectedIdState.value }?.demographicStreet
                  if (target != null) {
                    scope.launch {
                      snackbarHostState.showSnackbar(
                        message = "Please confirm the location change request for this client ?",
                        actionLabel = "Confirm",
                        duration = SnackbarDuration.Short
                      ).also { result ->
                        when (result) {
                          SnackbarResult.Dismissed -> Unit
                          SnackbarResult.ActionPerformed -> onEvent(
                            CompanyClientLocationEvent.Button.Change(
                              target
                            )
                          )
                        }
                      }
                    }
                  }
                },
                enabled = selectedIdState.value != currentId,
                modifier = Modifier.fillMaxWidth()
              ) { Text("Change Location") }
            }
          }
        }
      ) {
        LazyColumn(
          contentPadding = it,
          modifier = Modifier.padding(16.dp)
        ) {
          item {
            Text(
              text = "Change Location",
              style = MaterialTheme.typography.headlineSmall,
              modifier = Modifier.padding(bottom = 16.dp)
            )
          }

          // Current location hero card (reimagined)
          item {
            ElevatedCard(
              modifier = Modifier.padding(bottom = 16.dp),
              shape = MaterialTheme.shapes.large
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    painter = painterResource(R.drawable.ic_house),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                  )
                  Column(
                    modifier = Modifier
                      .padding(start = 12.dp)
                      .weight(1f)
                  ) {
                    Text(text = "Current Location", style = MaterialTheme.typography.labelLarge)
                    Text(
                      text = state.demographic.toLocation(),
                      style = MaterialTheme.typography.bodyMedium,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                  OutlinedCard(shape = CircleShape) {
                    Text(
                      text = "Active",
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.primary,
                      modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                  }
                }


                Box(
                  modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(
                      Brush.horizontalGradient(
                        listOf(
                          MaterialTheme.colorScheme.primary.copy(alpha = .5f),
                          MaterialTheme.colorScheme.primary
                        )
                      )
                    )
                )
              }
            }
          }

          // Search
          item {
            CompanyClientLocationSearchView(state) { onEvent(it) }
          }

          // Area chips
          item {
            val areas = state.demographics.map { it.demographicArea }.distinctBy { it.id }
            LazyRow(modifier = Modifier.padding(top = 8.dp)) {
              item {
                OutlinedCard(
                  shape = MaterialTheme.shapes.small,
                  modifier = Modifier
                    .padding(end = 8.dp)
                    .clickable { selectedAreaIdState.value = null }
                ) {
                  Text(
                    text = "All",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    color = if (selectedAreaIdState.value == null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                  )
                }
              }
              items(areas) { area ->
                OutlinedCard(
                  shape = MaterialTheme.shapes.small,
                  modifier = Modifier
                    .padding(end = 8.dp)
                    .clickable { selectedAreaIdState.value = area.id }
                ) {
                  Text(
                    text = area.name,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    color = if (selectedAreaIdState.value == area.id) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                  )
                }
              }
            }
          }

          // Selected summary
          item {
            if (selectedIdState.value != currentId) {
              ElevatedCard(
                modifier = Modifier.padding(top = 12.dp),
                shape = MaterialTheme.shapes.large
              ) {
                Row(modifier = Modifier.padding(16.dp)) {
                  Text(
                    text = "Selected:",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  val selected =
                    state.demographics.firstOrNull { it.demographicStreet.id == selectedIdState.value }
                  Text(
                    text = selected?.toLocation() ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                  )
                  Spacer(modifier = Modifier.weight(1f))
                  TextButton(onClick = { selectedIdState.value = currentId }) { Text("Clear") }
                }
              }
            }
          }


          item {
            // Compute filtered list using query and area chips
            Text(
              text = "Available Locations (" + state.demographics.filter { model ->
                val matchesQuery = state.query.isBlank() ||
                  model.demographicStreet.name.contains(state.query, ignoreCase = true) ||
                  model.demographicArea.name.contains(state.query, ignoreCase = true)
                val matchesArea = selectedAreaIdState.value?.let { model.demographicArea.id == it } ?: true
                matchesQuery && matchesArea
              }.size + ")",
              style = MaterialTheme.typography.titleMedium,
              modifier = Modifier.padding(vertical = 8.dp)
            )
          }
          item { Spacer(modifier = Modifier.height(8.dp)) }

          val filtered = state.demographics.filter { model ->
            val matchesQuery = state.query.isBlank() ||
              model.demographicStreet.name.contains(state.query, ignoreCase = true) ||
              model.demographicArea.name.contains(state.query, ignoreCase = true)
            val matchesArea = selectedAreaIdState.value?.let { model.demographicArea.id == it } ?: true
            matchesQuery && matchesArea
          }
          if (filtered.isEmpty()) {
            item {
              Text(
                text = "No locations match your filters",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 24.dp)
              )
            }
          }

          items(filtered) { model ->
            CompanyClientLocationItem(
              modifier = Modifier.animateItem(),
              location = state.companyLocation,
              model = model,
            ) { event ->
              if (event is CompanyClientLocationEvent.Button.Change) {
                selectedIdState.value = event.demographicStreet.id
              } else onEvent(event)
            }
          }
        }
      }
    }
  }
}


@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CompanyClientLocationScreenPreview() {
  WasticalTheme {
    CompanyClientLocationScreen(
      state = companyClientLocationStateSuccess(),
      onEvent = {}
    )
  }
}


fun companyClientLocationStateSuccess() = CompanyClientLocationState.Success(
  company = company4Preview,
  account = account4Preview,
  accountDemographicArea = demographicArea4Preview,
  accountDemographicStreet = demographicStreet4Preview,
  demographics = (1..5).map { companyLocationWithDemographic4Preview },
  demographic = companyLocationWithDemographic4Preview,
  companyLocation = companyLocation4Preview
)
