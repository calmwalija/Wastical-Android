package net.techandgraphics.wastical.ui.screen.client.notification

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.defaultDateTime
import net.techandgraphics.wastical.domain.model.NotificationUiModel
import net.techandgraphics.wastical.toZonedDateTime
import net.techandgraphics.wastical.ui.theme.Muted
import net.techandgraphics.wastical.ui.theme.WasticalTheme


@Composable
fun ClientNotificationItem(
  modifier: Modifier = Modifier,
  notification: NotificationUiModel,
) {

  var contentHeight by remember { mutableIntStateOf(0) }

  Row(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(16.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .height(with(LocalDensity.current) { contentHeight.toDp() })
        .width(38.dp),
      contentAlignment = Alignment.Center
    ) {

      Box(
        modifier = Modifier
          .width(2.dp)
          .fillMaxHeight()
          .background(Color.Gray)
      )

      Icon(
        imageVector = Icons.Outlined.Notifications,
        contentDescription = null,
        modifier = Modifier
          .size(38.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.primary)
          .border(2.dp, Color.White, CircleShape)
          .padding(8.dp),
        tint = Color.White
      )

    }

    Column(
      modifier = Modifier
        .clip(RoundedCornerShape(16.dp))
        .onGloballyPositioned { layoutCoordinates ->
          contentHeight = layoutCoordinates.size.height
        }
        .padding(vertical = 8.dp)
        .weight(1f),
      verticalArrangement = Arrangement.Center
    ) {
      Text(
        text = notification.type.description,
        color = MaterialTheme.colorScheme.primary,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
      Text(
        notification.body,
        style = MaterialTheme.typography.bodyMedium
      )
      Text(
        text = notification.bigText,
        style = MaterialTheme.typography.bodySmall,
        color = Muted
      )
      Text(
        text = notification.createdAt.toZonedDateTime().defaultDateTime(),
        style = MaterialTheme.typography.labelLarge,
      )
    }

  }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ClientNotificationItemPreview() {
  WasticalTheme {
    ClientNotificationItem(
      notification = notificationStateSuccess().notifications.first()
    )
  }
}
