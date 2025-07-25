package net.techandgraphics.wastical.ui.screen.company.payment.pay

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import net.techandgraphics.wastical.domain.model.account.AccountUiModel
import net.techandgraphics.wastical.domain.model.relations.CompanyLocationWithDemographicUiModel
import net.techandgraphics.wastical.toFullName
import net.techandgraphics.wastical.toLocation
import net.techandgraphics.wastical.ui.screen.account4Preview
import net.techandgraphics.wastical.ui.screen.client.home.LetterView
import net.techandgraphics.wastical.ui.screen.companyLocationWithDemographic4Preview
import net.techandgraphics.wastical.ui.theme.WasticalTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyMakePaymentClientView(
  demographic: CompanyLocationWithDemographicUiModel,
  account: AccountUiModel,
  onEvent: (CompanyMakePaymentEvent) -> Unit,
) {

  Column {
    Text(
      text = "For Account",
      modifier = Modifier.padding(8.dp)
    )
    Card(
      colors = CardDefaults.elevatedCardColors(),
      onClick = { onEvent(CompanyMakePaymentEvent.GoTo.BackHandler) }) {

      Row(
        modifier = Modifier
          .padding(16.dp)
          .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        LetterView(account)
        Column(
          modifier = Modifier
            .padding(horizontal = 8.dp)
            .weight(1f)
        ) {
          Text(
            text = account.toFullName(),
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.MiddleEllipsis,
            color = MaterialTheme.colorScheme.primary
          )
          Text(
            text = demographic.toLocation(),
            maxLines = 1,
            overflow = TextOverflow.MiddleEllipsis,
            style = MaterialTheme.typography.bodyMedium,
          )
        }

        if (account.username.isDigitsOnly())
          IconButton(
            enabled = account.username.isDigitsOnly(),
            onClick = {}) {
            Icon(
              Icons.Default.Phone,
              contentDescription = null
            )
          }
      }
    }

  }

}


@Preview(showBackground = true)
@Composable
private fun CompanyMakePaymentClientViewPreview() {
  WasticalTheme {
    CompanyMakePaymentClientView(
      account = account4Preview,
      demographic = companyLocationWithDemographic4Preview,
      onEvent = {}
    )
  }
}
