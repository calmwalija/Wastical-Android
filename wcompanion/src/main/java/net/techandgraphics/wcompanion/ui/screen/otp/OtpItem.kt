package net.techandgraphics.wcompanion.ui.screen.otp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wcompanion.domain.model.AccountWithOtpUiModel
import net.techandgraphics.wcompanion.ui.theme.WasticalTheme

@Composable
fun OtpItem(
  modifier: Modifier = Modifier,
  accountWithOtp: AccountWithOtpUiModel,
  onEvent: (OtpEvent) -> Unit,
) {
  val account = accountWithOtp.account
  Row(
    modifier = modifier
      .padding(horizontal = 16.dp)
      .fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically
  ) {
    OtpItemLetterView(account.lastname)
    Column(
      modifier = Modifier
        .padding(horizontal = 8.dp)
        .weight(1f)
    ) {
      Text(
        text = "${account.firstname} ${account.lastname}",
        style = MaterialTheme.typography.titleMedium,
        maxLines = 1,
        overflow = TextOverflow.StartEllipsis,
        color = MaterialTheme.colorScheme.primary
      )
      Text(
        text = account.username,
        maxLines = 1,
        overflow = TextOverflow.MiddleEllipsis,
        style = MaterialTheme.typography.bodyMedium,
      )
    }

    Text(
      text = accountWithOtp.otp.otp.toString(),
      modifier = Modifier.padding(horizontal = 16.dp),
      style = MaterialTheme.typography.bodyLarge,
    )

    IconButton(onClick = { onEvent(OtpEvent.Resend(accountWithOtp.otp)) }) {
      Icon(
        imageVector = Icons.Rounded.CheckCircle,
        contentDescription = null,
        modifier = Modifier
          .size(24.dp)
          .alpha(if (accountWithOtp.otp.sent) 1f else 0f),
        tint = MaterialTheme.colorScheme.primary
      )
    }

    Spacer(modifier = Modifier.width(16.dp))

  }

}

@Composable private fun OtpItemLetterView(lastname: String) {

  Box(contentAlignment = Alignment.Center) {
    Box(
      modifier = Modifier
        .clip(CircleShape)
        .size(58.dp)
        .background(MaterialTheme.colorScheme.primary.copy(.2f))
    )
    Box(
      modifier = Modifier
        .clip(CircleShape)
        .size(64.dp)
        .background(MaterialTheme.colorScheme.primary.copy(.1f))
    )
    Text(
      text = lastname.first().toString(),
      modifier = Modifier.padding(4.dp),
      fontWeight = FontWeight.Bold,
      style = MaterialTheme.typography.bodyLarge
    )
  }

}


@Preview(showBackground = true)
@Composable
private fun OtpScreenPreview() {
  WasticalTheme {
    OtpItem(
      accountWithOtp = otpStateSuccess().accountWithOtps.first(),
      onEvent = {})
  }
}
