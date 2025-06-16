package net.techandgraphics.wastemanagement.ui.screen.company.info.method

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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastemanagement.gatewayDrawableRes
import net.techandgraphics.wastemanagement.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastemanagement.ui.screen.company.CompanyInfoTopAppBarView
import net.techandgraphics.wastemanagement.ui.screen.company4Preview
import net.techandgraphics.wastemanagement.ui.screen.paymentMethodWithGateway4Preview
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyInfoMethodScreen(
  state: CompanyInfoMethodState,
  onEvent: (CompanyInfoMethodEvent) -> Unit,
) {

  when (state) {
    CompanyInfoMethodState.Loading -> LoadingIndicatorView()
    is CompanyInfoMethodState.Success -> Scaffold(
      topBar = {
        TopAppBar(
          title = { CompanyInfoTopAppBarView(state.company) },
          navigationIcon = {
            IconButton(onClick = { onEvent(CompanyInfoMethodEvent.Button.BackHandler) }) {
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
          text = "Payment Method",
          style = MaterialTheme.typography.headlineMedium,
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 8.dp),
        )
        LazyColumn(
          modifier = Modifier.padding(vertical = 16.dp),
        ) {
          items(state.methods) { ofType ->
            OutlinedCard(
              modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
              colors = CardDefaults.elevatedCardColors()
            ) {
              Row(
                modifier = Modifier
                  .clickable { }
                  .fillMaxWidth()
                  .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {

                Image(
                  painterResource(gatewayDrawableRes[ofType.gateway.id.minus(1).toInt()]),
                  contentDescription = null,
                  modifier = Modifier
                    .clip(CircleShape)
                    .size(32.dp),
                  contentScale = ContentScale.Crop,
                )

                Column(
                  modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .weight(1f)
                ) {

                  Text(
                    text = ofType.method.account,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.MiddleEllipsis
                  )

                  Text(
                    text = ofType.gateway.name,
                    style = MaterialTheme.typography.titleMedium
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


@Preview
@Composable
private fun CompanyInfoMethodScreenPreview() {
  WasteManagementTheme {
    CompanyInfoMethodScreen(
      state = CompanyInfoMethodState.Success(
        company = company4Preview,
        methods = (1..3).map { listOf(paymentMethodWithGateway4Preview) }.flatten()
      ),
      onEvent = {}
    )
  }
}
