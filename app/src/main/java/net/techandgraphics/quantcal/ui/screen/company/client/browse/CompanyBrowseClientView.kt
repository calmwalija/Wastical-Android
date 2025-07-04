package net.techandgraphics.quantcal.ui.screen.company.client.browse

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.quantcal.domain.model.account.AccountInfoUiModel
import net.techandgraphics.quantcal.toFullName
import net.techandgraphics.quantcal.toInitials
import net.techandgraphics.quantcal.ui.screen.accountWithStreetAndArea4Preview
import net.techandgraphics.quantcal.ui.theme.QuantcalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyBrowseClientView(
  account: AccountInfoUiModel,
  modifier: Modifier = Modifier,
  onEvent: (CompanyBrowseClientListEvent) -> Unit,
) {
  Row(
    modifier = modifier
      .clickable { onEvent(CompanyBrowseClientListEvent.Goto.Profile(account.accountId)) }
      .padding(16.dp)
      .fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically
  ) {
    CompanyListClientLetterView(account.lastname)
    Column(
      modifier = Modifier
        .padding(horizontal = 8.dp)
        .weight(1f)
    ) {
      Text(
        text = account.username,
        maxLines = 1,
        overflow = TextOverflow.MiddleEllipsis,
        style = MaterialTheme.typography.bodyMedium,
      )
      Text(
        text = toFullName(account.title, account.firstname, account.lastname),
        style = MaterialTheme.typography.titleMedium,
        maxLines = 1,
        overflow = TextOverflow.StartEllipsis,
        color = MaterialTheme.colorScheme.primary
      )
      Text(
        text = "${account.areaName}, ${account.streetName}",
        maxLines = 1,
        overflow = TextOverflow.MiddleEllipsis,
        style = MaterialTheme.typography.bodyMedium,
      )
    }


    Spacer(modifier = Modifier.width(8.dp))

  }


}

@Composable private fun CompanyListClientLetterView(lastname: String) {

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
      text = lastname.toInitials(),
      modifier = Modifier.padding(4.dp),
      fontWeight = FontWeight.Bold,
      style = MaterialTheme.typography.bodyLarge
    )
  }

}


@Preview(showBackground = true)
@Composable
private fun CompanyBrowseClientViewPreview() {
  QuantcalTheme {
    CompanyBrowseClientView(
      account = accountWithStreetAndArea4Preview,
      onEvent = {}
    )
  }
}
