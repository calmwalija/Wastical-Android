package net.techandgraphics.wastical.ui.screen.company.report

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.R
import net.techandgraphics.wastical.ui.theme.WasticalTheme


data class CompanyReportItem(
  val label: String,
  val event: CompanyReportEvent,
)


@Composable fun CompanyReportItemView(
  showIndicator: Boolean,
  item: CompanyReportItem,
  onEvent: (CompanyReportEvent) -> Unit,
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 8.dp, vertical = 4.dp),
    shape = CircleShape,
    colors = CardDefaults.elevatedCardColors(),
    enabled = showIndicator.not(),
    onClick = { onEvent(item.event) }) {
    Row(
      modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {

      Image(
        painterResource(R.drawable.ic_invoice),
        contentDescription = null,
        modifier = Modifier
          .size(24.dp)
          .padding(2.dp)
      )

      Column(
        modifier = Modifier
          .weight(1f)
          .padding(horizontal = 8.dp)
      ) {
        Text(
          text = item.label, style = MaterialTheme.typography.bodyMedium
        )
      }

      Box(contentAlignment = Alignment.Center) {
        if (showIndicator)
          CircularProgressIndicator(modifier = Modifier.size(24.dp))
        else
          Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
          )
      }

      Spacer(modifier = Modifier.width(8.dp))

    }
  }
}

@Preview
@Composable fun CompanyReportItemPreview() {
  WasticalTheme {
//    CompanyReportItemView(
//
//    )
  }
}
