package net.techandgraphics.wastemanagement.ui.screen.company.home

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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastemanagement.data.local.database.dashboard.account.Payment4CurrentMonth
import net.techandgraphics.wastemanagement.getTimeOfDay
import net.techandgraphics.wastemanagement.toFullName
import net.techandgraphics.wastemanagement.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastemanagement.ui.screen.account4Preview
import net.techandgraphics.wastemanagement.ui.screen.client.home.LetterView
import net.techandgraphics.wastemanagement.ui.screen.company4Preview
import net.techandgraphics.wastemanagement.ui.screen.companyContact4Preview
import net.techandgraphics.wastemanagement.ui.screen.payment4CurrentLocationMonth4Preview
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CompanyHomeScreen(
  state: CompanyHomeState,
  onEvent: (CompanyHomeEvent) -> Unit,
) {

  var showMenuItems by remember { mutableStateOf(false) }

  when (state) {
    CompanyHomeState.Loading -> LoadingIndicatorView()
    is CompanyHomeState.Success -> Scaffold(
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
            IconButton(onClick = { }) {
              BadgedBox(badge = { Badge() }) {
                Icon(Icons.Outlined.Notifications, null)
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

                DropdownMenuItem(text = {
                  Text(text = "Sign Out")
                }, onClick = {})

              }
            }

          },
          modifier = Modifier.shadow(0.dp),
          colors = TopAppBarDefaults.topAppBarColors()
        )
      }
    ) {
      Column(
        modifier = Modifier
          .padding(it)
          .verticalScroll(rememberScrollState())
          .padding(horizontal = 24.dp)
          .fillMaxWidth()
          .padding(bottom = 24.dp),
      ) {


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



        Spacer(modifier = Modifier.height(16.dp))

        CompanyHomeClientPaidView(state)

        Spacer(modifier = Modifier.height(16.dp))


        Row(
          modifier = Modifier.padding(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {

          Text(
            text = "Payments per Location",
            modifier = Modifier.weight(1f)
          )

          TextButton(onClick = { onEvent(CompanyHomeEvent.Goto.PerLocation) }) {
            Text(text = "See all")
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null)
          }
        }


        state.payment4CurrentLocationMonth.forEach { location ->
          CompanyHomeClientPaidStreetView(location, onEvent)
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
      state = companyHomeStateSuccess(),
      onEvent = {}
    )
  }
}

fun companyHomeStateSuccess() = CompanyHomeState.Success(
  payment4CurrentLocationMonth = listOf(payment4CurrentLocationMonth4Preview),
  account = account4Preview,
  company = company4Preview,
  companyContact = companyContact4Preview,
  accountsSize = 200,
  payment4CurrentMonth = Payment4CurrentMonth(120, 935_000),
  expectedAmountToCollect = 2444_000,
  paymentPlanAgainstAccounts = listOf()
)
