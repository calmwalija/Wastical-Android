package net.techandgraphics.wastical.ui.screen.client.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
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
import net.techandgraphics.wastical.R
import net.techandgraphics.wastical.capitalize
import net.techandgraphics.wastical.toShortMonthName
import net.techandgraphics.wastical.toZonedDateTime
import net.techandgraphics.wastical.ui.theme.WasticalTheme
import java.time.YearMonth
import kotlin.math.min


@OptIn(ExperimentalMaterial3Api::class)
@Composable fun HomeActivityView(
  state: ClientHomeState.Success,
  homeActivity: ClientHomeActivityItemModel,
  modifier: Modifier = Modifier,
  onEvent: () -> Unit,
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
    modifier = modifier
      .padding(4.dp)
      .heightIn(min = 140.dp),
    onClick = { onEvent() },
    enabled = homeActivity.clickable
  ) {
    if (homeActivity.activity == homeActivityUiModels.last().activity) {

      Column(modifier = Modifier.padding(16.dp)) {

        val isPaymentDue = state.monthsOutstanding > 0

        val nextDueYm: YearMonth = state.outstandingMonths.firstOrNull()?.let { ym ->
          YearMonth.of(ym.year, ym.month)
        } ?: run {
          val lastCovered = state.lastMonthCovered
          if (lastCovered != null) YearMonth.of(lastCovered.year, lastCovered.month).plusMonths(1)
          else {
            val created = state.account.createdAt.toZonedDateTime()
            YearMonth.of(created.year, created.month).plusMonths(1)
          }
        }

        val iconRes = if (isPaymentDue) R.drawable.ic_close else homeActivity.drawableRes
        val statusLabel = if (isPaymentDue) "Payment due" else "Next Payment"

        val color =
          if (isPaymentDue) MaterialTheme.colorScheme.error else homeActivity.iconBackground
        val brush = Brush.horizontalGradient(
          listOf(color.copy(0.7f), color.copy(0.8f), color)
        )

        val billingDay = state.company.billingDate
        fun dayInMonth(ym: YearMonth) = min(billingDay, ym.lengthOfMonth())

        Icon(
          painter = painterResource(iconRes),
          contentDescription = null,
          modifier = Modifier
            .padding(vertical = 8.dp)
            .clip(CircleShape)
            .background(brush)
            .size(42.dp)
            .padding(8.dp),
          tint = homeActivity.iconTint
        )

        Text(
          text = statusLabel,
          style = MaterialTheme.typography.bodySmall,
          color = if (isPaymentDue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
        )
        Text(
          text = "${dayInMonth(nextDueYm)} ${nextDueYm.month.value.toShortMonthName()} ${nextDueYm.year}",
          maxLines = 1,
          color = if (isPaymentDue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
          overflow = TextOverflow.Ellipsis,
        )

      }

    } else {
      Column(modifier = Modifier.padding(16.dp)) {
        Icon(
          painterResource(homeActivity.drawableRes), null,
          modifier = Modifier
            .padding(vertical = 8.dp)
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
  WasticalTheme {
    HomeActivityView(
      state = clientHomeStateSuccess(),
      homeActivity = homeActivityUiModels.last(),
      onEvent = {}
    )
  }
}
