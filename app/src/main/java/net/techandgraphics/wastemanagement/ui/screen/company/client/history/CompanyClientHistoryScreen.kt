package net.techandgraphics.wastemanagement.ui.screen.company.client.history

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentStatus
import net.techandgraphics.wastemanagement.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastemanagement.ui.screen.account4Preview
import net.techandgraphics.wastemanagement.ui.screen.company.AccountInfoView
import net.techandgraphics.wastemanagement.ui.screen.company.payment.verify.CompanyVerifyPaymentEvent
import net.techandgraphics.wastemanagement.ui.screen.imageLoader
import net.techandgraphics.wastemanagement.ui.screen.payment4Preview
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyClientHistoryScreen(
  state: CompanyClientHistoryState,
  onEvent: (CompanyVerifyPaymentEvent) -> Unit,
) {


  Scaffold(
    topBar = {
      TopAppBar(
        title = {},
        navigationIcon = {
          IconButton(onClick = { }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
          }
        },
        modifier = Modifier.shadow(0.dp),
        colors = TopAppBarDefaults.topAppBarColors()
      )
    },
  ) {
    Column(modifier = Modifier.padding(it)) {

      Text(
        text = "Payment History",
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.padding(16.dp)
      )


      val statuses = remember {
        PaymentStatus
          .entries
          .drop(1)
          .toList()
          .toTypedArray()
      }

      var selectedChoiceIndex = remember { mutableIntStateOf(0) }


      if (state is CompanyClientHistoryState.Success) {

        AccountInfoView(state.account)

        Spacer(modifier = Modifier.height(8.dp))

        SingleChoiceSegmentedButtonRow(
          modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
        ) {
          statuses.forEachIndexed { index, status ->

            SegmentedButton(
              selected = selectedChoiceIndex.intValue == index,
              onClick = {
                selectedChoiceIndex.intValue = index
                onEvent(CompanyVerifyPaymentEvent.Verify.Button.Status(status))
              },
              icon = {},
              colors = SegmentedButtonDefaults.colors(
                activeContainerColor = MaterialTheme.colorScheme.primary.copy(.4f),
                activeContentColor = MaterialTheme.colorScheme.onSecondaryContainer
              ),
              shape = SegmentedButtonDefaults.itemShape(
                index = index,
                count = statuses.count()
              )
            ) {
              Text(
                text = status.name,
                modifier = Modifier.padding(8.dp)
              )
            }
          }
        }


        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
          items(state.payments) { payment ->
            CompanyClientHistoryView(
              payment = payment,
              imageLoader = state.imageLoader,
              onEvent = onEvent
            )
          }
        }

      } else {
        LoadingIndicatorView()
      }

    }
  }
}


@Preview
@Composable
private fun CompanyClientHistoryScreenPreview() {
  WasteManagementTheme {
    CompanyClientHistoryScreen(
      state = CompanyClientHistoryState.Success(
        account = account4Preview,
        imageLoader = imageLoader(LocalContext.current),
        payments = listOf(payment4Preview)
      ),
      onEvent = {}
    )
  }
}
