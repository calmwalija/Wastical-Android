package net.techandgraphics.wastical.ui.screen.company.client.create

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.domain.model.account.AccountInfoUiModel
import net.techandgraphics.wastical.toFullName
import net.techandgraphics.wastical.ui.screen.accountWithStreetAndArea4Preview
import net.techandgraphics.wastical.ui.theme.WasticalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyCreateClientConflictItem(
  account: AccountInfoUiModel,
  modifier: Modifier = Modifier,
) {
  OutlinedCard(
    modifier = modifier
      .padding(16.dp)
      .fillMaxWidth(),
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(15.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
  }

}


@Preview(showBackground = true)
@Composable
private fun CompanyCreateClientConflictPreview() {
  WasticalTheme {
    CompanyCreateClientConflictItem(
      account = accountWithStreetAndArea4Preview,
    )
  }
}
