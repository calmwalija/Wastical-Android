package net.techandgraphics.wastical.ui.screen.company.client.create

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import net.techandgraphics.wastical.R
import net.techandgraphics.wastical.domain.model.relations.CompanyLocationWithDemographicUiModel
import net.techandgraphics.wastical.ui.screen.companyLocationWithDemographic4Preview
import net.techandgraphics.wastical.ui.theme.Muted
import net.techandgraphics.wastical.ui.theme.WasticalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyCreateLocationView(
  location: CompanyLocationWithDemographicUiModel,
) {
  Row(
    modifier = Modifier
      .padding(vertical = 16.dp)
      .padding(bottom = 8.dp)
      .fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      painter = painterResource(R.drawable.ic_house),
      contentDescription = null,
      modifier = Modifier
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.primary.copy(.5f))
        .size(68.dp)
        .padding(20.dp),
    )
    Column(modifier = Modifier.padding(start = 8.dp)) {
      Text(
        text = location.demographicStreet.name,
        style = MaterialTheme.typography.titleLarge
      )
      Text(
        text = location.demographicArea.name,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        color = Muted
      )
    }
  }
}


@Preview(showBackground = true)
@Composable
private fun CompanyBrowseLocationPreview() {
  WasticalTheme {
    Box(modifier = Modifier.padding(16.dp)) {
      CompanyCreateLocationView(
        location = companyLocationWithDemographic4Preview,
      )
    }
  }
}
