package net.techandgraphics.wastical.ui.screen.company.client.location

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import net.techandgraphics.wastical.R
import net.techandgraphics.wastical.toLocation
import net.techandgraphics.wastical.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastical.ui.screen.SearchInputItemView
import net.techandgraphics.wastical.ui.screen.SearchInputItemViewEvent
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

          item {
            ElevatedCard(
              modifier = Modifier.padding(bottom = 16.dp),
              shape = MaterialTheme.shapes.large
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    painter = painterResource(R.drawable.ic_check_circle),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                  )
                  Column(
                    modifier = Modifier
                      .padding(horizontal = 16.dp)
                      .weight(1f)
                  ) {
                    Text(
                      text = "Current Location",
                      style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                      text = state.demographic.toLocation(),
                      style = MaterialTheme.typography.bodyMedium,
                      color = MaterialTheme.colorScheme.onSurfaceVariant,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis
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
              }
            }
          }

          item {
            SearchInputItemView(state.query, trailingView = {
              IconButton(
                onClick = { },
                enabled = false,
                modifier = Modifier.alpha(0f)
              ) {
                Icon(
                  painter = painterResource(id = R.drawable.ic_sort),
                  contentDescription = null,
                )
              }
            }) { event ->
              when (event) {
                is SearchInputItemViewEvent.InputSearch ->
                  onEvent(CompanyClientLocationEvent.Input.Search(event.query))
              }
            }
          }

          item { Spacer(modifier = Modifier.height(8.dp)) }

          item {
            val areas =
              state.demographics.map { item -> item.demographicArea }.distinctBy { item -> item.id }
            LazyRow(modifier = Modifier.padding(top = 8.dp)) {
              item {
                OutlinedCard(
                  shape = CircleShape,
                  modifier = Modifier.padding(end = 8.dp),
                  enabled = selectedAreaIdState.value != null,
                  onClick = { selectedAreaIdState.value = null }
                ) {
                  Text(
                    text = "All",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = if (selectedAreaIdState.value == null) MaterialTheme.colorScheme.primary else {
                      MaterialTheme.colorScheme.onSurface
                    },
                    style = MaterialTheme.typography.labelLarge
                  )
                }
              }
              items(areas) { area ->
                OutlinedCard(
                  shape = CircleShape,
                  modifier = Modifier.padding(end = 8.dp),
                  enabled = selectedAreaIdState.value != area.id,
                  onClick = { selectedAreaIdState.value = area.id }
                ) {
                  Text(
                    text = area.name,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = if (selectedAreaIdState.value == area.id) MaterialTheme.colorScheme.primary else {
                      MaterialTheme.colorScheme.onSurface
                    },
                    style = MaterialTheme.typography.labelLarge
                  )
                }
              }
            }
          }

          item { Spacer(modifier = Modifier.height(16.dp)) }

          item {
            if (selectedIdState.value != currentId) {
              Card(
                modifier = Modifier.padding(top = 12.dp),
                colors = CardDefaults.cardColors(
                  containerColor = MaterialTheme.colorScheme.primary.copy(.1f)
                )
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "Selected:",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary
                  )
                  Spacer(modifier = Modifier.width(8.dp))

                  val selected =
                    state.demographics
                      .firstOrNull { item -> item.demographicStreet.id == selectedIdState.value }
                  Text(
                    text = selected?.toLocation() ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                  )
                  Spacer(modifier = Modifier.weight(1f))
                  TextButton(onClick = { selectedIdState.value = currentId }) { Text("Clear") }
                }
              }
            }
          }

          item { Spacer(modifier = Modifier.height(16.dp)) }


          item {
            Text(
              text = "Available Locations (" + state.demographics.filter { model ->
                val matchesQuery = state.query.isBlank() ||
                  model.demographicStreet.name.contains(state.query, ignoreCase = true) ||
                  model.demographicArea.name.contains(state.query, ignoreCase = true)
                val matchesArea =
                  selectedAreaIdState.value?.let { model.demographicArea.id == it } ?: true
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
            val matchesArea =
              selectedAreaIdState.value?.let { model.demographicArea.id == it } ?: true
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

          items(filtered, key = { item -> item.demographicStreet.id }) { model ->
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
