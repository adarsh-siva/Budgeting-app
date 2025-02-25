package com.example.budgetingapp

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.LineType
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine

class BudgetLine {
        @Composable
        fun buildGraph(points : List<Point>): LineChartData {
            val steps = 5
            var maxValue = Float.MIN_VALUE
            for (point in points)
            {
                if (point.y > maxValue)
                    maxValue = point.y
            }

            val xAxisData = AxisData.Builder()
                .axisStepSize(100.dp)
                .backgroundColor(MaterialTheme.colorScheme.background)
                .steps(5)
                .labelData { i -> i.toString()}
                .labelAndAxisLinePadding(15.dp)
                .build()

            val yAxisData = AxisData.Builder()
                .steps(steps)
                .backgroundColor(MaterialTheme.colorScheme.background)
                .labelAndAxisLinePadding(30.dp)
                .labelData { i ->
                    val yScale = maxValue / steps
                    String.format("%.2f",(i * yScale))
                }.build()
            val lineChartData = LineChartData(
                linePlotData = LinePlotData(
                    lines = listOf(
                        Line(
                            dataPoints = points,
                            LineStyle(lineType = LineType.Straight(false)),
                            IntersectionPoint(),
                            SelectionHighlightPoint(),
                            ShadowUnderLine(),
                            SelectionHighlightPopUp()
                        )
                    ),
                ),
                xAxisData = xAxisData,
                yAxisData = yAxisData,
                gridLines = GridLines(),
                backgroundColor = MaterialTheme.colorScheme.background
            )
            return lineChartData
        }

}