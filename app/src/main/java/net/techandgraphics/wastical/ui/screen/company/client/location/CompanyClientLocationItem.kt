package net.techandgraphics.wastical.ui.screen.company.client.location

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.R
import net.techandgraphics.wastical.domain.model.payment.CompanyLocationUiModel
import net.techandgraphics.wastical.domain.model.relations.CompanyLocationWithDemographicUiModel
import net.techandgraphics.wastical.ui.screen.companyLocation4Preview
import net.techandgraphics.wastical.ui.screen.companyLocationWithDemographic4Preview
import net.techandgraphics.wastical.ui.theme.WasticalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyClientLocationItem(
  modifier: Modifier = Modifier,
  location: CompanyLocationUiModel,
  model: CompanyLocationWithDemographicUiModel,
  onEvent: (CompanyClientLocationEvent) -> Unit,
) {

  val currentLocation = model.demographicStreet.id == location.demographicStreetId

  Card(
    onClick = { onEvent(CompanyClientLocationEvent.Button.Change(model.demographicStreet)) },
    modifier = modifier.padding(4.dp),
    shape = CircleShape,
    colors = CardDefaults.elevatedCardColors()
  ) {
    Row(
      modifier = Modifier
        .padding(horizontal = 16.dp, vertical = 8.dp)
        .fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {

      RadioButton(
        selected = currentLocation,
        onClick = { onEvent(CompanyClientLocationEvent.Button.Change(model.demographicStreet)) }
      )

      Column(
        modifier = Modifier
          .padding(start = 8.dp)
          .weight(1f)
      ) {
        Text(
          text = model.demographicStreet.name,
          color = MaterialTheme.colorScheme.secondary,
          style = MaterialTheme.typography.titleMedium,
          maxLines = 1,
          overflow = TextOverflow.MiddleEllipsis
        )
        Text(
          text = model.demographicArea.name,
          style = MaterialTheme.typography.labelLarge
        )
      }

      if (currentLocation)
        Icon(
          painterResource(R.drawable.ic_check_circle),
          tint = MaterialTheme.colorScheme.primary,
          contentDescription = null
        )


      Spacer(modifier = Modifier.width(16.dp))

    }
  }

}


@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CompanyClientLocationItemPreview() {
  WasticalTheme {
    CompanyClientLocationItem(
      model = companyLocationWithDemographic4Preview,
      location = companyLocation4Preview,
      onEvent = {}
    )
  }
}
