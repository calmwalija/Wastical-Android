package net.techandgraphics.wastemanagement.ui.screen.company.payment.verify

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.techandgraphics.wastemanagement.R
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentStatus
import net.techandgraphics.wastemanagement.toGradient
import net.techandgraphics.wastemanagement.ui.screen.appState
import net.techandgraphics.wastemanagement.ui.screen.paymentAccount4Preview
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyVerifyPaymentScreen(
  state: CompanyVerifyPaymentState,
  channel: Flow<CompanyVerifyPaymentChannel>,
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
        text = "Verify Screenshots",
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp)
      )

      Spacer(modifier = Modifier.height(8.dp))


      OutlinedCard(modifier = Modifier.padding(horizontal = 16.dp)) {
        Column(
          modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Icon(
            painterResource(R.drawable.ic_photo_library),
            contentDescription = null,
            modifier = Modifier
              .padding(bottom = 8.dp)
              .clip(CircleShape)
              .background(brush = MaterialTheme.colorScheme.primary.toGradient())
              .size(42.dp)
              .padding(8.dp),
            tint = MaterialTheme.colorScheme.onSecondaryContainer
          )
          Text(
            text = "All Screenshot Payment Requests",
            modifier = Modifier
              .padding(horizontal = 16.dp)
              .fillMaxWidth(),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.MiddleEllipsis,
          )
          Text(
            text = state.payments.size.toString(),
            modifier = Modifier
              .padding(4.dp)
              .fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge
          )
        }
      }


      val statuses = remember {
        PaymentStatus
          .entries
          .drop(1)
          .toList()
          .toTypedArray()
      }

      var selectedChoiceIndex = remember { mutableIntStateOf(0) }

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
          CompanyVerifyPaymentView(
            paymentAccount = payment,
            imageLoader = state.state.imageLoader!!,
            channel = channel,
            onEvent = onEvent
          )
        }
      }

    }
  }
}


@Preview
@Composable
private fun CompanyVerifyScreenPreview() {
  WasteManagementTheme {
    CompanyVerifyPaymentScreen(
      state = CompanyVerifyPaymentState(
        state = appState(LocalContext.current),
        payments = (1..3)
          .map { listOf(paymentAccount4Preview, paymentAccount4Preview) }
          .toList()
          .flatten()
      ),
      channel = flow { },
      onEvent = {}
    )
  }
}
