package net.techandgraphics.wastical.ui.screen.company.home.charts

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.ui.screen.company.home.CompanyHomeState
import net.techandgraphics.wastical.ui.screen.company.home.companyHomeStateSuccess
import net.techandgraphics.wastical.ui.theme.WasticalTheme

@Composable fun CompanyHomePaidUnpaidChart(
  state: CompanyHomeState.Success,
  modifier: Modifier = Modifier,
) {

  var targetPaidValue by remember { mutableIntStateOf(0) }
  var targetTotalValue by remember { mutableIntStateOf(0) }

  LaunchedEffect(state.payment4CurrentMonth.totalPaidAccounts) {
    targetTotalValue = state.accountsSize
    targetPaidValue = state.payment4CurrentMonth.totalPaidAccounts
  }

  val animatePaidAsInt by animateIntAsState(targetValue = targetPaidValue)
  val animateTotalAsInt by animateIntAsState(targetValue = targetTotalValue)
  val animateUnPaidAsInt by remember { derivedStateOf { animateTotalAsInt - animatePaidAsInt } }

  val paidColor = MaterialTheme.colorScheme.primary
  val unpaidColor = MaterialTheme.colorScheme.error
  val surfaceColor = MaterialTheme.colorScheme.surface

  Card(colors = CardDefaults.elevatedCardColors(), modifier = modifier) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = "Paid vs Unpaid",
        style = MaterialTheme.typography.titleMedium
      )

      Spacer(Modifier.height(12.dp))

      androidx.compose.foundation.Canvas(modifier = Modifier.size(180.dp)) {
        val paidSweep = 360f * (animatePaidAsInt.toFloat() / animateTotalAsInt.toFloat())
        drawArc(paidColor, startAngle = -90f, sweepAngle = paidSweep, useCenter = true)
        drawArc(
          unpaidColor,
          startAngle = -90f + paidSweep,
          sweepAngle = 360f - paidSweep,
          useCenter = true
        )
        drawCircle(color = surfaceColor, radius = size.minDimension * .30f, style = Fill)
      }

      Spacer(Modifier.height(32.dp))

      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f),
          horizontalArrangement = Arrangement.Center
        ) {
          Spacer(
            Modifier
              .size(16.dp)
              .clip(CircleShape)
              .background(paidColor)
          )
          Text(
            text = "  Paid: $animatePaidAsInt",
            style = MaterialTheme.typography.bodySmall
          )
        }
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center,
          modifier = Modifier.weight(1f)
        ) {
          Spacer(
            Modifier
              .size(16.dp)
              .clip(CircleShape)
              .background(unpaidColor)
          )
          Text(
            text = "  Unpaid: $animateUnPaidAsInt",
            style = MaterialTheme.typography.bodySmall
          )
        }
      }
    }
  }
}


@Preview @Composable fun CompanyHomePaidUnpaidChartPreview() {
  WasticalTheme {
    CompanyHomePaidUnpaidChart(state = companyHomeStateSuccess())
  }
}
