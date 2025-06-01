package net.techandgraphics.wastemanagement.ui.screen.company.client.manage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import net.techandgraphics.wastemanagement.domain.model.account.AccountUiModel
import net.techandgraphics.wastemanagement.toFullName
import net.techandgraphics.wastemanagement.ui.screen.account4Preview
import net.techandgraphics.wastemanagement.ui.screen.client.home.LetterView
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyManageClientView(
  account: AccountUiModel,
  onEvent: (CompanyManageClientEvent) -> Unit,
) {

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
      )
      Text(
        text = account.email!!,
        maxLines = 1,
        overflow = TextOverflow.MiddleEllipsis,
        style = MaterialTheme.typography.bodyMedium,
      )
    }

    IconButton(onClick = { }) {
      Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null)
    }
  }


}


@Preview(showBackground = true)
@Composable
private fun CompanyManageClientViewPreview() {
  WasteManagementTheme {
    CompanyManageClientView(
      account = account4Preview,
      onEvent = {}
    )
  }
}
