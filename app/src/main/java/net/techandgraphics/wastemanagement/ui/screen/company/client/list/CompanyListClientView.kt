package net.techandgraphics.wastemanagement.ui.screen.company.client.list

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastemanagement.defaultDateTime
import net.techandgraphics.wastemanagement.domain.model.account.AccountUiModel
import net.techandgraphics.wastemanagement.toFullName
import net.techandgraphics.wastemanagement.toInitials
import net.techandgraphics.wastemanagement.toZonedDateTime
import net.techandgraphics.wastemanagement.ui.screen.account4Preview
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyListClientView(
  account: AccountUiModel,
  onEvent: (CompanyListClientEvent) -> Unit,
) {

  Row(
    modifier = Modifier
      .clickable { onEvent(CompanyListClientEvent.Goto.Profile(account.id)) }
      .padding(16.dp)
      .fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically
  ) {
    CompanyListClientLetterView(account)
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
      )
      Text(
        text = account.username,
        maxLines = 1,
        overflow = TextOverflow.MiddleEllipsis,
        style = MaterialTheme.typography.bodyMedium,
      )
      Text(
        text = account.createdAt.toZonedDateTime().defaultDateTime(),
        maxLines = 1,
        overflow = TextOverflow.MiddleEllipsis,
        style = MaterialTheme.typography.bodyMedium,
      )
    }

    IconButton(onClick = { }) {
      Icon(Icons.Default.Phone, null)
    }

    Spacer(modifier = Modifier.width(8.dp))

  }


}

@Composable private fun CompanyListClientLetterView(account: AccountUiModel) {
  val brush = Brush.horizontalGradient(
    listOf(
      MaterialTheme.colorScheme.primary.copy(.7f),
      MaterialTheme.colorScheme.primary.copy(.8f),
      MaterialTheme.colorScheme.primary
    )
  )

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
    Box(
      modifier = Modifier
        .clip(CircleShape)
        .size(52.dp)
        .background(
          brush = brush
        )
    )
    Text(
      text = account.toInitials(),
      modifier = Modifier.padding(4.dp),
      fontWeight = FontWeight.Bold,
      style = MaterialTheme.typography.bodyLarge
    )
  }

}


@Preview(showBackground = true)
@Composable
private fun CompanyListClientViewPreview() {
  WasteManagementTheme {
    CompanyListClientView(
      account = account4Preview,
      onEvent = {}
    )
  }
}
