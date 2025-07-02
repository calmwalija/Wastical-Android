package net.techandgraphics.wastemanagement.ui.screen.company.location.overview

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
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
import net.techandgraphics.wastemanagement.domain.model.account.AccountWithPaymentStatusUiModel
import net.techandgraphics.wastemanagement.toFullName
import net.techandgraphics.wastemanagement.toInitials
import net.techandgraphics.wastemanagement.ui.screen.accountWithPaymentStatus4Preview
import net.techandgraphics.wastemanagement.ui.theme.Green
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyPaymentLocationClientView(
  entity: AccountWithPaymentStatusUiModel,
  modifier: Modifier = Modifier,
  onEvent: (CompanyPaymentLocationOverviewEvent) -> Unit,
) {

  val account = entity.account

  Row(
    modifier = modifier
      .clickable { onEvent(CompanyPaymentLocationOverviewEvent.Goto.Profile(account.id)) }
      .padding(vertical = 16.dp, horizontal = 8.dp)
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
        text = toFullName(account.title.name, account.firstname, account.lastname),
        style = MaterialTheme.typography.titleMedium,
        maxLines = 1,
        overflow = TextOverflow.MiddleEllipsis,
      )
      Text(
        text = account.username,
        maxLines = 1,
        overflow = TextOverflow.MiddleEllipsis,
      )
    }

    Badge(
      modifier = Modifier.padding(horizontal = 8.dp),
      containerColor = if (entity.hasPaid) Green else MaterialTheme.colorScheme.error,
    ) {
      Text(
        text = if (entity.hasPaid) "Paid" else "Not Paid",
        modifier = Modifier.padding(4.dp)
      )
    }

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
private fun CompanyPaymentLocationClientPreview() {
  WasteManagementTheme {
    CompanyPaymentLocationClientView(
      entity = accountWithPaymentStatus4Preview,
      onEvent = {}
    )
  }
}
