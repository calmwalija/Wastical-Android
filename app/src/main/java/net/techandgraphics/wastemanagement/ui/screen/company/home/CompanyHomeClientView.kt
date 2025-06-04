package net.techandgraphics.wastemanagement.ui.screen.company.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastemanagement.R
import net.techandgraphics.wastemanagement.format
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyHomeClientView(
  state: CompanyHomeState,
  onEvent: (CompanyHomeEvent) -> Unit,
) {


  OutlinedCard {
    Row(
      modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {

      Icon(
        painterResource(R.drawable.ic_account),
        contentDescription = null,
        modifier = Modifier
          .size(62.dp)
          .padding(8.dp),
        tint = MaterialTheme.colorScheme.secondary
      )

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = "Number Of Clients",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.secondary,
          modifier = Modifier.padding(end = 16.dp),
          maxLines = 1,
          overflow = TextOverflow.MiddleEllipsis
        )
        Text(
          text = format(state.state.accounts.size),
          style = MaterialTheme.typography.titleLarge,
        )
      }


      IconButton(onClick = { onEvent(CompanyHomeEvent.Goto.Create) }) {
        Icon(
          Icons.Rounded.Add, null,
          modifier = Modifier.size(32.dp),
          tint = MaterialTheme.colorScheme.primary
        )
      }

    }
  }


}


@Preview(showBackground = true)
@Composable
private fun CompanyHomeClientViewPreview() {
  WasteManagementTheme {
    CompanyHomeClientView(
      state = CompanyHomeState(),
      onEvent = {}
    )
  }
}
