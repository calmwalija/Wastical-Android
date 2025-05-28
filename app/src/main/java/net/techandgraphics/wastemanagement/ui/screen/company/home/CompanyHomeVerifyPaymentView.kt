package net.techandgraphics.wastemanagement.ui.screen.company.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import coil.ImageLoader
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.techandgraphics.wastemanagement.calculate
import net.techandgraphics.wastemanagement.defaultDate
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentAccountUiModel
import net.techandgraphics.wastemanagement.imageGatewayUrl
import net.techandgraphics.wastemanagement.imageScreenshotUrl
import net.techandgraphics.wastemanagement.toFullName
import net.techandgraphics.wastemanagement.toZonedDateTime
import net.techandgraphics.wastemanagement.toast
import net.techandgraphics.wastemanagement.ui.screen.company.payment.dialog.CompanyPaymentViewDialog
import net.techandgraphics.wastemanagement.ui.screen.company.payment.verify.CompanyVerifyPaymentChannel
import net.techandgraphics.wastemanagement.ui.screen.company.payment.verify.CompanyVerifyPaymentEvent
import net.techandgraphics.wastemanagement.ui.screen.imageGatewayPainter
import net.techandgraphics.wastemanagement.ui.screen.imageLoader
import net.techandgraphics.wastemanagement.ui.screen.paymentAccount4Preview
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme

@Composable fun CompanyHomeVerifyPaymentView(
  channel: Flow<CompanyHomeChannel>,
  paymentAccount: PaymentAccountUiModel,
  imageLoader: ImageLoader?,
  onEvent: (CompanyHomeEvent) -> Unit
) {

  var showOptions by remember { mutableStateOf(false) }
  var showDialog by remember { mutableStateOf(false) }

  val context = LocalContext.current
  val payment = paymentAccount.payment
  val account = paymentAccount.account


  val lifecycleOwner = LocalLifecycleOwner.current
  LaunchedEffect(key1 = channel) {
    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
      channel.collect { event ->
        showDialog = false
        when (event) {
          is CompanyHomeChannel.Payment.Failure -> context.toast(event.error.message)
          is CompanyHomeChannel.Payment.Success -> Unit
        }
      }
    }
  }



  Card(
    modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
    shape = CircleShape,
    colors = CardDefaults.elevatedCardColors(),
    onClick = { showDialog = true }
  ) {
    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {

      Box(modifier = Modifier.size(48.dp)) {

        val gatewayImagePainter =
          imageGatewayPainter(imageGatewayUrl(payment.paymentGatewayId), imageLoader!!)

        val screenshotImagePainter =
          imageGatewayPainter(payment.imageScreenshotUrl(), imageLoader)

        Image(
          painter = screenshotImagePainter,
          contentDescription = null,
          modifier = Modifier
            .padding(4.dp)
            .fillMaxSize()
            .clickable {}
            .clip(CircleShape)
        )
        Image(
          painter = gatewayImagePainter,
          contentDescription = null,
          modifier = Modifier
            .border(1.dp, MaterialTheme.colorScheme.onSecondary, CircleShape)
            .clip(CircleShape)
            .size(18.dp)
            .align(Alignment.BottomEnd),
          contentScale = ContentScale.Crop
        )
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        Column(
          modifier = Modifier
            .weight(1f)
            .padding(horizontal = 8.dp)
        ) {
          Text(
            text = account.toFullName(),
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Text(
            text = payment.createdAt.toZonedDateTime().defaultDate(),
            style = MaterialTheme.typography.bodyMedium
          )

        }

        Text(
          text = payment.calculate(),
          style = MaterialTheme.typography.bodySmall,
          maxLines = 1,
          overflow = TextOverflow.MiddleEllipsis,
          modifier = Modifier.padding(end = 16.dp)
        )

      }
    }
  }
}


@Preview(showBackground = true)
@Composable fun CompanyHomeVerifyPaymentViewPreview() {
  WasteManagementTheme {
    CompanyHomeVerifyPaymentView(
      channel = flow {},
      paymentAccount = paymentAccount4Preview,
      imageLoader = imageLoader(LocalContext.current),
      onEvent = {}
    )
  }
}
