package net.techandgraphics.wastical.ui.screen.client.payment

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import net.techandgraphics.wastical.domain.model.relations.PaymentMethodWithGatewayAndPlanUiModel
import net.techandgraphics.wastical.gatewayDrawableRes
import net.techandgraphics.wastical.ui.screen.paymentMethodWithGatewayAndPlan4Preview
import net.techandgraphics.wastical.ui.theme.WasticalTheme

private val promptMessage = listOf(
  "You're almost there, please attach a screenshot of your payment to complete your transaction.",
  "One last step, upload a screenshot of your payment to finish your transaction process.",
  "Almost done! Attach a screenshot of your payment so we can verify your transaction.",
).random()


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientPaymentScreenShotView(
  onProceed: () -> Unit,
  item: PaymentMethodWithGatewayAndPlanUiModel,
) {

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(24.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Image(
      painter = painterResource(
        id = gatewayDrawableRes[item.gateway.id.minus(1).toInt()]
      ),
      contentDescription = null,
      modifier = Modifier
        .clip(CircleShape)
        .size(92.dp),
      contentScale = ContentScale.Crop,
    )

    Text(
      text = "Attach Proof Of Payment",
      style = MaterialTheme.typography.titleLarge,
      modifier = Modifier.padding(top = 16.dp),
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )
    Text(
      text = item.gateway.name,
      modifier = Modifier.padding(bottom = 16.dp),
      color = MaterialTheme.colorScheme.primary
    )


    Text(
      text = promptMessage,
      textAlign = TextAlign.Center
    )


    Spacer(modifier = Modifier.height(32.dp))

    Row {
      Button(
        modifier = Modifier.weight(1f),
        onClick = { onProceed.invoke() }) {
        Box {
          Text(text = "Proceed")
        }
      }
    }
  }
}


@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ClientPaymentScreenShotPreview() {
  WasticalTheme {
    ClientPaymentScreenShotView(
      onProceed = {},
      item = paymentMethodWithGatewayAndPlan4Preview
    )
  }
}
