package net.techandgraphics.wastical.ui.screen.company.report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.R


data class CompanyReportKpiItem(
  val iconRes: Int,
  val title: String,
  val value: String,
  val caption: String? = null,
  val accentColor: Color,
)


@Composable
fun CompanyReportKpiItemView(
  item: CompanyReportKpiItem,
  modifier: Modifier = Modifier,
) {
  OutlinedCard(modifier = modifier) {
    Row(
      modifier = Modifier
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Box(
        modifier = Modifier
          .size(40.dp)
          .clip(MaterialTheme.shapes.small)
          .background(item.accentColor.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          painter = painterResource(id = item.iconRes),
          contentDescription = item.title,
          modifier = Modifier.size(22.dp)
        )
      }
      Spacer(modifier = Modifier.size(12.dp))
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = item.title,
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Text(
          text = item.value,
          style = MaterialTheme.typography.titleMedium
        )
      }
    }
  }
}

@Preview
@Composable
fun CompanyReportKpiItemPreview() {
  CompanyReportKpiItemView(
    item = item
  )
}


val item = CompanyReportKpiItem(
  iconRes = R.drawable.ic_list_active,
  title = "Active Client",
  value = 1203.toString(),
  accentColor = Color.DarkGray
)
