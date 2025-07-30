package net.techandgraphics.wastical.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.domain.model.account.AccountUiModel
import net.techandgraphics.wastical.toInitials


@Composable fun AccountAvatarView(
  modifier: Modifier = Modifier,
  style: TextStyle = MaterialTheme.typography.headlineSmall,
  account: AccountUiModel,
) {
  val brush = Brush.horizontalGradient(
    listOf(
      MaterialTheme.colorScheme.primary.copy(.7f),
      MaterialTheme.colorScheme.primary.copy(.8f),
      MaterialTheme.colorScheme.primary
    )
  )
  Box(
    contentAlignment = Alignment.Center,
    modifier = Modifier
      .clip(CircleShape)
  ) {
    Box(
      modifier = modifier
        .clip(CircleShape)
        .size(160.dp)
        .background(brush = brush)
    )
    Text(
      text = account.toInitials(),
      style = style,
      modifier = Modifier.padding(4.dp),
      fontWeight = FontWeight.Bold
    )
  }
}
