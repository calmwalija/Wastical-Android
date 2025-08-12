package net.techandgraphics.wastical.ui.screen.company.client

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.domain.model.account.AccountInfoUiModel
import net.techandgraphics.wastical.ui.screen.accountWithStreetAndArea4Preview
import net.techandgraphics.wastical.ui.screen.company.client.create.CompanyCreateClientConflictItem
import net.techandgraphics.wastical.ui.theme.WasticalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyClientConflictDialog(
  accounts: List<AccountInfoUiModel>,
  onEvent: () -> Unit,
) {
  Column(
    modifier = Modifier
      .padding(16.dp)
      .fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Icon(
      imageVector = Icons.Rounded.Close,
      contentDescription = null,
      modifier = Modifier
        .padding(bottom = 8.dp)
        .clip(CircleShape)
        .background(Color.Red)
        .size(72.dp)
        .padding(4.dp),
      tint = Color.White
    )
    Text(
      text = "Conflict",
      style = MaterialTheme.typography.headlineSmall,
    )

    Text(
      text = "Contact number belongs to client below",
      textAlign = TextAlign.Center,
      style = MaterialTheme.typography.labelLarge,
      modifier = Modifier
        .padding(vertical = 2.dp)
        .fillMaxWidth()
    )

    LazyColumn {
      items(accounts) { account ->
        CompanyCreateClientConflictItem(
          account = account,
          modifier = Modifier.animateItem()
        )
      }
    }

    Button(
      modifier = Modifier.fillMaxWidth(.5f),
      onClick = onEvent
    ) { Text(text = "Close") }
  }
}


@Preview(showBackground = true)
@Composable
private fun CompanyClientConflictDialogPreview() {
  WasticalTheme {
    CompanyClientConflictDialog(
      accounts = (1..3).map { accountWithStreetAndArea4Preview },
      onEvent = {}
    )
  }
}
