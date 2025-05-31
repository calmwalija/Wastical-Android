package net.techandgraphics.wastemanagement.ui.screen.client.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastemanagement.R
import net.techandgraphics.wastemanagement.capitalize
import net.techandgraphics.wastemanagement.toAmount
import net.techandgraphics.wastemanagement.toZonedDateTime
import net.techandgraphics.wastemanagement.ui.screen.appState
import net.techandgraphics.wastemanagement.ui.screen.client.home.ClientHomeActivityItemModel
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme
import net.techandgraphics.wastemanagement.withPatten
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit


@OptIn(ExperimentalMaterial3Api::class)
@Composable fun HomeActivityView(
  state: ClientHomeState,
  homeActivity: ClientHomeActivityItemModel,
  modifier: Modifier = Modifier,
  onEvent: (ClientHomeEvent) -> Unit
) {


  val brush = Brush.horizontalGradient(
    listOf(
      homeActivity.iconBackground.copy(.7f),
      homeActivity.iconBackground.copy(.8f),
      homeActivity.iconBackground
    )
  )

  Surface(
    tonalElevation = 3.dp,
    shape = RoundedCornerShape(8),
    color = homeActivity.containerColor,
    modifier = modifier.padding(4.dp),
    onClick = { onEvent(homeActivity.event) },
    enabled = homeActivity.clickable
  ) {
    if (homeActivity.activity == homeActivityUiModels.last().activity) {
      state.state.invoices.firstOrNull()?.let { oldPay ->
        state.state.paymentPlans.firstOrNull()?.let { plan ->
          Column(modifier = Modifier.padding(16.dp)) {

            val dueDate =
              oldPay.createdAt.toZonedDateTime().plusMonths(oldPay.numberOfMonths.toLong())
            val monthCount = ChronoUnit.MONTHS.between(ZonedDateTime.now(), dueDate)

            val overdueFee = plan.fee.times(monthCount).toAmount()
            val isPaymentDue = ZonedDateTime.now().isAfter(dueDate)

            val drawableRes = if (isPaymentDue) R.drawable.ic_close else homeActivity.drawableRes
            val activity = if (isPaymentDue) homeActivity.activity else "Next Payment"

            val error = MaterialTheme.colorScheme.onError

            val brush = Brush.horizontalGradient(
              listOf(
                (if (isPaymentDue) error else homeActivity.iconBackground).copy(.7f),
                (if (isPaymentDue) error else homeActivity.iconBackground).copy(.8f),
                (if (isPaymentDue) error else homeActivity.iconBackground)
              )
            )


            Icon(
              painterResource(drawableRes), null,
              modifier = Modifier
                .padding(bottom = 8.dp)
                .clip(CircleShape)
                .background(brush = brush)
                .size(42.dp)
                .padding(8.dp),
              tint = homeActivity.iconTint
            )

            Text(
              text = activity,
              style = MaterialTheme.typography.bodySmall,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )

            Text(
              text = dueDate.withPatten(),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )

          }
        }
      }
    } else {
      Column(modifier = Modifier.padding(16.dp)) {
        Icon(
          painterResource(homeActivity.drawableRes), null,
          modifier = Modifier
            .padding(bottom = 8.dp)
            .clip(CircleShape)
            .background(brush = brush)
            .size(42.dp)
            .padding(8.dp),
          tint = homeActivity.iconTint
        )
        Text(
          text = homeActivity.activity,
          style = MaterialTheme.typography.bodySmall,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )

        state.state.trashSchedules.forEach { trashSchedule ->
          Text(
            text = trashSchedule.dayOfWeek.lowercase().capitalize(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
        }
      }
    }
  }

}

@Preview(showBackground = true)
@Composable
private fun HomeActivityViewPreview() {
  WasteManagementTheme {
    HomeActivityView(
      state = ClientHomeState(
        state = appState(LocalContext.current)
      ),
      homeActivity = homeActivityUiModels.last(),
      onEvent = {}
    )
  }
}
