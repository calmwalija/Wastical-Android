package net.techandgraphics.wastemanagement.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.techandgraphics.wastemanagement.getTimeOfDay
import net.techandgraphics.wastemanagement.toFullName
import net.techandgraphics.wastemanagement.ui.screen.payment.imageLoader
import net.techandgraphics.wastemanagement.ui.screen.payment.paymentPlan
import net.techandgraphics.wastemanagement.ui.screen.transaction.TransactionView
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
  state: HomeState,
  channel: Flow<HomeChannel>,
  onEvent: (HomeEvent) -> Unit
) {

  var showMenuOptions by remember { mutableStateOf(false) }
  val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
  LaunchedEffect(key1 = channel) {
    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
    }
  }

  val paymentPlan = state.paymentPlans.firstOrNull() ?: return


  Scaffold(
    topBar = {
      TopAppBar(
        title = { },
        actions = {
          IconButton(onClick = { }) {
            BadgedBox(badge = { Badge() }) {
              Icon(Icons.Outlined.Notifications, null)
            }
          }

          IconButton(onClick = { showMenuOptions = true }) {
            Icon(Icons.Default.MoreVert, null)
            DropdownMenu(showMenuOptions, onDismissRequest = { showMenuOptions = false }) {

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
    Column(
      modifier = Modifier
        .padding(it)
        .padding(horizontal = 24.dp)
        .verticalScroll(rememberScrollState())
        .fillMaxWidth()
        .padding(bottom = 24.dp),
    ) {


      state.accounts.forEach { account ->
        Row(verticalAlignment = Alignment.CenterVertically) {
          LetterView()
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
      }


      Spacer(modifier = Modifier.height(24.dp))

      Text(
        text = "Upcoming Activities",
        modifier = Modifier.padding(8.dp),
        fontWeight = FontWeight.Bold,
      )

      FlowRow(maxItemsInEachRow = 2) {
        homeActivityUiModels
          .mapIndexed { index, item ->
            if (index == 0) item.copy(
              containerColor = MaterialTheme.colorScheme.primary.copy(.1f),
              iconBackground = MaterialTheme.colorScheme.primary.copy(.5f)
            ) else item
          }
          .forEach {
            HomeActivityView(
              state = state,
              modifier = Modifier.fillMaxWidth(.5f),
              homeActivityUiModel = it,
              onEvent = onEvent
            )
          }
      }

      Spacer(modifier = Modifier.height(24.dp))

      Text(
        text = "Quick Actions",
        modifier = Modifier.padding(8.dp),
        fontWeight = FontWeight.Bold,
      )

      FlowRow(maxItemsInEachRow = 2) {
        homeActionUiModels.forEach {
          HomeQuickActionView(
            homeActionUiModel = it,
            modifier = Modifier.fillMaxWidth(.5f),
            onEvent = onEvent
          )
        }
      }


      Spacer(modifier = Modifier.height(24.dp))


      Text(
        text = "Paid Invoice Reports",
        modifier = Modifier.padding(8.dp),
        fontWeight = FontWeight.Bold,
      )


      state.invoices.forEach { invoice ->
        HomeInvoiceView(
          invoice = invoice,
          modifier = Modifier.fillMaxWidth(1f),
          onEvent = onEvent
        )
      }


      Spacer(modifier = Modifier.height(24.dp))

      Text(
        text = "Recent Payments",
        modifier = Modifier.padding(8.dp),
        fontWeight = FontWeight.Bold,
      )


      state.payments.forEach { payment ->
        TransactionView(payment, paymentPlan, state.imageLoader)
      }
    }

    Spacer(modifier = Modifier.height(42.dp))
  }


}


@Composable fun LetterView() {
  val brush = Brush.horizontalGradient(
    listOf(
      MaterialTheme.colorScheme.primary.copy(.7f),
      MaterialTheme.colorScheme.primary.copy(.8f),
      MaterialTheme.colorScheme.primary
    )
  )

  Box(contentAlignment = Alignment.Center) {
    Box(
      modifier = Modifier
        .clip(CircleShape)
        .size(38.dp)
        .background(MaterialTheme.colorScheme.primary.copy(.2f))
    )
    Box(
      modifier = Modifier
        .clip(CircleShape)
        .size(44.dp)
        .background(MaterialTheme.colorScheme.primary.copy(.1f))
    )
    Box(
      modifier = Modifier
        .clip(CircleShape)
        .size(32.dp)
        .background(
          brush = brush
        )
    )
    Text(
      text = "Mj",
      style = MaterialTheme.typography.bodySmall,
      modifier = Modifier.padding(4.dp),
      fontWeight = FontWeight.Bold,
    )
  }

}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
  WasteManagementTheme {
    HomeScreen(
      state = HomeState(
        accounts = listOf(account),
        payments = listOf(payment, payment),
        invoices = listOf(payment, payment),
        imageLoader = imageLoader(LocalContext.current),
        paymentPlans = listOf(paymentPlan)
      ),
      channel = flow { },
      onEvent = {}
    )
  }
}
