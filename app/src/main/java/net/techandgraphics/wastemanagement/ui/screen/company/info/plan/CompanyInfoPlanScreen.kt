package net.techandgraphics.wastemanagement.ui.screen.company.info.plan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastemanagement.toAmount
import net.techandgraphics.wastemanagement.ui.screen.LoadingIndicatorView
import net.techandgraphics.wastemanagement.ui.screen.company.CompanyInfoTopAppBarView
import net.techandgraphics.wastemanagement.ui.screen.company4Preview
import net.techandgraphics.wastemanagement.ui.screen.paymentPlan4Preview
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyInfoPlanScreen(
  state: CompanyInfoPlanState,
  onEvent: (CompanyInfoPlanEvent) -> Unit,
) {
  when (state) {
    CompanyInfoPlanState.Loading -> LoadingIndicatorView()
    is CompanyInfoPlanState.Success -> Scaffold(
      topBar = {
        TopAppBar(
          title = { CompanyInfoTopAppBarView(state.company) },
          navigationIcon = {
            IconButton(onClick = { onEvent(CompanyInfoPlanEvent.Button.BackHandler) }) {
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
          text = "Payment Plan",
          style = MaterialTheme.typography.headlineMedium,
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 8.dp),
        )

        LazyColumn(
          modifier = Modifier
            .padding(vertical = 16.dp)
        ) {
          itemsIndexed(state.plans) { index, plan ->
            OutlinedCard(
              modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
              colors = CardDefaults.elevatedCardColors()
            ) {
              Row(
                modifier = Modifier
                  .clickable { }
                  .fillMaxWidth()
                  .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {

                Column(
                  modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .weight(1f)
                ) {
                  Text(
                    text = "Payment Plan ${index.plus(1)}",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.MiddleEllipsis
                  )
                  Text(
                    text = plan.name,
                    style = MaterialTheme.typography.titleMedium
                  )
                }

                Box(
                  modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .background(MaterialTheme.colorScheme.secondary.copy(.1f))
                    .fillMaxHeight(.05f)
                    .width(1.dp)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text(
                    text = plan.fee.toAmount(),
                    color = MaterialTheme.colorScheme.primary
                  )
                  Text(
                    text = plan.period.name,
                    style = MaterialTheme.typography.bodySmall
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
private fun CompanyInfoPlanScreenPreview() {
  WasteManagementTheme {
    CompanyInfoPlanScreen(
      state = CompanyInfoPlanState.Success(
        company = company4Preview,
        plans = (1..3).map { listOf(paymentPlan4Preview, paymentPlan4Preview) }.flatten()
      ),
      onEvent = {}
    )
  }
}
