package com.example.budgetingapp

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.common.utils.DataUtils
import co.yml.charts.common.utils.DataUtils.getColorPaletteList
import co.yml.charts.ui.barchart.models.BarData
import co.yml.charts.ui.barchart.models.BarPlotData
import co.yml.charts.ui.barchart.models.GroupBar
import co.yml.charts.ui.barchart.models.GroupBarChartData
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
import java.time.LocalDate
import kotlin.math.*
import java.time.temporal.WeekFields
import java.util.Locale
class BudgetLine {
        val currentMonth = LocalDate.now().month
        val year = LocalDate.now().year

        @Composable
        fun buildGraph(map : Map<Int, Pair<Float, Float>>, WeeklyView : Boolean = true): GroupBarChartData {
            val groupBarList = mutableListOf<GroupBar>()
            var maxValue = Float.MIN_VALUE
    //listOf(GroupBar("week 1", listOf(BarData(point = Point(0f, 10f), label = "expenses"), BarData(point = Point(1f, 20f), label = "income"))),
            //                GroupBar("2", listOf(BarData(point = Point(2f, 10f)), BarData(point = Point(3f, 20f))))
            if(WeeklyView)
            {
                for((week, data) in map)
                {
                    val firstDayOfMonth = LocalDate.of(year, currentMonth, 1)
                    val firstWeek = firstDayOfMonth.get(WeekFields.of(Locale.getDefault()).weekOfMonth())

                    val startOfWeek = firstDayOfMonth.plusDays(((week + 1)  - firstWeek) * 7L)
                    val endOfWeek = startOfWeek.plusDays(6).withMonth(currentMonth.value) // Ensure it stays in the same month

                    val weekRange = "${startOfWeek.month.name.lowercase().replaceFirstChar { it.uppercase() }.substring(0, 3)} ${startOfWeek.dayOfMonth}-${endOfWeek.dayOfMonth}"
                    groupBarList.add(GroupBar(weekRange, listOf(BarData(point = Point(0f, data.first)), BarData(point = Point(0f, data.second)))))
                    if(data.first > maxValue)
                        maxValue = data.first
                    if(data.second > maxValue)
                        maxValue = data.second
                }
            }
            else
            {
                for((month, data) in map)
                {
                    groupBarList.add(GroupBar(LocalDate.of(year, month, 1).month.name.lowercase().replaceFirstChar { it.uppercase() }.substring(0, 3), listOf(BarData(point = Point(0f, data.first)), BarData(point = Point(0f, data.second)))))
                    if(data.first > maxValue)
                        maxValue = data.first
                    if(data.second > maxValue)
                        maxValue = data.second
                }
            }




            val groupBarPlotData = BarPlotData(
                groupBarList = groupBarList,

                barColorPaletteList = listOf(Color(0xFFC62828), Color(0xFF388E3C))
            )
            val xAxisData = AxisData.Builder()
                .axisStepSize(30.dp)
                .steps(groupBarPlotData.groupingSize - 1)
                .backgroundColor(MaterialTheme.colorScheme.background)
                .bottomPadding(20.dp)
                .labelData { index -> groupBarPlotData.groupBarList[index].label }
                .build()

            val yAxisData = AxisData.Builder()
                .steps(5)
                .labelAndAxisLinePadding(30.dp)
                .axisOffset(20.dp)
                .backgroundColor(MaterialTheme.colorScheme.background)
                .labelData { index -> (index * (maxValue / 5)).toString() }
                .build()
            val groupBarChartData = GroupBarChartData(
                barPlotData = groupBarPlotData,
                xAxisData = xAxisData,
                yAxisData = yAxisData,
                backgroundColor = MaterialTheme.colorScheme.background

            )
            return groupBarChartData
        }


}