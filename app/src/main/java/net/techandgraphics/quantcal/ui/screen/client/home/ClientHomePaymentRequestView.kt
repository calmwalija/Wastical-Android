package net.techandgraphics.quantcal.ui.screen.client.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.quantcal.R
import net.techandgraphics.quantcal.defaultDateTime
import net.techandgraphics.quantcal.domain.model.relations.PaymentRequestWithAccountUiModel
import net.techandgraphics.quantcal.toAmount
import net.techandgraphics.quantcal.toZonedDateTime
import net.techandgraphics.quantcal.ui.theme.QuantcalTheme

@Composable fun ClientHomePaymentRequestView(
  model: PaymentRequestWithAccountUiModel,
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    shape = CircleShape,
    colors = CardDefaults.elevatedCardColors()
  ) {
    Row(
      modifier = Modifier.padding(vertical = 6.dp, horizontal = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        painterResource(R.drawable.ic_cloud_sync),
        contentDescription = null,
        modifier = Modifier
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.surface)
          .size(42.dp)
          .padding(8.dp),
      )
      Column(
        modifier = Modifier
          .weight(1f)
          .padding(horizontal = 8.dp)
      ) {
        Text(
          text = model.fee.times(model.payment.months).toAmount(),
          style = MaterialTheme.typography.bodyMedium,
          maxLines = 1,
          overflow = TextOverflow.MiddleEllipsis,
          modifier = Modifier.padding(end = 8.dp)
        )
        Text(
          text = model.payment.createdAt.toZonedDateTime().defaultDateTime(),
          style = MaterialTheme.typography.bodySmall,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
      }
    }
  }
}


@Preview(showBackground = true)
@Composable fun ClientHomePaymentRequestViewPreview() {
  QuantcalTheme {
    ClientHomePaymentRequestView(
      model = clientHomeStateSuccess().paymentRequests.first(),
    )
  }
}
