package net.techandgraphics.wastical.ui.screen.client.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.defaultDate
import net.techandgraphics.wastical.domain.model.relations.PaymentWithAccountAndMethodWithGatewayUiModel
import net.techandgraphics.wastical.gatewayDrawableRes
import net.techandgraphics.wastical.toAmount
import net.techandgraphics.wastical.toZonedDateTime
import net.techandgraphics.wastical.ui.theme.WasticalTheme
import java.time.format.TextStyle
import java.util.Locale

@Composable fun ClientHomePaymentView(
  modifier: Modifier = Modifier,
  model: PaymentWithAccountAndMethodWithGatewayUiModel,
) {
  ElevatedCard(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    shape = MaterialTheme.shapes.large,
    colors = CardDefaults.elevatedCardColors()
  ) {
    Column {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .padding(16.dp)
      ) {
        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = .12f)),
          contentAlignment = Alignment.Center
        ) {
          Image(
            painter = painterResource(
              id = gatewayDrawableRes[model.gateway.id.minus(1).toInt()]
            ),
            contentDescription = null,
          )
        }

        Column(
          modifier = Modifier
            .padding(start = 12.dp)
            .weight(1f)
        ) {
          Text(
            text = model.gateway.name,
            style = MaterialTheme.typography.titleSmall,
          )
          Text(
            text = model.payment.createdAt.toZonedDateTime().defaultDate(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
          )
        }

        Text(
          text = model.plan.fee.times(model.coveredSize).toAmount(),
          color = MaterialTheme.colorScheme.primary,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
      }

      HorizontalDivider()

      Column(modifier = Modifier.padding(16.dp)) {
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          model.covered.forEach { item ->
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Text(
                text = java.time.Month.of(item.month)
                  .getDisplayName(TextStyle.SHORT, Locale.getDefault())
                  .plus(" ${item.year}"),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
              )
            }
          }
        }
      }
    }
  }
}


@Preview(showBackground = true)
@Composable fun ClientHomePaymentViewPreview() {
  WasticalTheme {
    ClientHomePaymentView(
      model = clientHomeStateSuccess().payments.first(),
    )
  }
}
