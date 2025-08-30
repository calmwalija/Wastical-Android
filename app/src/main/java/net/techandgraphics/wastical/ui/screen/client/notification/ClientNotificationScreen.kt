package net.techandgraphics.wastical.ui.screen.client.notification

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import net.techandgraphics.wastical.R
import net.techandgraphics.wastical.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastical.ui.screen.ScrollToTopView
import net.techandgraphics.wastical.ui.screen.SearchInputItemView
import net.techandgraphics.wastical.ui.screen.SearchInputItemViewEvent
import net.techandgraphics.wastical.ui.screen.VerticalScrollbar
import net.techandgraphics.wastical.ui.screen.company.CompanyInfoTopAppBarView
import net.techandgraphics.wastical.ui.screen.company4Preview
import net.techandgraphics.wastical.ui.screen.notification4Preview
import net.techandgraphics.wastical.ui.theme.WasticalTheme

@Composable
fun ClientNotificationScreen(
  state: ClientNotificationState,
  onEvent: (ClientNotificationEvent) -> Unit,
) {

  val listState = rememberLazyListState()
  val coroutineScope = rememberCoroutineScope()
  val showScrollToTop by remember { derivedStateOf { listState.firstVisibleItemIndex >= 10 } }


  when (state) {
    ClientNotificationState.Loading -> LoadingIndicatorView()
    is ClientNotificationState.Success -> Scaffold(
      topBar = {
        CompanyInfoTopAppBarView(state.company) {
          onEvent(ClientNotificationEvent.Button.BackHandler)
        }
      },
    ) {
      Box {
        LazyColumn(
          state = listState,
          contentPadding = it,
          modifier = Modifier.padding(16.dp)
        ) {

          item {
            Text(
              text = "Notifications",
              style = MaterialTheme.typography.headlineSmall,
              modifier = Modifier.padding(bottom = 24.dp)
            )
          }

          item {
            SearchInputItemView(
              query = state.query,
              placeholder = "Input keyword to search",
              trailingView = {
                var showSortBy by remember { mutableStateOf(false) }

                Row {
                  IconButton(
                    onClick = { showSortBy = true },
                    colors = IconButtonDefaults.iconButtonColors(
                      containerColor = MaterialTheme.colorScheme.primary.copy(.2f)
                    ),
                  ) {
                    Icon(
                      painter = painterResource(id = R.drawable.ic_sort),
                      contentDescription = null,
                    )
                    DropdownMenu(
                      expanded = showSortBy,
                      onDismissRequest = { showSortBy = false }) {
                      DropdownMenuItem(
                        text = { Text("Newest") },
                        enabled = !state.sort,
                        trailingIcon = {
                          if (state.sort)
                            Icon(
                              imageVector = Icons.Rounded.CheckCircle,
                              contentDescription = null,
                              tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        onClick = {
                          showSortBy = false
                          onEvent(ClientNotificationEvent.Button.Sort(true))
                        })

                      DropdownMenuItem(
                        text = { Text("Oldest") },
                        trailingIcon = {
                          if (!state.sort)
                            Icon(
                              imageVector = Icons.Rounded.CheckCircle,
                              contentDescription = null,
                              tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        enabled = state.sort,
                        onClick = {
                          showSortBy = false
                          onEvent(ClientNotificationEvent.Button.Sort(false))
                        })
                    }
                  }
                }

              },
              onEvent = { event ->
                when (event) {
                  is SearchInputItemViewEvent.InputSearch -> {
                    onEvent(ClientNotificationEvent.Input.Query(event.query))
                  }
                }
              }
            )
          }

          item { Spacer(modifier = Modifier.height(16.dp)) }

          items(state.notifications, key = { key -> key.id }) { notification ->
            ClientNotificationItem(
              modifier = Modifier.animateItem(),
              notification = notification
            )
          }

        }

        VerticalScrollbar(
          listState = listState,
          modifier = Modifier
            .zIndex(1f)
            .align(Alignment.CenterEnd)
        )

        ScrollToTopView(
          listState = listState,
          coroutineScope = coroutineScope,
          showScrollToTop = showScrollToTop,
          modifier = Modifier
            .zIndex(1f)
            .align(Alignment.BottomEnd)
        )

      }
    }
  }
}


@Preview
@Composable
private fun ClientNotificationScreenPreview() {
  WasticalTheme {
    ClientNotificationScreen(
      state = notificationStateSuccess(),
      onEvent = {}
    )
  }
}

fun notificationStateSuccess() = ClientNotificationState.Success(
  company = company4Preview,
  notifications = listOf(notification4Preview)
)
