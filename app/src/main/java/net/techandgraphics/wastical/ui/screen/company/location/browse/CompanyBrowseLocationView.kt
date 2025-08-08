package net.techandgraphics.wastical.ui.screen.company.location.browse

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.techandgraphics.wastical.data.local.database.dashboard.street.Payment4CurrentLocationMonth
import net.techandgraphics.wastical.toInitials
import net.techandgraphics.wastical.ui.theme.WasticalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyBrowseLocationView(
  modifier: Modifier = Modifier,
  location: Payment4CurrentLocationMonth,
  onEvent: (CompanyBrowseLocationEvent) -> Unit,
) {

  Row(
    modifier = modifier
      .clickable { onEvent(CompanyBrowseLocationEvent.Goto.LocationOverview(location.streetId)) }
      .padding(16.dp)
      .fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically
  ) {

    val displayText = location.totalAccounts.takeIf { it <= 100 }?.toString() ?: "99+"

    Box(contentAlignment = Alignment.BottomEnd) {
      CompanyLocationLetterView(location.streetName)
      Card(
        shape = CircleShape,
        modifier = Modifier
          .offset(x = (1).dp)
          .size(28.dp),
        colors = CardDefaults.cardColors(
          containerColor = Color.White
        ),
        elevation = CardDefaults.elevatedCardElevation(
          defaultElevation = 1.dp
        ),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(.5f))
      ) {
        Box(
          modifier = Modifier.fillMaxSize(),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = displayText,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            textAlign = TextAlign.Center,
            color = Color.DarkGray,
            maxLines = 1,
          )
        }
      }
    }

    Column(
      modifier = Modifier
        .padding(horizontal = 12.dp)
        .weight(1f)
    ) {
      Text(
        text = location.districtName,
        maxLines = 1,
        overflow = TextOverflow.MiddleEllipsis,
        style = MaterialTheme.typography.labelMedium,
      )
      Text(
        maxLines = 1,
        text = location.streetName,
        color = MaterialTheme.colorScheme.primary,
        overflow = TextOverflow.MiddleEllipsis
      )
      Text(
        text = location.areaName,
        style = MaterialTheme.typography.labelLarge
      )
    }

    Icon(
      Icons.AutoMirrored.Filled.KeyboardArrowRight,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.primary
    )

    Spacer(modifier = Modifier.width(8.dp))

  }


}


@Composable private fun CompanyLocationLetterView(location: String) {

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
      text = location.toInitials(),
      modifier = Modifier.padding(4.dp),
      fontWeight = FontWeight.Bold,
      style = MaterialTheme.typography.bodyLarge
    )
  }

}

@Preview(showBackground = true)
@Composable
private fun CompanyBrowseLocationPreview() {
  WasticalTheme {
    CompanyBrowseLocationView(
      location = companyBrowseLocationStateSuccess().payment4CurrentLocationMonth.first(),
      onEvent = {},
    )
  }
}
