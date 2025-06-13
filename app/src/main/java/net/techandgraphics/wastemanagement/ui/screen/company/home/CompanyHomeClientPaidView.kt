package net.techandgraphics.wastemanagement.ui.screen.company.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastemanagement.toAmount
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyHomeClientPaidView(state: CompanyHomeState.Success) {

  var targetValue by remember { mutableFloatStateOf(0f) }
  val currentMonth = LocalDate.now().month
  val monthName = currentMonth.getDisplayName(TextStyle.FULL, Locale.getDefault())

  val animateAsFloat by animateFloatAsState(
    targetValue = targetValue,
    animationSpec = tween(durationMillis = 5_000)
  )

  LaunchedEffect(state.accountsSize) {
    targetValue = state.payment4CurrentMonth.totalPaidAccounts
      .toFloat()
      .div(state.accountsSize)
  }

  Card(
    modifier = Modifier
      .padding(4.dp)
      .fillMaxWidth(),
  ) {
    Column(
      modifier = Modifier
        .padding(8.dp)
        .fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {

      Text(
        text = "Month of $monthName",
        modifier = Modifier.padding(top = 8.dp)
      )

      Box(
        modifier = Modifier
          .padding(24.dp)
          .size(200.dp),
        contentAlignment = Alignment.Center
      ) {

        CircularProgressIndicator(
          progress = { animateAsFloat },
          trackColor = MaterialTheme.colorScheme.surface,
          color = MaterialTheme.colorScheme.primary.copy(.6f),
          strokeCap = StrokeCap.Round,
          strokeWidth = 20.dp,
          modifier = Modifier.fillMaxSize(),
        )

        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.padding(16.dp)
        ) {
          Text(
            text = "${state.payment4CurrentMonth.totalPaidAccounts} of ${state.accountsSize}",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
          )

          Text(
            text = state.payment4CurrentMonth.totalPaidAmount.toAmount(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary
          )
        }
      }


      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
      ) {

        Column(
          modifier = Modifier.padding(16.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(text = "Expected")
          Text(
            text = state.expectedAmountToCollect.toAmount(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
          )
        }


        Box(
          modifier = Modifier
            .width(2.dp)
            .height(40.dp)
            .background(MaterialTheme.colorScheme.secondary)
        )

        Column(
          modifier = Modifier.padding(16.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(text = "Outstanding")
          Text(
            text = state.expectedAmountToCollect
              .minus(state.payment4CurrentMonth.totalPaidAmount)
              .toAmount(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}


@Preview(showBackground = true)
@Composable
private fun CompanyHomeClientPaidViewPreview() {
  WasteManagementTheme {
    Box(modifier = Modifier.padding(16.dp)) {
      CompanyHomeClientPaidView(state = companyHomeStateSuccess())
    }
  }
}
