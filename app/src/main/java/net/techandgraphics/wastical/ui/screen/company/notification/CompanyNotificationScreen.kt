package net.techandgraphics.wastical.ui.screen.company.notification

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.flow
import net.techandgraphics.wastical.R
import net.techandgraphics.wastical.defaultDate
import net.techandgraphics.wastical.toZonedDateTime
import net.techandgraphics.wastical.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastical.ui.screen.SearchInputItemView
import net.techandgraphics.wastical.ui.screen.SearchInputItemViewEvent
import net.techandgraphics.wastical.ui.screen.company.CompanyInfoTopAppBarView
import net.techandgraphics.wastical.ui.screen.company4Preview
import net.techandgraphics.wastical.ui.screen.notification4Preview
import net.techandgraphics.wastical.ui.theme.WasticalTheme

@Composable
fun CompanyNotificationScreen(
  state: CompanyNotificationState,
  onEvent: (CompanyNotificationEvent) -> Unit,
) {
  when (state) {
    CompanyNotificationState.Loading -> LoadingIndicatorView()
    is CompanyNotificationState.Success -> Scaffold(
      topBar = {
        CompanyInfoTopAppBarView(state.company) {
          onEvent(CompanyNotificationEvent.Button.BackHandler)
        }
      },
    ) {


      val pagingItems = state.notifications.collectAsLazyPagingItems()

      LazyColumn(
        contentPadding = it,
        modifier = Modifier.padding(16.dp)
      ) {


        item {
          Text(
            text = "Notifications",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 32.dp)
          )
        }

        item {
          SearchInputItemView(
            query = state.query,
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
                      onClick = { onEvent(CompanyNotificationEvent.Button.Sort(true)) })

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
                      onClick = { onEvent(CompanyNotificationEvent.Button.Sort(false)) })
                  }
                }
              }

            },
            onEvent = { event ->
              when (event) {
                is SearchInputItemViewEvent.InputSearch -> {
                  onEvent(CompanyNotificationEvent.Input.Query(event.query))
                }
              }
            }
          )
        }

        items(
          count = pagingItems.itemCount,
          key = { index -> pagingItems.peek(index)?.id ?: index }
        ) { index ->

          val notification = pagingItems[index] ?: return@items
          val label = notification.createdAt.toZonedDateTime().defaultDate()

          if (index == 0 || pagingItems[index - 1]?.createdAt?.toZonedDateTime()
              ?.defaultDate() != label
          ) {
            Text(
              text = label,
              modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
              fontWeight = FontWeight.Bold,
              style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))
          }
          CompanyNotificationItem(
            modifier = Modifier.animateItem(),
            notification = notification
          )
        }

      }
    }
  }
}

@Preview
@Composable
private fun CompanyNotificationScreenPreview() {
  WasticalTheme {
    CompanyNotificationScreen(
      state = CompanyNotificationState.Success(
        company = company4Preview,
        notifications = theData
      ),
      onEvent = {}
    )
  }
}


private val theData = flow {
  val items = (1L..3L).map { notification4Preview.copy(id = it) }
  emit(PagingData.from(items))
}
