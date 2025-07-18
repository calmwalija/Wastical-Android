package net.techandgraphics.quantcal.ui.screen.client.home

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.techandgraphics.quantcal.R
import net.techandgraphics.quantcal.domain.model.account.AccountUiModel
import net.techandgraphics.quantcal.getTimeOfDay
import net.techandgraphics.quantcal.toFullName
import net.techandgraphics.quantcal.toInitials
import net.techandgraphics.quantcal.ui.screen.LoadingIndicatorView
import net.techandgraphics.quantcal.ui.screen.account4Preview
import net.techandgraphics.quantcal.ui.screen.company4Preview
import net.techandgraphics.quantcal.ui.screen.paymentMethodWithGatewayAndPlan4Preview
import net.techandgraphics.quantcal.ui.screen.paymentPlan4Preview
import net.techandgraphics.quantcal.ui.screen.paymentRequestWithAccount4Preview
import net.techandgraphics.quantcal.ui.screen.paymentWithAccountAndMethodWithGateway4Preview
import net.techandgraphics.quantcal.ui.theme.QuantcalTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
  state: ClientHomeState,
  channel: Flow<ClientHomeChannel>,
  onEvent: (ClientHomeEvent) -> Unit,
) {

  var showMenuOptions by remember { mutableStateOf(false) }
  var isFetching by remember { mutableStateOf(false) }

  when (state) {
    ClientHomeState.Loading -> LoadingIndicatorView()
    is ClientHomeState.Success -> {


      val lifecycleOwner = LocalLifecycleOwner.current
      LaunchedEffect(key1 = channel) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
          channel.collect { event ->
            when (event) {
              is ClientHomeChannel.Fetch -> isFetching = when (event) {
                is ClientHomeChannel.Fetch.Error -> false
                ClientHomeChannel.Fetch.Fetching -> true
                ClientHomeChannel.Fetch.Success -> false
              }

              ClientHomeChannel.Goto.Login -> onEvent(ClientHomeEvent.Goto.Login)
            }
          }
        }
      }

      Scaffold(
        topBar = {
          TopAppBar(
            title = {
              Text(
                text = state.company.name,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                  .padding(horizontal = 16.dp)
                  .fillMaxWidth()
              )
            },
            actions = {

              IconButton(
                enabled = isFetching.not(),
                onClick = { onEvent(ClientHomeEvent.Button.Fetch) }) {
                if (isFetching) CircularProgressIndicator(modifier = Modifier.size(24.dp)) else {
                  Icon(
                    painter = painterResource(R.drawable.ic_cloud_download),
                    contentDescription = null
                  )
                }
              }

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

                  HorizontalDivider()
                  DropdownMenuItem(text = {
                    Text(text = "Logout")
                  }, onClick = {
                    showMenuOptions = false
                    onEvent(ClientHomeEvent.Button.Logout)
                  })
                }
              }

            },
            colors = TopAppBarDefaults.topAppBarColors(
              containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
          )
        },
      ) {
        LazyColumn(
          contentPadding = it,
          modifier = Modifier.padding(vertical = 32.dp, horizontal = 16.dp)
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
            ClientHomeQuickActionView(
              homeQuickActionUiModel = homeQuickActionUiModels.first(),
              onEvent = { onEvent(ClientHomeEvent.Button.MakePayment(state.account.id)) }
            )
          }

          item { Spacer(modifier = Modifier.height(24.dp)) }

          item {
            Text(
              text = "Upcoming Activities",
              modifier = Modifier.padding(8.dp),
              fontWeight = FontWeight.Bold,
            )
            FlowRow(maxItemsInEachRow = 2) {
              homeActivityUiModels
                .mapIndexed { index, item ->
                  if (index == 1) item.copy(
                    containerColor = MaterialTheme.colorScheme.primary.copy(.1f),
                    iconBackground = MaterialTheme.colorScheme.primary.copy(.5f)
                  ) else item
                }
                .forEach {
                  HomeActivityView(
                    state = state,
                    modifier = Modifier.fillMaxWidth(.5f),
                    homeActivity = it,
                    onEvent = { onEvent(ClientHomeEvent.Button.MakePayment(state.account.id)) }
                  )
                }
            }
          }

          item { Spacer(modifier = Modifier.height(24.dp)) }

          item {
            Text(
              text = "Payment Methods",
              modifier = Modifier.padding(8.dp),
              fontWeight = FontWeight.Bold,
            )
          }

          items(state.paymentMethods) { paymentMethod ->
            ClientPaymentMethodView(
              model = paymentMethod,
              onEvent = onEvent
            )
          }

          item { Spacer(modifier = Modifier.height(24.dp)) }

          if (state.invoices.isNotEmpty()) {
            item {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
              ) {
                Text(
                  text = "Paid Invoice Reports",
                  modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                  fontWeight = FontWeight.Bold,
                )

                if (state.invoices.size > 3)
                  TextButton(onClick = { onEvent(ClientHomeEvent.Goto.Invoice(state.account.id)) }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text(text = "See all", style = MaterialTheme.typography.bodyMedium)
                      Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null)
                    }
                  }

              }
            }
            items(state.invoices) { payment ->
              ClientHomeInvoiceView(
                model = payment,
                paymentPlan = state.paymentPlan,
                onEvent = onEvent
              )
            }
          }

          item { Spacer(modifier = Modifier.height(24.dp)) }

          if (state.payments.isNotEmpty()) {
            item {
              Text(
                text = "Payments Waiting Approval",
                modifier = Modifier.padding(8.dp),
                fontWeight = FontWeight.Bold,
              )
            }

            items(state.payments) { payment ->
              ClientHomePaymentView(model = payment)
            }

          }

          item { Spacer(modifier = Modifier.height(24.dp)) }

          if (state.paymentRequests.isNotEmpty()) {
            item {
              Text(
                text = "Awaiting Internet Connection",
                modifier = Modifier.padding(8.dp),
                fontWeight = FontWeight.Bold,
              )
            }

            items(state.paymentRequests) { payment ->
              ClientHomePaymentRequestView(model = payment)
            }

          }

        }
      }
    }
  }
}


@Composable fun LetterView(account: AccountUiModel) {
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
      text = account.toInitials(),
      style = MaterialTheme.typography.bodySmall,
      modifier = Modifier.padding(4.dp),
      fontWeight = FontWeight.Bold,
    )
  }

}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
  QuantcalTheme {
    HomeScreen(
      state = clientHomeStateSuccess(),
      channel = flow { }
    ) {}
  }
}

fun clientHomeStateSuccess() = ClientHomeState.Success(
  account = account4Preview,
  company = company4Preview,
  paymentPlan = paymentPlan4Preview,
  paymentMethods = listOf(paymentMethodWithGatewayAndPlan4Preview),
  invoices = listOf(paymentWithAccountAndMethodWithGateway4Preview),
  companyBinCollections = listOf(),
  payments = listOf(paymentWithAccountAndMethodWithGateway4Preview),
  paymentRequests = listOf(paymentRequestWithAccount4Preview)
)
