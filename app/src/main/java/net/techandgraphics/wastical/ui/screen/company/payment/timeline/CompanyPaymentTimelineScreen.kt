package net.techandgraphics.wastical.ui.screen.company.payment.timeline

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import net.techandgraphics.wastical.R
import net.techandgraphics.wastical.defaultDate
import net.techandgraphics.wastical.toZonedDateTime
import net.techandgraphics.wastical.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastical.ui.screen.SearchInputItemView
import net.techandgraphics.wastical.ui.screen.SearchInputItemViewEvent
import net.techandgraphics.wastical.ui.screen.SnackbarThemed
import net.techandgraphics.wastical.ui.screen.company.CompanyInfoTopAppBarView
import net.techandgraphics.wastical.ui.screen.company4Preview
import net.techandgraphics.wastical.ui.screen.paymentWithAccountAndMethodWithGateway4Preview
import net.techandgraphics.wastical.ui.theme.WasticalTheme

@OptIn(ExperimentalMaterial3Api::class) @Composable fun CompanyPaymentTimelineScreen(
  state: CompanyPaymentTimelineState,
  onEvent: (CompanyPaymentTimelineEvent) -> Unit,
) {
  when (state) {
    CompanyPaymentTimelineState.Loading -> LoadingIndicatorView()
    is CompanyPaymentTimelineState.Success -> {

      val snackbarHostState = remember { SnackbarHostState() }

      Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) { SnackbarThemed(it) } },
        topBar = {
          CompanyInfoTopAppBarView(state.company) { onEvent(CompanyPaymentTimelineEvent.Button.BackHandler) }
        },
      ) { innerPadding ->
        val listState = rememberLazyListState()
        val scope = rememberCoroutineScope()
        val showScrollToTop by remember { derivedStateOf { listState.firstVisibleItemIndex >= 10 } }

        val pagingItems = state.payments.collectAsLazyPagingItems()
        Box(modifier = Modifier.padding(innerPadding)) {
          LazyColumn(
            state = listState,
            contentPadding = PaddingValues(bottom = 80.dp),
            modifier = Modifier.padding(16.dp)
          ) {

            item {
              Text(
                text = "Payments Timeline",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 32.dp)
              )
            }

            item {
              SearchInputItemView(
                query = state.query,
                placeholder = "Input account, gateway",
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
                            onEvent(CompanyPaymentTimelineEvent.Button.Sort(true))
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
                            onEvent(CompanyPaymentTimelineEvent.Button.Sort(false))
                          })
                      }
                    }
                  }

                },
                onEvent = { event ->
                  when (event) {
                    is SearchInputItemViewEvent.InputSearch -> {
                      onEvent(CompanyPaymentTimelineEvent.Input.Query(event.query))
                    }
                  }
                }
              )
            }

            item { Spacer(Modifier.height(16.dp)) }

            items(
              count = pagingItems.itemCount,
              key = { index -> pagingItems.peek(index)?.payment?.id ?: index }
            ) { index ->

              val model = pagingItems[index] ?: return@items
              val label = model.payment.createdAt.toZonedDateTime().defaultDate()

              if (index == 0 || pagingItems[index - 1]?.payment?.createdAt?.toZonedDateTime()
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
              CompanyPaymentTimelineItem(
                modifier = Modifier.animateItem(),
                item = model,
                onEvent = onEvent
              )
            }



            when (pagingItems.loadState.append) {
              is LoadState.Error ->
                item {
                  Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = null
                  )
                }

              is LoadState.Loading -> item { CircularProgressIndicator() }

              else -> Unit
            }
          }

          AnimatedVisibility(
            visible = showScrollToTop,
            modifier = Modifier
              .align(Alignment.BottomEnd)
              .padding(16.dp)
          ) {
            FloatingActionButton(
              onClick = { scope.launch { listState.animateScrollToItem(0) } },
              containerColor = MaterialTheme.colorScheme.primary
            ) {
              Icon(
                imageVector = Icons.Rounded.KeyboardArrowUp,
                contentDescription = "Scroll to top"
              )
            }
          }
        }
      }
    }
  }
}

@Preview @Composable fun CompanyPaymentTimelineScreenPreview() {
  WasticalTheme {
    CompanyPaymentTimelineScreen(
      state = companyPaymentTimelineState(), onEvent = {})
  }
}


fun companyPaymentTimelineState() = CompanyPaymentTimelineState.Success(
  company = company4Preview,
  payments = theData,
)

val theData = flow {
  val values = listOf(paymentWithAccountAndMethodWithGateway4Preview)
  emit(PagingData.from(values))
}
