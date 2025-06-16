package net.techandgraphics.wastemanagement.ui.screen.company.payment.verify

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
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
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
import net.techandgraphics.wastemanagement.R
import net.techandgraphics.wastemanagement.calculate
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentStatus
import net.techandgraphics.wastemanagement.defaultDateTime
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentMethodUiModel
import net.techandgraphics.wastemanagement.domain.model.relations.PaymentWithAccountAndMethodWithGatewayUiModel
import net.techandgraphics.wastemanagement.imageGatewayUrl
import net.techandgraphics.wastemanagement.imageScreenshotUrl
import net.techandgraphics.wastemanagement.toFullName
import net.techandgraphics.wastemanagement.toZonedDateTime
import net.techandgraphics.wastemanagement.toast
import net.techandgraphics.wastemanagement.ui.screen.imageGatewayPainter
import net.techandgraphics.wastemanagement.ui.screen.imageLoader
import net.techandgraphics.wastemanagement.ui.screen.paymentWithAccountAndMethodWithGateway4Preview
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme

@Composable fun CompanyVerifyPaymentView(
  channel: Flow<CompanyVerifyPaymentChannel>,
  entity: PaymentWithAccountAndMethodWithGatewayUiModel,
  imageLoader: ImageLoader?,
  onEvent: (CompanyVerifyPaymentEvent) -> Unit,
) {

  var showOptions by remember { mutableStateOf(false) }
  var showDialog by remember { mutableStateOf(false) }
  var paymentMethod by remember { mutableStateOf<PaymentMethodUiModel?>(null) }

  val context = LocalContext.current

  val payment = entity.payment
  val account = entity.account


  val lifecycleOwner = LocalLifecycleOwner.current
  LaunchedEffect(key1 = channel) {
    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
      channel.collect { event ->
        showDialog = false
        when (event) {
          is CompanyVerifyPaymentChannel.Payment.Failure -> context.toast(event.error.message)
          is CompanyVerifyPaymentChannel.Payment.Success -> Unit
        }
      }
    }
  }


  if (showDialog) {
    Dialog(onDismissRequest = { showDialog = false }) {
      CompanyVerifyPaymentDialog(
        entity = entity,
        onEvent = onEvent
      ) { showDialog = false }
    }
  }

  Card(
    modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp),
    shape = CircleShape,
    colors = CardDefaults.elevatedCardColors(),
    onClick = { showDialog = true }
  ) {
    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {

      Box(modifier = Modifier.size(48.dp)) {

        val gatewayImagePainter =
          imageGatewayPainter(imageGatewayUrl(payment.id), imageLoader!!)
//          imageGatewayPainter(imageGatewayUrl(payment.paymentGatewayId), imageLoader!!)

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
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Text(
            text = payment.createdAt.toZonedDateTime().defaultDateTime(),
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.MiddleEllipsis
          )

        }

        when (payment.status) {
          PaymentStatus.Verifying -> R.drawable.ic_help
          PaymentStatus.Approved -> R.drawable.ic_check_circle
          else -> R.drawable.ic_close
        }.also {
          Icon(
            painterResource(it),
            contentDescription = null,
            modifier = Modifier.padding(horizontal = 16.dp),
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
@Composable fun CompanyVerifyPaymentViewPreview() {
  WasteManagementTheme {
    CompanyVerifyPaymentView(
      channel = flow {},
      entity = paymentWithAccountAndMethodWithGateway4Preview,
      imageLoader = imageLoader(LocalContext.current),
      onEvent = {}
    )
  }
}
