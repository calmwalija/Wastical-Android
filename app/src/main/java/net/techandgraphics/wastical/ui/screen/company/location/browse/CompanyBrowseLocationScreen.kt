package net.techandgraphics.wastical.ui.screen.company.location.browse

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.R
import net.techandgraphics.wastical.data.local.database.dashboard.payment.MonthYear
import net.techandgraphics.wastical.getToday
import net.techandgraphics.wastical.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastical.ui.screen.SearchInputItemView
import net.techandgraphics.wastical.ui.screen.SearchInputItemViewEvent
import net.techandgraphics.wastical.ui.screen.company.CompanyInfoTopAppBarView
import net.techandgraphics.wastical.ui.screen.company4Preview
import net.techandgraphics.wastical.ui.screen.payment4CurrentLocationMonth4Preview
import net.techandgraphics.wastical.ui.theme.Muted
import net.techandgraphics.wastical.ui.theme.WasticalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyBrowseLocationScreen(
  state: CompanyBrowseLocationState,
  onEvent: (CompanyBrowseLocationEvent) -> Unit,
) {

  val showSort = remember { mutableStateOf(false) }

  when (state) {
    CompanyBrowseLocationState.Loading -> LoadingIndicatorView()
    is CompanyBrowseLocationState.Success ->
      Scaffold(
        topBar = {
          CompanyInfoTopAppBarView(
            company = state.company,
            onBackHandler = { onEvent(CompanyBrowseLocationEvent.Button.BackHandler) }
          )
        },
      ) {
        LazyColumn(
          contentPadding = it,
          modifier = Modifier.padding(vertical = 16.dp)
        ) {
          item {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Column(
                modifier = Modifier
                  .padding(horizontal = 16.dp)
                  .weight(1f)
              ) {
                Text(
                  text = "Browse Location",
                  style = MaterialTheme.typography.headlineMedium,
                )
                Text(
                  text = "Which location are you looking for ?",
                  style = MaterialTheme.typography.bodyMedium,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                  color = Muted
                )
              }
            }
          }

          item { Spacer(modifier = Modifier.height(16.dp)) }

          item {
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
              SearchInputItemView(
                query = state.query,
                trailingView = {
                  Row {
                    IconButton(
                      onClick = { showSort.value = true },
                      colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(.2f)
                      ),
                    ) {
                      Icon(
                        painter = painterResource(id = R.drawable.ic_sort),
                        contentDescription = null,
                      )
                      DropdownMenu(
                        expanded = showSort.value,
                        onDismissRequest = { showSort.value = false }) {
                        LocationSortOrder.entries.forEach { sortBy ->
                          DropdownMenuItem(
                            text = {
                              Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                  text = sortBy.description,
                                  modifier = Modifier.padding(end = 16.dp),
                                  color = if (state.sortBy == sortBy) MaterialTheme.colorScheme.primary else {
                                    MaterialTheme.colorScheme.secondary
                                  }
                                )
                                if (state.sortBy == sortBy) {
                                  Icon(
                                    imageVector = Icons.Rounded.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                  )
                                }
                              }
                            },
                            enabled = state.sortBy != sortBy,
                            onClick = {
                              onEvent(CompanyBrowseLocationEvent.SortBy(sortBy))
                              showSort.value = false
                            },
                          )
                        }
                      }
                    }
                  }

                },
                onEvent = { event ->
                  when (event) {
                    is SearchInputItemViewEvent.InputSearch -> {
                      onEvent(CompanyBrowseLocationEvent.Input.Search(event.query))
                    }
                  }
                }
              )
            }
          }

          item { Spacer(modifier = Modifier.height(8.dp)) }

          items(state.payment4CurrentLocationMonth, key = { key -> key.streetId }) { location ->
            CompanyBrowseLocationView(
              modifier = Modifier.animateItem(),
              location = location,
              onEvent = onEvent
            )
          }

        }
      }
  }

}


@Preview
@Composable
private fun CompanyBrowseLocationScreenPreview() {
  WasticalTheme {
    CompanyBrowseLocationScreen(
      state = companyBrowseLocationStateSuccess(),
      onEvent = {}
    )
  }
}

fun companyBrowseLocationStateSuccess() = CompanyBrowseLocationState.Success(
  payment4CurrentLocationMonth = listOf(payment4CurrentLocationMonth4Preview),
  company = company4Preview,
  monthYear = MonthYear(getToday().month, getToday().year)
)
