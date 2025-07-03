package net.techandgraphics.quantcal.ui.screen.company.location.overview

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.quantcal.R
import net.techandgraphics.quantcal.domain.model.account.AccountWithPaymentStatusUiModel
import net.techandgraphics.quantcal.toAmount
import net.techandgraphics.quantcal.toFullName
import net.techandgraphics.quantcal.toInitials
import net.techandgraphics.quantcal.ui.screen.accountWithPaymentStatus4Preview
import net.techandgraphics.quantcal.ui.theme.Green
import net.techandgraphics.quantcal.ui.theme.QuantcalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyPaymentLocationClientView(
  entity: AccountWithPaymentStatusUiModel,
  modifier: Modifier = Modifier,
  onEvent: (CompanyPaymentLocationOverviewEvent) -> Unit,
) {
  val account = entity.account
  Card(
    modifier = modifier.padding(vertical = 4.dp),
    shape = CircleShape,
    colors = CardDefaults.elevatedCardColors(),
  ) {
    Row(
      modifier = modifier
        .clickable { onEvent(CompanyPaymentLocationOverviewEvent.Goto.Profile(account.id)) }
        .padding(8.dp)
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
          style = MaterialTheme.typography.bodyMedium,
          maxLines = 1,
          overflow = TextOverflow.MiddleEllipsis,
        )
        Text(
          text = account.username,
          style = MaterialTheme.typography.labelSmall,
        )
      }

      when {
        entity.hasPaid -> R.drawable.ic_check_circle
        else -> R.drawable.ic_close
      }.also {
        Icon(
          painterResource(it),
          contentDescription = null,
          modifier = Modifier.padding(horizontal = 16.dp),
          tint = if (entity.hasPaid) Green else Color.Red,
        )
      }

      Text(
        text = entity.amount.toAmount(),
        style = MaterialTheme.typography.bodySmall,
        maxLines = 1,
        overflow = TextOverflow.MiddleEllipsis,
        modifier = Modifier.padding(end = 16.dp)
      )

    }
  }
}

@Composable private fun CompanyListClientLetterView(lastname: String) {

  Box(contentAlignment = Alignment.Center) {
    Box(
      modifier = Modifier
        .clip(CircleShape)
        .size(38.dp)
        .background(MaterialTheme.colorScheme.primary.copy(.2f))
    )
    Box(
      modifier = Modifier
        .clip(CircleShape)
        .size(44.dp)
        .background(MaterialTheme.colorScheme.primary.copy(.1f))
    )
    Text(
      text = lastname.toInitials(),
      modifier = Modifier.padding(4.dp),
      fontWeight = FontWeight.Bold,
      style = MaterialTheme.typography.bodySmall
    )
  }

}


@Preview(showBackground = true)
@Composable
private fun CompanyPaymentLocationClientPreview() {
  QuantcalTheme {
    CompanyPaymentLocationClientView(
      entity = accountWithPaymentStatus4Preview,
      onEvent = {}
    )
  }
}
