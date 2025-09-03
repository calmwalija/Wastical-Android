package net.techandgraphics.wastical.ui.screen.company.payment.verify

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.data.remote.payment.PaymentType
import net.techandgraphics.wastical.defaultDateTime
import net.techandgraphics.wastical.domain.model.relations.PaymentWithAccountAndMethodWithGatewayUiModel
import net.techandgraphics.wastical.gatewayDrawableRes
import net.techandgraphics.wastical.toAmount
import net.techandgraphics.wastical.toFullName
import net.techandgraphics.wastical.toZonedDateTime
import net.techandgraphics.wastical.ui.screen.paymentWithAccountAndMethodWithGateway4Preview
import net.techandgraphics.wastical.ui.theme.Green
import net.techandgraphics.wastical.ui.theme.WasticalTheme

@Composable fun CompanyVerifyPaymentItem(
  modifier: Modifier = Modifier,
  model: PaymentWithAccountAndMethodWithGatewayUiModel,
  onEvent: (CompanyVerifyPaymentEvent) -> Unit,
) {

  val payment = model.payment
  val account = model.account
  val plan = model.plan
  val gateway = model.gateway
  val months = model.coveredSize

  Card(
    modifier = modifier.padding(vertical = 6.dp),
    colors = CardDefaults.elevatedCardColors(),
    onClick = { onEvent(CompanyVerifyPaymentEvent.Goto.Profile(account.id)) }
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(44.dp)) {
          Image(
            painter = painterResource(gatewayDrawableRes[gateway.id.minus(1).toInt()]),
            contentDescription = null,
            modifier = Modifier
              .clip(CircleShape)
              .fillMaxSize(),
            contentScale = ContentScale.Crop
          )
        }

        Column(
          modifier = Modifier
            .weight(1f)
            .padding(start = 12.dp)
        ) {
          Text(
            text = account.toFullName(),
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Text(
            text = "${gateway.name} • $months month${if (months == 1) "" else "s"}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Text(
            text = payment.createdAt.toZonedDateTime().defaultDateTime(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
        Text(
          text = plan.fee.times(months).toAmount(),
          style = MaterialTheme.typography.titleSmall,
          color = MaterialTheme.colorScheme.primary,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      val showAttachment = model.gateway.type != PaymentType.Cash.name

      val labels = if (showAttachment)
        listOf(payment.proofExt!!.uppercase(), "Deny", "Approve") else listOf("Deny", "Approve")

      var selectedIndex by remember { mutableIntStateOf(-1) }

      SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        labels.forEachIndexed { index, label ->
          SegmentedButton(
            selected = selectedIndex == index,
            onClick = {
              selectedIndex = index
              if (showAttachment) {
                when (index) {
                  0 -> onEvent(CompanyVerifyPaymentEvent.Payment.Image(payment))
                  1 -> onEvent(CompanyVerifyPaymentEvent.Payment.Deny(payment))
                  2 -> onEvent(CompanyVerifyPaymentEvent.Payment.Approve(payment))
                }
              } else {
                when (index) {
                  0 -> onEvent(CompanyVerifyPaymentEvent.Payment.Deny(payment))
                  1 -> onEvent(CompanyVerifyPaymentEvent.Payment.Approve(payment))
                }
              }
            },
            shape = SegmentedButtonDefaults.itemShape(
              index = index,
              count = labels.size,
            ),
            icon = {},
          ) {
            Text(
              text = label,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              style = MaterialTheme.typography.labelMedium,
              color = when (label) {
                "Deny" -> MaterialTheme.colorScheme.error
                "Approve" -> Green
                else -> MaterialTheme.colorScheme.onSurface
              }
            )
          }
        }
      }
    }
  }
}


@Preview(showBackground = true)
@Composable fun CompanyVerifyPaymentItemPreview() {
  WasticalTheme {
    CompanyVerifyPaymentItem(
      model = paymentWithAccountAndMethodWithGateway4Preview,
      onEvent = {}
    )
  }
}
