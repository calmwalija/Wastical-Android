package net.techandgraphics.wastemanagement.ui.screen.company.info

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastemanagement.R
import net.techandgraphics.wastemanagement.toPhoneFormat
import net.techandgraphics.wastemanagement.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastemanagement.ui.screen.company4Preview
import net.techandgraphics.wastemanagement.ui.screen.companyContact4Preview
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyInfoScreen(
  state: CompanyInfoState,
  onEvent: (CompanyInfoEvent) -> Unit,
) {

  when (state) {
    CompanyInfoState.Loading -> LoadingIndicatorView()
    is CompanyInfoState.Success ->
      Scaffold(
        topBar = {
          TopAppBar(
            title = {},
            navigationIcon = {
              IconButton(onClick = { onEvent(CompanyInfoEvent.Button.BackHandler) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
              }
            },
            modifier = Modifier.shadow(0.dp),
            colors = TopAppBarDefaults.topAppBarColors()
          )
        },
      ) {

        Column(
          modifier = Modifier
            .padding(top = 16.dp)
            .padding(horizontal = 16.dp)
            .padding(it)
        ) {
          Text(
            text = "Company Info",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 8.dp, horizontal = 8.dp),
          )
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {

            Image(
              painterResource(R.drawable.im_placeholder),
              contentDescription = null,
              modifier = Modifier
                .padding(vertical = 24.dp)
                .clip(CircleShape)
                .size(180.dp)
            )
            Text(
              text = state.company.name,
              style = MaterialTheme.typography.headlineSmall,
              maxLines = 1,
              overflow = TextOverflow.MiddleEllipsis,
              color = MaterialTheme.colorScheme.primary
            )
            Text(
              text = state.company.email,
              maxLines = 1,
              overflow = TextOverflow.MiddleEllipsis,
              style = MaterialTheme.typography.bodyMedium,
            )
            state.contacts.forEach { contact ->
              Text(
                text = contact.contact.toPhoneFormat(),
                maxLines = 1,
                overflow = TextOverflow.MiddleEllipsis,
                style = MaterialTheme.typography.bodyMedium,
              )
            }

            Card(modifier = Modifier.padding(vertical = 24.dp)) {
              LazyColumn {
                items(companyInfoItems) { item ->
                  Column(
                    modifier = Modifier
                      .fillMaxWidth()
                      .clickable { onEvent(item.event) }
                  ) {
                    Row(modifier = Modifier.padding(24.dp)) {
                      Icon(
                        painterResource(item.drawableRes),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                      )
                      Text(
                        text = item.title,
                        modifier = Modifier
                          .padding(start = 16.dp)
                          .weight(1f),
                      )
                      Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null
                      )
                    }
                  }
                }
              }
            }
          }
        }
      }
  }

}


@Preview
@Composable
private fun CompanyInfoScreenPreview() {
  WasteManagementTheme {
    CompanyInfoScreen(
      state = CompanyInfoState.Success(
        company = company4Preview,
        contacts = listOf(companyContact4Preview)
      ),
      onEvent = {}
    )
  }
}
