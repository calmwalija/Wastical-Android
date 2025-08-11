package net.techandgraphics.wastical.ui.screen.company.report

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
  @DrawableRes val drawableRes: Int = R.drawable.ic_balance,
)


@Composable fun CompanyReportItemView(
  showIndicator: Boolean,
  item: CompanyReportItem,
  onEvent: (CompanyReportEvent) -> Unit,
) {
  Column {

    Column(
      modifier = Modifier
        .clickable(enabled = showIndicator.not()) { onEvent(item.event) }
        .padding(16.dp),
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          painterResource(item.drawableRes),
          contentDescription = null,
          modifier = Modifier
            .size(32.dp)
            .padding(2.dp),
          tint = MaterialTheme.colorScheme.secondary
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
          if (showIndicator) CircularProgressIndicator(modifier = Modifier.size(24.dp))
          else Icon(
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
}

@Preview @Composable fun CompanyReportItemPreview() {
  WasticalTheme {
    CompanyReportItemView(
      showIndicator = false, item = CompanyReportItem(
        "Lorem Ipusum", CompanyReportEvent.Load
      ), onEvent = { })
  }
}
