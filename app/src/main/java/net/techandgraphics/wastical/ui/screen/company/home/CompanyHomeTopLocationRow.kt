package net.techandgraphics.wastical.ui.screen.company.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.R

@Composable
fun TopLocationRow(
  title: String,
  subtitle: String,
  progress: Float,
  trailing: String,
) {
  Card(
    colors = CardDefaults.elevatedCardColors(),
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 8.dp, vertical = 4.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(painter = painterResource(R.drawable.ic_house), contentDescription = null, modifier = Modifier.size(24.dp))
      Column(modifier = Modifier
        .weight(1f)
        .padding(horizontal = 12.dp)) {
        Text(text = title, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(text = subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
        LinearProgressIndicator(progress = { progress.coerceIn(0f, 1f) })
      }
      Text(text = trailing, style = MaterialTheme.typography.labelLarge)
    }
  }
}
