package net.techandgraphics.wastemanagement.ui.screen.company.home

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Pie
import net.techandgraphics.wastemanagement.data.local.database.dashboard.payment.DailyPaymentSummary
import net.techandgraphics.wastemanagement.ui.theme.WasteManagementTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyHomeClientPaymentDailyView(dailyPayments: List<DailyPaymentSummary>) {


  val containerColor = MaterialTheme.colorScheme.primary

  ColumnChart(
    modifier = Modifier
      .fillMaxWidth()
      .height(200.dp),
    labelProperties = LabelProperties(
      textStyle = TextStyle(
        color = MaterialTheme.colorScheme.primary
      ),
      enabled = true
    ),
    labelHelperProperties = LabelHelperProperties(
      textStyle = TextStyle(
        color = MaterialTheme.colorScheme.primary
      ),
    ), indicatorProperties = HorizontalIndicatorProperties(
      textStyle = TextStyle(
        color = MaterialTheme.colorScheme.primary
      ),

    ),
    data = remember {
      dailyPayments.map {
        Bars(
          label = it.paymentDate,
          values = listOf(
            Bars.Data(
              label = null,
              value = it.totalAmount.toDouble(),
              color = SolidColor(containerColor),
              properties = BarProperties(
                cornerRadius = Bars.Data.Radius.Circular(20.dp)
              )
            )
          ),
        )
      }
    },
    barProperties = BarProperties(
      cornerRadius = Bars.Data.Radius.Rectangle(topRight = 6.dp, topLeft = 6.dp),
      spacing = 3.dp,
    ),
    animationSpec = spring(
      dampingRatio = Spring.DampingRatioMediumBouncy,
      stiffness = Spring.StiffnessLow
    ),
  )


}


@Preview(showBackground = true)
@Composable
private fun CompanyHomeClientPaymentDailyViewPreview() {
  WasteManagementTheme {
    Box(modifier = Modifier.padding(16.dp)) {
      CompanyHomeClientPaymentDailyView(dailyPayments = listOf())
    }
  }
}
