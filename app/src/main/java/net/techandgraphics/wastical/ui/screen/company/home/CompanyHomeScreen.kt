package net.techandgraphics.wastical.ui.screen.company.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import net.techandgraphics.wastical.R
import net.techandgraphics.wastical.data.local.database.dashboard.account.Payment4CurrentMonth
import net.techandgraphics.wastical.data.local.database.dashboard.payment.MonthYear
import net.techandgraphics.wastical.data.local.database.dashboard.payment.MonthYearPayment4Month
import net.techandgraphics.wastical.getTimeOfDay
import net.techandgraphics.wastical.getToday
import net.techandgraphics.wastical.share
import net.techandgraphics.wastical.toFullName
import net.techandgraphics.wastical.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastical.ui.screen.account4Preview
import net.techandgraphics.wastical.ui.screen.client.home.LetterView
import net.techandgraphics.wastical.ui.screen.company4Preview
import net.techandgraphics.wastical.ui.screen.companyContact4Preview
import net.techandgraphics.wastical.ui.screen.payment4CurrentLocationMonth4Preview
import net.techandgraphics.wastical.ui.screen.paymentWithAccountAndMethodWithGateway4Preview
import net.techandgraphics.wastical.ui.theme.Brown
import net.techandgraphics.wastical.ui.theme.Green
import net.techandgraphics.wastical.ui.theme.Purple
import net.techandgraphics.wastical.ui.theme.WasticalTheme


private val quickOption = listOf(

  CompanyHomeItemModel(
    title = "Locations",
    drawableRes = R.drawable.ic_house,
    containerColor = Purple,
    event = CompanyHomeEvent.Goto.PerLocation
  ),

  CompanyHomeItemModel(
    title = "Clients",
    drawableRes = R.drawable.ic_account,
    containerColor = Brown,
    event = CompanyHomeEvent.Goto.Clients
  ),

  CompanyHomeItemModel(
    title = "Reports",
    drawableRes = R.drawable.ic_bar_chart,
    containerColor = Green,
    event = CompanyHomeEvent.Goto.Report
  )
)


@OptIn(
  ExperimentalMaterial3Api::class,
  ExperimentalLayoutApi::class
) @Composable fun CompanyHomeScreen(
  state: CompanyHomeState,
  channel: Flow<CompanyHomeChannel>,
  templates: Flow<List<Pair<String, String>>>,
  onEvent: (CompanyHomeEvent) -> Unit,
) {

  var showMenuItems by remember { mutableStateOf(false) }
  var isFetching by remember { mutableStateOf(false) }
  val context = LocalContext.current
  var showBroadcast by remember { mutableStateOf(false) }
  var customTitle by remember { mutableStateOf("") }
  var customBody by remember { mutableStateOf("") }

  when (state) {
    CompanyHomeState.Loading -> LoadingIndicatorView()
    is CompanyHomeState.Success -> {


      val lifecycleOwner = LocalLifecycleOwner.current
      LaunchedEffect(key1 = channel) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
          channel.collect { event ->
            when (event) {
              is CompanyHomeChannel.Export -> context.share(event.file)
              is CompanyHomeChannel.Fetch -> isFetching = when (event) {
                is CompanyHomeChannel.Fetch.Error -> false
                CompanyHomeChannel.Fetch.Fetching -> true
                CompanyHomeChannel.Fetch.Success -> false
              }

              CompanyHomeChannel.Goto.Login -> onEvent(CompanyHomeEvent.Goto.Login)
              CompanyHomeChannel.Goto.Reload -> onEvent(CompanyHomeEvent.Goto.Reload)

              else -> Unit
            }
          }
        }
      }

      Scaffold(
        topBar = {
          TopAppBar(
            title = {
              Column(
                modifier = Modifier
                  .padding(horizontal = 16.dp)
                  .fillMaxWidth()
              ) {
                Text(
                  text = state.company.name,
                  fontWeight = FontWeight.Bold,
                  maxLines = 1,
                  style = MaterialTheme.typography.bodyLarge,
                  overflow = TextOverflow.Ellipsis,
                )
              }
            },
            actions = {
              if (state.accountRequests.isEmpty() && state.paymentRequests.isEmpty()) {
                IconButton(
                  enabled = isFetching.not(),
                  onClick = { onEvent(CompanyHomeEvent.Fetch) }) {
                  if (isFetching) CircularProgressIndicator(modifier = Modifier.size(24.dp)) else {
                    Icon(
                      painter = painterResource(R.drawable.ic_cloud_download),
                      contentDescription = null
                    )
                  }
                }
              } else {
                IconButton(
                  onClick = { onEvent(CompanyHomeEvent.Button.Workers) }) {
                  Icon(
                    painter = painterResource(R.drawable.ic_cloud_sync),
                    contentDescription = null
                  )
                }
              }


              IconButton(onClick = { showMenuItems = true }) {
                Icon(Icons.Default.MoreVert, null)
                DropdownMenu(showMenuItems, onDismissRequest = { showMenuItems = false }) {

                  if (state.proofOfPayments.isNotEmpty()) {
                    DropdownMenuItem(
                      leadingIcon = {
                        BadgedBox(badge = {
                          Badge(containerColor = MaterialTheme.colorScheme.primary) {
                            Text(
                              text = state.proofOfPayments.size.toString(),
                              style = MaterialTheme.typography.labelSmall
                            )
                          }
                        }) {
                          Icon(painterResource(R.drawable.ic_list), null)
                        }
                      },
                      text = {
                        Text(text = "Verify Payments")
                      }, onClick = {
                        showMenuItems = false
                        onEvent(CompanyHomeEvent.Goto.Payments)
                      })
                  }


                  DropdownMenuItem(
                    leadingIcon = { Icon(Icons.Outlined.Notifications, null) },
                    text = {
                      Text(text = "Notifications")
                    }, onClick = {
                      showMenuItems = false
                      onEvent(CompanyHomeEvent.Goto.Notifications)
                    })

                  DropdownMenuItem(
                    leadingIcon = {
                      Icon(
                        painter = painterResource(R.drawable.ic_megaphone),
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                      )
                    },
                    text = {
                      Text(text = "Broadcast")
                    }, onClick = {
                      showMenuItems = false
                      showBroadcast = true
                    })


                  DropdownMenuItem(
                    leadingIcon = {
                      Icon(
                        painter = painterResource(R.drawable.ic_pie_chart),
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                      )
                    },
                    text = {
                      Text(text = "Expenses")
                    }, onClick = {
                      showMenuItems = false
                      onEvent(CompanyHomeEvent.Goto.Expenses)
                    })

                  DropdownMenuItem(
                    leadingIcon = { Icon(Icons.Outlined.Info, null) },
                    text = {
                      Text(text = "Company")
                    }, onClick = {
                      showMenuItems = false
                      onEvent(CompanyHomeEvent.Goto.Company)
                    })

                  HorizontalDivider()

                  DropdownMenuItem(text = {
                    Text(text = "Logout")
                  }, onClick = {
                    showMenuItems = false
                    onEvent(CompanyHomeEvent.Button.Logout)
                  })

                }
              }

            },
          )
        },
      ) {
        LazyColumn(
          contentPadding = it,
          modifier = Modifier.padding(16.dp)
        ) {
          item {
            Row(verticalAlignment = Alignment.CenterVertically) {
              LetterView(state.account)
              Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                Text(
                  text = "Good ${getTimeOfDay()}",
                  style = MaterialTheme.typography.bodySmall,
                )
                Text(
                  text = state.account.toFullName(),
                  style = MaterialTheme.typography.bodyMedium,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.secondary
                )
              }
            }
          }


          item { Spacer(modifier = Modifier.height(24.dp)) }


          item {
            FlowRow(
              maxItemsInEachRow = 3,
              modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              (quickOption).forEach { model ->
                Card(
                  onClick = { onEvent(model.event) },
                  modifier = Modifier.fillMaxWidth(.3f),
                  colors = CardDefaults.elevatedCardColors()
                ) {
                  Column(
                    modifier = Modifier
                      .fillMaxSize()
                      .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                  ) {
                    Icon(
                      painterResource(model.drawableRes),
                      contentDescription = null,
                      modifier = Modifier.size(32.dp),
                      tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                      text = model.title,
                      style = MaterialTheme.typography.bodySmall
                    )
                  }
                }
              }
            }
          }

          item { Spacer(modifier = Modifier.height(24.dp)) }

          item {
            var chartStyle by remember { mutableStateOf(ChartStyle.Bar) }
            CompanyHomeMonthlyPaymentChart(
              data = state.allMonthsPayments.reversed(),
              showYAxis = true,
              showBarLabels = false,
              style = chartStyle,
              onStyleChange = { chartStyle = it }
            )
          }

          item { Spacer(modifier = Modifier.height(24.dp)) }

          item { CompanyHomePaymentMonthlyView(state, onEvent) }

          item { Spacer(modifier = Modifier.height(24.dp)) }

          item {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(start = 16.dp)
            ) {
              Text(
                text = "Payments timeline",
                modifier = Modifier.weight(1f)
              )
              TextButton(onClick = { onEvent(CompanyHomeEvent.Goto.Timeline) }) {
                Text(text = "See all")
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null)
              }
            }
          }

          items(state.timeline) { CompanyHomePaymentTimelineItem(it, onEvent) }

        }
      }

      if (showBroadcast) {
        ModalBottomSheet(onDismissRequest = { showBroadcast = false }) {
          Text(
            text = "Select a template or create custom",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
          )
          val templatePairs = templates.collectAsState(initial = emptyList()).value
          val theTemplates = if (templatePairs.isEmpty()) listOf(
            "Service Interruption" to "Dear customer, there will be a temporary service interruption today.",
            "Payment Reminder" to "Kind reminder to complete your monthly payment."
          ) else templatePairs
          theTemplates.forEach { (t, b) ->
            Card(
              onClick = {
                onEvent(CompanyHomeEvent.Broadcast.Send(t, b))
                showBroadcast = false
              },
              modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .fillMaxWidth(),
              colors = CardDefaults.elevatedCardColors()
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Text(text = t, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(text = b, maxLines = 2, overflow = TextOverflow.Ellipsis)
              }
            }
          }
          Spacer(Modifier.height(16.dp))
          Text(
            text = "Custom message",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
          )
          OutlinedTextField(
            value = customTitle,
            onValueChange = { customTitle = it },
            label = { Text("Title") },
            modifier = Modifier
              .padding(horizontal = 16.dp, vertical = 8.dp)
              .fillMaxWidth()
          )
          OutlinedTextField(
            value = customBody,
            onValueChange = { customBody = it },
            label = { Text("Body") },
            modifier = Modifier
              .padding(horizontal = 16.dp)
              .fillMaxWidth()
          )
          Row(
            modifier = Modifier
              .padding(16.dp)
              .fillMaxWidth(),
            horizontalArrangement = Arrangement.End
          ) {
            Button(
              enabled = customTitle.isNotBlank() && customBody.isNotBlank(),
              onClick = {
                onEvent(CompanyHomeEvent.Broadcast.Send(customTitle, customBody))
                showBroadcast = false
                customTitle = ""
                customBody = ""
              }
            ) { Text("Send") }
          }
        }
      }
    }
  }


}


@Preview(showBackground = true) @Composable private fun CompanyHomeScreenPreview() {
  WasticalTheme {
    CompanyHomeScreen(
      state = companyHomeStateSuccess(),
      channel = flowOf(),
      templates = flowOf(listOf("Payment Reminder" to "Please pay."))
    ) {}
  }
}

fun companyHomeStateSuccess(): CompanyHomeState.Success {
  val (_, month, year) = getToday()
  return CompanyHomeState.Success(
    payment4CurrentLocationMonth = listOf(payment4CurrentLocationMonth4Preview),
    account = account4Preview,
    company = company4Preview,
    companyContact = companyContact4Preview,
    accountsSize = 200,
    payment4CurrentMonth = Payment4CurrentMonth(120, 935_000),
    expectedAmountToCollect = 2444_000,
    paymentPlanAgainstAccounts = listOf(),
    monthYear = MonthYear(month, year),
    allMonthsPayments = (1..month.plus(1)).map {
      MonthYearPayment4Month(
        MonthYear(it, year),
        payment4CurrentMonth = Payment4CurrentMonth(120, 935_000),
      )
    },
    proofOfPayments = (1..3).map { paymentWithAccountAndMethodWithGateway4Preview },
    timeline = (1..3).map { paymentWithAccountAndMethodWithGateway4Preview },
  )
}
