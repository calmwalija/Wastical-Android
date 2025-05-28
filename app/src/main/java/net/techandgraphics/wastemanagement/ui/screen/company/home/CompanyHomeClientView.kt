package net.techandgraphics.wastemanagement.ui.screen.company.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastemanagement.R
import net.techandgraphics.wastemanagement.ui.screen.account4Preview
import net.techandgraphics.wastemanagement.ui.screen.company4Preview
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyHomeClientView(
  state: CompanyHomeState,
  onEvent: (CompanyHomeEvent) -> Unit
) {


  val account = account4Preview
  val company = company4Preview



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
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.secondary,
          modifier = Modifier.padding(end = 16.dp),
          maxLines = 1,
          overflow = TextOverflow.MiddleEllipsis
        )
        Text(
          text = "4,020",
          style = MaterialTheme.typography.headlineSmall,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary
        )
      }


      FilledIconButton(
        onClick = {},
        colors = IconButtonDefaults.iconButtonColors(
          containerColor = MaterialTheme.colorScheme.onSecondary
        )
      ) {
        Icon(Icons.Default.Add, null)
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
