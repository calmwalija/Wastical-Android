package net.techandgraphics.wastical.ui.screen.company.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.defaultDateTime
import net.techandgraphics.wastical.domain.model.NotificationUiModel
import net.techandgraphics.wastical.notification.NotificationType
import net.techandgraphics.wastical.toZonedDateTime
import net.techandgraphics.wastical.ui.screen.notification4Preview
import net.techandgraphics.wastical.ui.theme.Green
import net.techandgraphics.wastical.ui.theme.WasticalTheme

@Composable
fun CompanyNotificationItem(
  modifier: Modifier = Modifier,
  notification: NotificationUiModel,
) {

  ElevatedCard(
    modifier = modifier
      .padding(vertical = 8.dp)
      .fillMaxWidth(),
    colors = CardDefaults.elevatedCardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
    )
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(IntrinsicSize.Min)
        .padding(12.dp),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .width(5.dp)
          .fillMaxHeight()
          .clip(RoundedCornerShape(12.dp))
          .background(colorForType(notification.type))
      )

      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(28.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(colorForType(notification.type).copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = iconForType(notification.type),
              contentDescription = null,
              tint = colorForType(notification.type),
            )
          }
          Text(
            text = notification.type.description,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 8.dp)
          )

        }

        Text(
          text = notification.body,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Text(
          text = notification.createdAt.toZonedDateTime().defaultDateTime(),
          style = MaterialTheme.typography.labelLarge,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

      }
    }
  }
}

@Composable
private fun colorForType(type: NotificationType): Color = when (type) {
  NotificationType.PROOF_OF_PAYMENT_APPROVED -> Green
  NotificationType.PROOF_OF_PAYMENT_SUCCESSFUL_BY_COMPANY -> Green
  NotificationType.PROOF_OF_PAYMENT_DECLINED -> MaterialTheme.colorScheme.error
  NotificationType.PROOF_OF_PAYMENT_SUBMITTED -> MaterialTheme.colorScheme.secondary
  NotificationType.PROOF_OF_PAYMENT_SUBMITTED_BY_COMPANY -> MaterialTheme.colorScheme.primary
  NotificationType.PROOF_OF_PAYMENT_COMPANY_VERIFY -> MaterialTheme.colorScheme.onSurfaceVariant
  else -> MaterialTheme.colorScheme.secondary
}

private fun iconForType(type: NotificationType) = when (type) {
  NotificationType.PROOF_OF_PAYMENT_APPROVED -> Icons.Outlined.CheckCircle
  NotificationType.PROOF_OF_PAYMENT_DECLINED -> Icons.Outlined.Close
  else -> Icons.Outlined.Notifications
}

@Preview(showBackground = true)
@Composable
private fun CompanyNotificationItemPreview() {
  WasticalTheme {
    Box(modifier = Modifier.padding(24.dp)) {
      CompanyNotificationItem(
        notification = notification4Preview
      )
    }
  }
}
