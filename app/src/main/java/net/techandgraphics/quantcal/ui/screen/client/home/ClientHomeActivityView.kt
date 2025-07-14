package net.techandgraphics.quantcal.ui.screen.client.home

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.quantcal.R
import net.techandgraphics.quantcal.capitalize
import net.techandgraphics.quantcal.toShortMonthName
import net.techandgraphics.quantcal.toZonedDateTime
import net.techandgraphics.quantcal.ui.theme.QuantcalTheme
import java.time.YearMonth


@OptIn(ExperimentalMaterial3Api::class)
@Composable fun HomeActivityView(
  state: ClientHomeState.Success,
  homeActivity: ClientHomeActivityItemModel,
  modifier: Modifier = Modifier,
  onEvent: (ClientHomeEvent) -> Unit,
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

      val lastMonthCovered = state.invoices
        .flatMap { it.covered }
        .sortedWith(compareBy({ it.year }, { it.month }))
        .lastOrNull()


      Column(modifier = Modifier.padding(16.dp)) {

        val lastPaid = if (lastMonthCovered != null)
          YearMonth.of(lastMonthCovered.year, lastMonthCovered.month) else {
          val createdDate = state.account.createdAt.toZonedDateTime().toLocalDate()
          YearMonth.of(createdDate.year, createdDate.monthValue.minus(1))
        }


        val nextDue = lastPaid.plusMonths(1)
        val isPaymentDue = nextDue <= YearMonth.now()

        val iconRes = if (isPaymentDue) R.drawable.ic_close else homeActivity.drawableRes
        val statusLabel = if (isPaymentDue) homeActivity.activity else "Next Payment"

        val color =
          if (isPaymentDue) MaterialTheme.colorScheme.onError else homeActivity.iconBackground
        val brush = Brush.horizontalGradient(
          listOf(color.copy(0.7f), color.copy(0.8f), color)
        )

        Icon(
          painter = painterResource(iconRes),
          contentDescription = null,
          modifier = Modifier
            .padding(bottom = 8.dp)
            .clip(CircleShape)
            .background(brush)
            .size(42.dp)
            .padding(8.dp),
          tint = homeActivity.iconTint
        )
        Text(
          text = statusLabel,
          style = MaterialTheme.typography.bodySmall,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
        Text(
          text = "${nextDue.month.value.toShortMonthName()} ${nextDue.year}",
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
//          }
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

        state.companyBinCollections.forEach { binCollection ->
          Text(
            text = binCollection.dayOfWeek.lowercase().capitalize(),
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
  QuantcalTheme {
    HomeActivityView(
      state = clientHomeStateSuccess(),
      homeActivity = homeActivityUiModels.last(),
      onEvent = {}
    )
  }
}
