package net.techandgraphics.wastemanagement.ui.screen.company.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import net.techandgraphics.wastemanagement.R
import net.techandgraphics.wastemanagement.capitalize
import net.techandgraphics.wastemanagement.data.local.database.dashboard.account.Payment4CurrentMonth
import net.techandgraphics.wastemanagement.data.local.database.dashboard.payment.MonthYear
import net.techandgraphics.wastemanagement.data.local.database.dashboard.payment.MonthYearPayment4Month
import net.techandgraphics.wastemanagement.getTimeOfDay
import net.techandgraphics.wastemanagement.getToday
import net.techandgraphics.wastemanagement.share
import net.techandgraphics.wastemanagement.toAmount
import net.techandgraphics.wastemanagement.toFullName
import net.techandgraphics.wastemanagement.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastemanagement.ui.screen.account4Preview
import net.techandgraphics.wastemanagement.ui.screen.client.home.LetterView
import net.techandgraphics.wastemanagement.ui.screen.company4Preview
import net.techandgraphics.wastemanagement.ui.screen.companyContact4Preview
import net.techandgraphics.wastemanagement.ui.screen.payment4CurrentLocationMonth4Preview
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme
import java.time.Month

@OptIn(
  ExperimentalMaterial3Api::class,
  ExperimentalLayoutApi::class
) @Composable fun CompanyHomeScreen(
  state: CompanyHomeState,
  channel: Flow<CompanyHomeChannel>,
  onEvent: (CompanyHomeEvent) -> Unit,
) {

  var showMenuItems by remember { mutableStateOf(false) }
  val context = LocalContext.current

  when (state) {
    CompanyHomeState.Loading -> LoadingIndicatorView()
    is CompanyHomeState.Success -> {


      val lifecycleOwner = LocalLifecycleOwner.current
      LaunchedEffect(key1 = channel) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
          channel.collect { event ->
            when (event) {
              is CompanyHomeChannel.Export -> context.share(event.file)
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
              if (state.pending.isNotEmpty()) IconButton(onClick = { onEvent(CompanyHomeEvent.Goto.VerifyPayment) }) {
                BadgedBox(badge = {
                  Badge {
                    Text(
                      text = state.pending.size.toString(),
                      style = MaterialTheme.typography.labelSmall
                    )
                  }
                }) {
                  Icon(painterResource(R.drawable.ic_upload_ready), null)
                }
              }

              IconButton(onClick = { showMenuItems = true }) {
                Icon(Icons.Default.MoreVert, null)
                DropdownMenu(showMenuItems, onDismissRequest = { showMenuItems = false }) {

                  DropdownMenuItem(text = {
                    Text(text = "Payments")
                  }, onClick = {
                    showMenuItems = false
                    onEvent(CompanyHomeEvent.Goto.Payments)
                  })

                  DropdownMenuItem(text = {
                    Text(text = "Clients")
                  }, onClick = {
                    showMenuItems = false
                    onEvent(CompanyHomeEvent.Goto.Clients)
                  })

                  DropdownMenuItem(text = {
                    Text(text = "Company")
                  }, onClick = {
                    showMenuItems = false
                    onEvent(CompanyHomeEvent.Goto.Company)
                  })

                  HorizontalDivider()

                  DropdownMenuItem(text = {
                    Text(text = "Export")
                  }, onClick = {
                    showMenuItems = false
                    onEvent(CompanyHomeEvent.Button.Export)
                  })

                }
              }

            },
            colors = TopAppBarDefaults.topAppBarColors(
              containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
          )
        },
      ) {
        LazyColumn(
          contentPadding = it,
          modifier = Modifier.padding(vertical = 32.dp, horizontal = 4.dp)
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

          item { Spacer(modifier = Modifier.height(16.dp)) }

          item { CompanyHomeClientPaidView(state) }

          item { Spacer(modifier = Modifier.height(16.dp)) }

          item {
            LazyRow {
              items(state.allMonthsPayments) { item ->
                val theBorderColor =
                  if (item.monthYear == state.monthYear) MaterialTheme.colorScheme.primary else {
                    MaterialTheme.colorScheme.secondaryContainer
                  }
                OutlinedCard(
                  border = BorderStroke(1.dp, theBorderColor),
                  modifier = Modifier.padding(8.dp),
                  onClick = { onEvent(CompanyHomeEvent.Button.WorkingMonth(item.monthYear)) }) {
                  Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                  ) {

                    Text(text = Month.of(item.monthYear.month).name.capitalize())
                    Text(
                      text = "${item.payment4CurrentMonth.totalPaidAccounts}",
                      style = MaterialTheme.typography.titleLarge,
                      color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                      text = item.payment4CurrentMonth.totalPaidAmount.toAmount(),
                      style = MaterialTheme.typography.bodyLarge,
                      color = MaterialTheme.colorScheme.secondary
                    )
                  }
                }
              }
            }
          }

          item { Spacer(modifier = Modifier.height(16.dp)) }


          item {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "Payments per Location",
                modifier = Modifier.weight(1f)
              )
              TextButton(onClick = { onEvent(CompanyHomeEvent.Goto.PerLocation) }) {
                Text(text = "See all")
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null)
              }
            }
          }

          items(state.payment4CurrentLocationMonth) { CompanyHomeClientPaidStreetView(it, onEvent) }
        }
      }
    }
  }


}


@Preview(showBackground = true) @Composable private fun CompanyHomeScreenPreview() {
  WasteManagementTheme {
    CompanyHomeScreen(
      state = companyHomeStateSuccess(),
      channel = flowOf()
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
    }
  )
}
