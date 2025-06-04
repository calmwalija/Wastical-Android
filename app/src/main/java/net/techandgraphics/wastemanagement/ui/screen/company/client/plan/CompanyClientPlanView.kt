package net.techandgraphics.wastemanagement.ui.screen.company.client.plan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.techandgraphics.wastemanagement.toAmount
import net.techandgraphics.wastemanagement.ui.screen.account4Preview
import net.techandgraphics.wastemanagement.ui.screen.paymentPlan4Preview
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme

@Composable
fun CompanyClientPlanView(
  state: CompanyClientPlanState.Success,
  onEvent: (CompanyClientPlanEvent) -> Unit,
) {

  Column(
    modifier = Modifier
      .padding(vertical = 16.dp)
      .fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    state.paymentPlans.forEachIndexed { index, paymentPlan ->
      OutlinedCard(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        colors = if (paymentPlan.belongTo) CardDefaults.outlinedCardColors(
          containerColor = MaterialTheme.colorScheme.primary.copy(.1f)
        ) else {
          CardDefaults.elevatedCardColors()
        }
      ) {
        Row(
          modifier = Modifier
            .clickable { }
            .fillMaxWidth()
            .padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {

          RadioButton(selected = paymentPlan.belongTo, onClick = {})

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
              text = paymentPlan.name,
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
              text = paymentPlan.fee.toAmount(),
              color = MaterialTheme.colorScheme.primary
            )

            Text(
              text = paymentPlan.period.name,
              style = MaterialTheme.typography.bodySmall
            )
          }


        }

      }
    }


    Spacer(modifier = Modifier.height(24.dp))

    Button(onClick = { onEvent(CompanyClientPlanEvent.Button.ChangePlan) }) {
      Text(
        text = "Change Payment Plan",
        modifier = Modifier
          .fillMaxWidth(.8f)
          .padding(8.dp),
        textAlign = TextAlign.Center
      )
    }

  }


}


@Preview
@Composable
private fun CompanyClientPlanViewPreview() {
  WasteManagementTheme {
    CompanyClientPlanView(
      state = CompanyClientPlanState.Success(
        account = account4Preview,
        paymentPlans = listOf(paymentPlan4Preview, paymentPlan4Preview)
      ),
      onEvent = {}
    )
  }
}
