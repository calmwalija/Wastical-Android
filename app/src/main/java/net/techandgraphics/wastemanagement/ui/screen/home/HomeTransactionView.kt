package net.techandgraphics.wastemanagement.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import net.techandgraphics.wastemanagement.AppUrl
import net.techandgraphics.wastemanagement.R
import net.techandgraphics.wastemanagement.calculateAmount
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentStatus
import net.techandgraphics.wastemanagement.defaultDate
import net.techandgraphics.wastemanagement.toZonedDateTime
import net.techandgraphics.wastemanagement.ui.screen.payment.imageLoader
import net.techandgraphics.wastemanagement.ui.screen.payment.paymentPlan
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun HomeTransactionView(
  state: HomeState,
  onEvent: (HomeEvent) -> Unit
) {

  val plan = state.paymentPlans.firstOrNull() ?: return

  OutlinedCard(modifier = Modifier.fillMaxWidth()) {
    state.payments.forEach { payment ->
      val drawableResId =
        if (payment.status == PaymentStatus.Approved) R.drawable.ic_invoice else {
          R.drawable.ic_compare_arrows
        }

      Row(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          painterResource(drawableResId), null,
          modifier = Modifier
            .size(32.dp)
        )
        Column(
          modifier = Modifier
            .weight(1f)
            .padding(horizontal = 8.dp)
        ) {
          Text(
            text = payment.createdAt.toZonedDateTime().defaultDate(),
            style = MaterialTheme.typography.bodySmall
          )
          Row(verticalAlignment = Alignment.CenterVertically) {

            val imageUrl = AppUrl.FILE_URL.plus("gateway/").plus(payment.paymentMethodId)
            val asyncImagePainter = rememberAsyncImagePainter(
              model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .diskCacheKey(imageUrl)
                .networkCachePolicy(CachePolicy.ENABLED)
                .crossfade(true)
                .build(),
              imageLoader = state.imageLoader!!,
              placeholder = painterResource(R.drawable.im_placeholder),
              error = painterResource(R.drawable.im_placeholder)
            )

            Image(
              asyncImagePainter, null,
              modifier = Modifier
                .padding(end = 4.dp)
                .clip(CircleShape)
                .size(16.dp)
                .alpha(.9f)
            )
            Text(text = calculateAmount(plan, payment))
          }

          Text(
            text = payment.transactionId,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.MiddleEllipsis,
            modifier = Modifier.padding(end = 8.dp)
          )
        }

        if (payment.status == PaymentStatus.Approved)
          IconButton(onClick = { onEvent(HomeEvent.Invoice(payment)) }) {
            Icon(
              painterResource(R.drawable.ic_file_open), null,
              modifier = Modifier.size(20.dp),
            )
          } else {
          // TODO(Add appropriate icons)
          val drawableResId = when (payment.status) {
            PaymentStatus.Retry -> R.drawable.ic_file_open
            PaymentStatus.Pending -> R.drawable.ic_file_open
            PaymentStatus.Processing -> R.drawable.ic_file_open
            PaymentStatus.Cancelled -> R.drawable.ic_file_open
            else -> R.drawable.ic_file_open
          }

        }

        Spacer(modifier = Modifier.width(4.dp))
      }
      HorizontalDivider()
    }
  }


}

@Preview(showBackground = true)
@Composable
private fun HomeTransactionViewPreview() {
  WasteManagementTheme {
    Box(modifier = Modifier.padding(16.dp)) {
      HomeTransactionView(
        state = HomeState(
          payments = listOf(payment, payment),
          imageLoader = imageLoader(LocalContext.current),
          paymentPlans = listOf(paymentPlan)
        ),
        onEvent = {}
      )
    }
  }
}
