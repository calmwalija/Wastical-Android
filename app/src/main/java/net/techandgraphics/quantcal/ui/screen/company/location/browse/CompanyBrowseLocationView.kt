package net.techandgraphics.quantcal.ui.screen.company.location.browse

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.quantcal.R
import net.techandgraphics.quantcal.data.local.database.dashboard.street.Payment4CurrentLocationMonth
import net.techandgraphics.quantcal.ui.theme.QuantcalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyBrowseLocationView(
  location: Payment4CurrentLocationMonth,
  onEvent: (CompanyBrowseLocationEvent) -> Unit,
) {


  Row(
    modifier = Modifier
      .clickable { onEvent(CompanyBrowseLocationEvent.Goto.LocationOverview(location.streetId)) }
      .padding(16.dp)
      .fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically
  ) {

    Icon(
      painterResource(R.drawable.ic_location_searching),
      contentDescription = null,
      modifier = Modifier
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.primary.copy(.1f))
        .size(48.dp)
        .padding(12.dp),
    )

    Column(
      modifier = Modifier
        .padding(start = 8.dp)
        .weight(1f)
    ) {
      Text(
        text = location.districtName,
        style = MaterialTheme.typography.labelSmall
      )
      Text(
        text = location.streetName,
        color = MaterialTheme.colorScheme.secondary,
        style = MaterialTheme.typography.titleMedium,
        maxLines = 1,
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

  }


}


@Preview(showBackground = true)
@Composable
private fun CompanyBrowseLocationPreview() {
  QuantcalTheme {
    Box(modifier = Modifier.padding(16.dp)) {
      CompanyBrowseLocationView(
        location = companyBrowseLocationStateSuccess().payment4CurrentLocationMonth.first(),
        onEvent = {},
      )
    }
  }
}
