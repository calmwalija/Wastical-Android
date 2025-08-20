package net.techandgraphics.wastical.ui.screen.company.client.browse

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.techandgraphics.wastical.R
import net.techandgraphics.wastical.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastical.ui.screen.SearchInputItemView
import net.techandgraphics.wastical.ui.screen.SearchInputItemViewEvent
import net.techandgraphics.wastical.ui.screen.accountWithStreetAndArea4Preview
import net.techandgraphics.wastical.ui.screen.company.CompanyInfoTopAppBarView
import net.techandgraphics.wastical.ui.screen.company4Preview
import net.techandgraphics.wastical.ui.theme.Muted
import net.techandgraphics.wastical.ui.theme.WasticalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyBrowseClientScreen(
  state: CompanyBrowseClientState,
  channel: Flow<CompanyBrowseClientChannel>,
  onEvent: (CompanyBrowseClientListEvent) -> Unit,
) {

  val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
  LaunchedEffect(key1 = channel) {
    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
      channel.collect { event ->
        when (event) {
          else -> {
            TODO()
          }
        }
      }
    }
  }



  when (state) {
    CompanyBrowseClientState.Loading -> LoadingIndicatorView()
    is CompanyBrowseClientState.Success -> {

      var showFilters by remember { mutableStateOf(false) }


      if (showFilters) ModalBottomSheet(onDismissRequest = { showFilters = false }) {
        CompanyBrowseClientSearchFilterView(state, onEvent)
      }


      Scaffold(
        topBar = {
          CompanyInfoTopAppBarView(state.company) {
            onEvent(CompanyBrowseClientListEvent.Goto.BackHandler)
          }
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
                  text = "Browse Clients",
                  style = MaterialTheme.typography.headlineMedium,
                )
                Text(
                  text = "Which client are you looking for ?",
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
            Box(modifier = Modifier.padding(horizontal = 8.dp)) {
              SearchInputItemView(
                query = state.query,
                trailingView = {
                  Row {
                    IconButton(
                      onClick = { showFilters = true },
                      colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(.2f)
                      ),
                    ) {
                      Icon(
                        painter = painterResource(id = R.drawable.ic_sort),
                        contentDescription = null,
                      )
                    }
                  }

                },
                onEvent = { event ->
                  when (event) {
                    is SearchInputItemViewEvent.InputSearch -> {
                      onEvent(CompanyBrowseClientListEvent.Input.Search(event.query))
                    }
                  }
                }
              )
            }
          }

          item {
            if (state.searchHistoryTags.size > 3)
              CompanyBrowseClientSearchHistoryView(state, onEvent)
          }

          items(state.accounts, key = { key -> key.accountId }) { account ->
            CompanyBrowseClientView(account, modifier = Modifier.animateItem(), onEvent)
          }

        }
      }
    }
  }
}


@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CompanyBrowseClientScreenPreview() {
  WasticalTheme {
    CompanyBrowseClientScreen(
      state = CompanyBrowseClientState.Success(
        accounts = (1..6)
          .map { listOf(accountWithStreetAndArea4Preview) }
          .toList()
          .flatten(),
        company = company4Preview
      ),
      channel = flow { },
      onEvent = {}
    )
  }
}
