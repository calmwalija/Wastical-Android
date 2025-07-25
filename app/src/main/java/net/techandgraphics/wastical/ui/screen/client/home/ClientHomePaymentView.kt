package net.techandgraphics.wastical.ui.screen.client.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.R
import net.techandgraphics.wastical.data.remote.payment.PaymentStatus
import net.techandgraphics.wastical.defaultDate
import net.techandgraphics.wastical.domain.model.relations.PaymentWithAccountAndMethodWithGatewayUiModel
import net.techandgraphics.wastical.gatewayDrawableRes
import net.techandgraphics.wastical.toAmount
import net.techandgraphics.wastical.toZonedDateTime
import net.techandgraphics.wastical.ui.theme.WasticalTheme

@Composable fun ClientHomePaymentView(model: PaymentWithAccountAndMethodWithGatewayUiModel) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    shape = CircleShape,
    colors = CardDefaults.elevatedCardColors()
  ) {
    Row(
      modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Image(
        painter = painterResource(
          id = gatewayDrawableRes[model.gateway.id.minus(1).toInt()]
        ),
        contentDescription = null,
        modifier = Modifier
          .padding(horizontal = 4.dp)
          .clip(CircleShape)
          .size(38.dp),
        contentScale = ContentScale.Crop,
      )
      Column(
        modifier = Modifier
          .weight(1f)
          .padding(horizontal = 8.dp)
      ) {
        Text(
          text = model.gateway.name,
          style = MaterialTheme.typography.bodyMedium,
          maxLines = 1,
          overflow = TextOverflow.MiddleEllipsis,
          modifier = Modifier.padding(end = 8.dp)
        )
        Text(
          text = model.payment.createdAt.toZonedDateTime().defaultDate(),
          style = MaterialTheme.typography.bodySmall,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
      }

      when (model.payment.status) {
        PaymentStatus.Waiting -> {
          Icon(
            painter = painterResource(R.drawable.ic_cloud_sync),
            contentDescription = null,
            modifier = Modifier.padding(horizontal = 4.dp)
          )
        }

        else -> Unit
      }

      Text(
        text = model.coveredSize.times(model.plan.fee).toAmount(),
        style = MaterialTheme.typography.labelMedium,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.End
      )
      Spacer(modifier = Modifier.width(16.dp))
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
