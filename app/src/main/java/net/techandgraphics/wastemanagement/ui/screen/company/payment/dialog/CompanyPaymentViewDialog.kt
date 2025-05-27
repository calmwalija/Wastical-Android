package net.techandgraphics.wastemanagement.ui.screen.company.payment.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import net.techandgraphics.wastemanagement.R
import net.techandgraphics.wastemanagement.calculate
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentStatus
import net.techandgraphics.wastemanagement.defaultDateTime
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentAccountUiModel
import net.techandgraphics.wastemanagement.toFullName
import net.techandgraphics.wastemanagement.toZonedDateTime
import net.techandgraphics.wastemanagement.ui.screen.company.payment.CompanyPaymentEvent
import net.techandgraphics.wastemanagement.ui.screen.company.payment.CompanyPaymentEvent.Payment.Button
import net.techandgraphics.wastemanagement.ui.screen.paymentAccount4Preview
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme


@Composable fun CompanyPaymentViewDialog(
  paymentAccount: PaymentAccountUiModel,
  onEvent: (CompanyPaymentEvent) -> Unit,
  onDismissRequest: () -> Unit
) {


  val account = paymentAccount.account
  val payment = paymentAccount.payment

  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Image(
      painterResource(R.drawable.im_placeholder),
      contentDescription = null,
      modifier = Modifier
        .zIndex(1f)
        .offset(y = 42.dp)
        .clip(CircleShape)
        .size(120.dp)
    )
    Card(modifier = Modifier.fillMaxWidth()) {
      Column(
        modifier = Modifier
          .padding(24.dp),
        horizontalAlignment = Alignment.End
      ) {


        FilledIconButton(onClick = onDismissRequest) { Icon(Icons.Default.Close, null) }

        Column(
          modifier = Modifier
            .padding(top = 8.dp)
            .fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {

          Text(
            text = account.toFullName(),
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Text(
            text = payment.createdAt.toZonedDateTime().defaultDateTime(),
            style = MaterialTheme.typography.bodyMedium
          )
          Text(text = payment.paymentGatewayName)

          Text(
            text = payment.calculate(),
            style = MaterialTheme.typography.titleMedium,
          )
        }


        Card(
          modifier = Modifier
            .padding(vertical = 24.dp)
            .fillMaxWidth(),
          colors = CardDefaults.outlinedCardColors(),
        ) {
          Text(
            text = payment.screenshotText,
            modifier = Modifier
              .padding(32.dp)
              .fillMaxWidth(),
            textAlign = TextAlign.Center
          )
        }


        Row {
          TextButton(
            onClick = { onEvent(Button.Status(payment, PaymentStatus.Declined)) },
            modifier = Modifier.fillMaxWidth(.4f)
          ) { Text(text = "Decline") }

          Spacer(modifier = Modifier.width(10.dp))

          Button(
            onClick = { onEvent(Button.Status(payment, PaymentStatus.Approved)) },
            modifier = Modifier.weight(1f)
          ) { Text(text = "Approve") }

        }

      }
    }
  }
}

@Preview
@Composable fun CompanyPaymentViewDialogPreview() {
  WasteManagementTheme {
    CompanyPaymentViewDialog(
      paymentAccount = paymentAccount4Preview,
      onEvent = {},
      onDismissRequest = {}
    )
  }
}
