package net.techandgraphics.wastical.ui.screen.company.location.overview

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material3.OutlinedCard
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
import net.techandgraphics.wastical.R
import net.techandgraphics.wastical.domain.model.account.AccountWithPaymentStatusUiModel
import net.techandgraphics.wastical.toAmount
import net.techandgraphics.wastical.toFullName
import net.techandgraphics.wastical.toInitials
import net.techandgraphics.wastical.ui.screen.accountWithPaymentStatus4Preview
import net.techandgraphics.wastical.ui.theme.Green
import net.techandgraphics.wastical.ui.theme.WasticalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyPaymentLocationClientItem(
  entity: AccountWithPaymentStatusUiModel,
  modifier: Modifier = Modifier,
  onEvent: (CompanyPaymentLocationOverviewEvent) -> Unit,
) {
  val account = entity.account
  Card(
    modifier = modifier.padding(vertical = 4.dp),
    shape = CircleShape,
    colors = CardDefaults.elevatedCardColors(),
    onClick = { onEvent(CompanyPaymentLocationOverviewEvent.Goto.Profile(account.id)) }
  ) {
    Row(
      modifier = modifier
        .padding(horizontal = 8.dp, vertical = 4.dp)
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
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }

      Text(
        text = entity.amount.toAmount(),
        style = MaterialTheme.typography.bodySmall,
        maxLines = 1,
        overflow = TextOverflow.MiddleEllipsis,
      )

      when {
        entity.hasPaid -> R.drawable.ic_check_circle
        else -> if (entity.offlinePay) R.drawable.ic_upload_ready else R.drawable.ic_close
      }.also {
        Icon(
          painterResource(it),
          contentDescription = null,
          modifier = Modifier.padding(horizontal = 16.dp),
          tint = if (entity.hasPaid || entity.offlinePay) Green else Color.Red,
        )
      }

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


@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CompanyPaymentLocationClientItemPreview() {
  WasticalTheme {
    CompanyPaymentLocationClientItem(
      entity = accountWithPaymentStatus4Preview,
      onEvent = {}
    )
  }
}
