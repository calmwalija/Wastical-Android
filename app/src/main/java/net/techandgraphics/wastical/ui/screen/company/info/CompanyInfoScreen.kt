package net.techandgraphics.wastical.ui.screen.company.info

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastical.R
import net.techandgraphics.wastical.toPhoneFormat
import net.techandgraphics.wastical.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastical.ui.screen.company.CompanyInfoTopAppBarView
import net.techandgraphics.wastical.ui.screen.company4Preview
import net.techandgraphics.wastical.ui.screen.companyContact4Preview
import net.techandgraphics.wastical.ui.theme.WasticalTheme


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
          CompanyInfoTopAppBarView(state.company) {
            onEvent(CompanyInfoEvent.Button.BackHandler)
          }
        },
      ) {

        LazyColumn(
          contentPadding = it,
          modifier = Modifier.padding(16.dp),
        ) {

          item {
            Text(
              text = "Company Info",
              style = MaterialTheme.typography.headlineSmall,
              modifier = Modifier.padding(bottom = 32.dp)
            )
          }

          item {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
              Image(
                painterResource(R.drawable.im_placeholder),
                contentDescription = null,
                modifier = Modifier
                  .padding(vertical = 24.dp)
                  .clip(CircleShape)
                  .size(180.dp)
              )
            }
          }


          item {
            Column(
              modifier = Modifier.fillMaxWidth(),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
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
            }
          }


          items(state.contacts) { contact ->
            Text(
              text = contact.contact.toPhoneFormat(),
              maxLines = 1,
              overflow = TextOverflow.MiddleEllipsis,
              style = MaterialTheme.typography.bodyMedium,
              textAlign = TextAlign.Center,
              modifier = Modifier.fillMaxWidth()
            )
          }

          item { Spacer(modifier = Modifier.height(24.dp)) }

          items(companyInfoItems) { item ->
            Card(
              onClick = { onEvent(item.event) },
              shape = CircleShape,
              modifier = Modifier.padding(vertical = 4.dp)
            ) {
              Row(modifier = Modifier.padding(16.dp)) {
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


@Preview
@Composable
private fun CompanyInfoScreenPreview() {
  WasticalTheme {
    CompanyInfoScreen(
      state = CompanyInfoState.Success(
        company = company4Preview,
        contacts = listOf(companyContact4Preview)
      ),
      onEvent = {}
    )
  }
}
