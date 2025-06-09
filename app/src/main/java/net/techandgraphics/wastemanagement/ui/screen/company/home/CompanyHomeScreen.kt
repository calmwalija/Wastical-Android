package net.techandgraphics.wastemanagement.ui.screen.company.home

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastemanagement.data.local.database.dashboard.account.PaidThisMonthIndicator
import net.techandgraphics.wastemanagement.getTimeOfDay
import net.techandgraphics.wastemanagement.toFullName
import net.techandgraphics.wastemanagement.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastemanagement.ui.screen.account4Preview
import net.techandgraphics.wastemanagement.ui.screen.appState
import net.techandgraphics.wastemanagement.ui.screen.client.home.LetterView
import net.techandgraphics.wastemanagement.ui.screen.company4Preview
import net.techandgraphics.wastemanagement.ui.screen.paymentAccount4Preview
import net.techandgraphics.wastemanagement.ui.screen.streetPaidThisMonthIndicator4Preview
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CompanyHomeScreen(
  state: CompanyHomeState,
  onEvent: (CompanyHomeEvent) -> Unit,
) {


  val account = account4Preview
  val company = company4Preview


  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = company.name,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
              .padding(horizontal = 16.dp)
              .fillMaxWidth()
          )

        },
        actions = {
          IconButton(onClick = { }) {
            BadgedBox(badge = { Badge() }) {
              Icon(Icons.Outlined.Notifications, null)
            }
          }

          IconButton(onClick = { }) {
            Icon(Icons.Default.MoreVert, null)
            DropdownMenu(false, onDismissRequest = { }) {

              DropdownMenuItem(text = {
                Text(text = "Helpline")
              }, onClick = {})

              DropdownMenuItem(text = {
                Text(text = "Sign Out")
              }, onClick = {})


              HorizontalDivider()
              DropdownMenuItem(text = {
                Text(text = "Quit")
              }, onClick = {})
            }
          }

        },
        modifier = Modifier.shadow(0.dp),
        colors = TopAppBarDefaults.topAppBarColors()
      )
    },
  ) {
    when (state) {
      CompanyHomeState.Loading -> LoadingIndicatorView()
      is CompanyHomeState.Success ->
        Column(
          modifier = Modifier
            .padding(it)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
            .padding(bottom = 24.dp),
        ) {


          Row(verticalAlignment = Alignment.CenterVertically) {
            LetterView(account)
            Column(modifier = Modifier.padding(horizontal = 8.dp)) {
              Text(
                text = "Good ${getTimeOfDay()}",
                style = MaterialTheme.typography.bodySmall,
              )
              Text(
                text = account.toFullName(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
              )
            }
          }



          Spacer(modifier = Modifier.height(16.dp))

          CompanyHomeClientPaidView(state.paidThisMonth)

          Spacer(modifier = Modifier.height(16.dp))



          Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            OutlinedButton(onClick = { onEvent(CompanyHomeEvent.Goto.Clients) }) {
              Text(text = "Record Payment")
            }
          }


          Spacer(modifier = Modifier.height(16.dp))

          CompanyHomeSectionsView(onEvent)

          Spacer(modifier = Modifier.height(8.dp))


          state.streetPaidThisMonth.forEach { streetPaid ->
            CompanyHomeClientPaidStreetView(streetPaid)
          }



          Spacer(modifier = Modifier.height(24.dp))

        }
    }
  }
}


@Preview(showBackground = true)
@Composable
private fun CompanyHomeScreenPreview() {
  WasteManagementTheme {
    CompanyHomeScreen(
      state = companyHomeStateSuccess(LocalContext.current),
      onEvent = {}
    )
  }
}

fun companyHomeStateSuccess(context: Context) = CompanyHomeState.Success(
  state = appState(context),
  payments = listOf(paymentAccount4Preview, paymentAccount4Preview, paymentAccount4Preview),
  paidThisMonth = PaidThisMonthIndicator(2, 4, 100, .4f),
  streetPaidThisMonth = listOf(streetPaidThisMonthIndicator4Preview)
)
