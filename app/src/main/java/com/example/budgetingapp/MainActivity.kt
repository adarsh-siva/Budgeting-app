package com.example.budgetingapp

import android.graphics.Typeface
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp


import kotlin.text.toDoubleOrNull

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.barchart.GroupBarChart
import co.yml.charts.ui.barchart.models.GroupBarChartData
import co.yml.charts.ui.piechart.charts.DonutPieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import kotlinx.coroutines.launch

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.text.format
import kotlin.text.isBlank
import kotlin.text.isNotBlank

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BudgetingApp()

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetingApp() {
    val transactionManager = remember { TransactionManager() }

    var showAddDialog by remember { mutableStateOf(false) }
    var transactions by remember { mutableStateOf(transactionManager.transactions) }
    var transactionsMap by remember { mutableStateOf(transactionManager.getOrderedTransactions()) }
    var selectedTabIndex by remember { mutableIntStateOf(1) }
    var transactionToEdit by remember { mutableStateOf<Transaction?>(null) }
    var isAddingTransaction by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val tabItems = listOf("Transactions", "Dashboard", "Categories")
    var searchText by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    //preloads transaction data
    transactions.add(Transaction(0, 600.00, LocalDate.of(2025, 1, 5), "January Rent", false, "Rent"));
    transactions.add(Transaction(1, 45.75, LocalDate.of(2025, 1, 7), "Grocery Shopping", false, "Groceries"));
    transactions.add(Transaction(2, 65.00, LocalDate.of(2025, 1, 10), "Electricity Bill", false, "Utilities"));
    transactions.add(Transaction(3, 25.30, LocalDate.of(2025, 1, 12), "Dinner with Friends", false, "Restaurants"));
    transactions.add(Transaction(4, 150.00, LocalDate.of(2025, 1, 15), "Freelance Work", true, "Other"));
    transactions.add(Transaction(5, 8.99, LocalDate.of(2025, 1, 17), "Netflix Subscription", false, "Entertainment"));
    transactions.add(Transaction(6, 20.50, LocalDate.of(2025, 1, 19), "New T-shirt", false, "Clothing"));
    transactions.add(Transaction(7, 52.20, LocalDate.of(2025, 1, 21), "Gas Bill", false, "Utilities"));
    transactions.add(Transaction(8, 35.00, LocalDate.of(2025, 1, 23), "Movie Night", false, "Entertainment"));
    transactions.add(Transaction(9, 75.00, LocalDate.of(2025, 1, 25), "Tutoring Income", true, "Other"));
    transactions.add(Transaction(10, 12.49, LocalDate.of(2025, 1, 28), "Coffee & Snacks", false, "Restaurants"));
    transactions.add(Transaction(11, 600.00, LocalDate.of(2025, 2, 5), "February Rent", false, "Rent"));
    transactions.add(Transaction(12, 50.85, LocalDate.of(2025, 2, 6), "Grocery Shopping", false, "Groceries"));
    transactions.add(Transaction(13, 40.00, LocalDate.of(2025, 2, 8), "Clothing Sale", false, "Clothing"));
    transactions.add(Transaction(14, 200.00, LocalDate.of(2025, 2, 10), "Part-time Job", true, "Other"));
    transactions.add(Transaction(15, 100.00, LocalDate.of(2025, 2, 15), "Valentine's Dinner", false, "Restaurants"));
    transactions.add(Transaction(16, 75.99, LocalDate.of(2025, 2, 18), "New Headphones", false, "Shops"));
    transactions.add(Transaction(17, 30.45, LocalDate.of(2025, 2, 20), "Phone Bill", false, "Utilities"));
    transactions.add(Transaction(18, 15.99, LocalDate.of(2025, 2, 23), "Spotify Subscription", false, "Entertainment"));
    transactions.add(Transaction(19, 300.00, LocalDate.of(2025, 2, 25), "Scholarship", true, "Other"));
    transactions.add(Transaction(20, 60.00, LocalDate.of(2025, 2, 27), "Grocery Shopping", false, "Groceries"));
    transactions.add(Transaction(21, 600.00, LocalDate.of(2025, 3, 5), "March Rent", false, "Rent"));
    transactions.add(Transaction(22, 85.25, LocalDate.of(2025, 3, 7), "Clothing Shopping", false, "Clothing"));
    transactions.add(Transaction(23, 10.00, LocalDate.of(2025, 3, 10), "Lunch at Campus", false, "Restaurants"));
    transactions.add(Transaction(24, 250.00, LocalDate.of(2025, 3, 12), "Side Hustle Income", true, "Other"));
    transactions.add(Transaction(25, 50.00, LocalDate.of(2025, 3, 15), "Gaming Subscription", false, "Entertainment"));
    transactions.add(Transaction(26, 15.00, LocalDate.of(2025, 3, 18), "Uber Ride", false, "Other"));
    transactions.add(Transaction(27, 55.50, LocalDate.of(2025, 3, 20), "Groceries", false, "Groceries"));
    transactions.add(Transaction(28, 20.00, LocalDate.of(2025, 3, 22), "Coffee Date", false, "Restaurants"));
    transactions.add(Transaction(29, 100.00, LocalDate.of(2025, 3, 25), "Concert Ticket", false, "Entertainment"));
    transactions.add(Transaction(30, 900.00, LocalDate.of(2025, 3, 28), "Tax Refund", true, "Other"));
    transactions.add(Transaction(31, 200.00, LocalDate.of(2025, 4, 2), "Freelance Payment", true, "Other"));
    transactions.add(Transaction(32, 600.00, LocalDate.of(2025, 4, 5), "April Rent", false, "Rent"));
    transactions.add(Transaction(33, 60.75, LocalDate.of(2025, 4, 7), "Grocery Shopping", false, "Groceries"));
    transactions.add(Transaction(34, 35.00, LocalDate.of(2025, 4, 9), "Dinner with Family", false, "Restaurants"));
    transactions.add(Transaction(35, 90.00, LocalDate.of(2025, 4, 12), "New Sneakers", false, "Clothing"));
    transactions.add(Transaction(36, 80.00, LocalDate.of(2025, 4, 14), "Electricity Bill", false, "Utilities"));
    transactions.add(Transaction(37, 10.00, LocalDate.of(2025, 4, 18), "Coffee Break", false, "Restaurants"));
    transactions.add(Transaction(38, 250.00, LocalDate.of(2025, 4, 20), "Scholarship Bonus", true, "Other"));
    transactions.add(Transaction(39, 15.00, LocalDate.of(2025, 4, 22), "Uber Ride", false, "Other"));
    transactions.add(Transaction(40, 500.00, LocalDate.of(2025, 4, 25), "Freelance Project", true, "Other"));
    transactions.add(Transaction(41, 600.00, LocalDate.of(2025, 5, 5), "May Rent", false, "Rent"));
    transactions.add(Transaction(42, 55.30, LocalDate.of(2025, 5, 7), "Grocery Shopping", false, "Groceries"));
    transactions.add(Transaction(43, 20.00, LocalDate.of(2025, 5, 9), "Lunch with Friends", false, "Restaurants"));
    transactions.add(Transaction(44, 40.00, LocalDate.of(2025, 5, 12), "Cinema Ticket", false, "Entertainment"));
    transactions.add(Transaction(45, 150.00, LocalDate.of(2025, 5, 15), "Part-time Job", true, "Other"));
    transactions.add(Transaction(46, 80.00, LocalDate.of(2025, 5, 18), "New Jacket", false, "Clothing"));
    transactions.add(Transaction(47, 35.75, LocalDate.of(2025, 5, 20), "Gas Bill", false, "Utilities"));
    transactions.add(Transaction(48, 25.00, LocalDate.of(2025, 5, 22), "Spotify & Netflix", false, "Entertainment"));
    transactions.add(Transaction(49, 900.00, LocalDate.of(2025, 5, 25), "Tutoring Income", true, "Other"));
    transactions.add(Transaction(50, 75.00, LocalDate.of(2025, 5, 27), "Groceries", false, "Groceries"));
    transactions.add(Transaction(51, 600.00, LocalDate.of(2025, 6, 5), "June Rent", false, "Rent"));
    transactions.add(Transaction(52, 90.50, LocalDate.of(2025, 6, 7), "Summer Clothes Shopping", false, "Clothing"));
    transactions.add(Transaction(53, 12.00, LocalDate.of(2025, 6, 10), "Coffee & Donuts", false, "Restaurants"));
    transactions.add(Transaction(54, 300.00, LocalDate.of(2025, 6, 12), "Freelance Project", true, "Other"));
    transactions.add(Transaction(55, 45.00, LocalDate.of(2025, 6, 15), "Dinner with Parents", false, "Restaurants"));
    transactions.add(Transaction(56, 20.00, LocalDate.of(2025, 6, 18), "Uber Ride", false, "Other"));
    transactions.add(Transaction(57, 110.00, LocalDate.of(2025, 6, 20), "Electricity & Water Bill", false, "Utilities"));
    transactions.add(Transaction(58, 18.50, LocalDate.of(2025, 6, 22), "Hobby Supplies", false, "Shops"));
    transactions.add(Transaction(59, 350.00, LocalDate.of(2025, 6, 25), "Scholarship Payment", true, "Other"));
    transactions.add(Transaction(60, 65.00, LocalDate.of(2025, 6, 27), "Groceries", false, "Groceries"));
    transactions.add(Transaction(61, 1200.00, LocalDate.of(2025, 7, 5), "July Rent", false, "Rent"));
    transactions.add(Transaction(62, 10.50, LocalDate.of(2025, 7, 7), "Lunch at College", false, "Restaurants"));
    transactions.add(Transaction(63, 85.00, LocalDate.of(2025, 7, 10), "Clothing Shopping", false, "Clothing"));
    transactions.add(Transaction(64, 1200.00, LocalDate.of(2025, 7, 12), "Part-time Job Payment", true, "Other"));
    transactions.add(Transaction(65, 30.00, LocalDate.of(2025, 7, 15), "Streaming Services", false, "Entertainment"));
    transactions.add(Transaction(66, 60.00, LocalDate.of(2025, 7, 18), "Dinner with Friends", false, "Restaurants"));
    transactions.add(Transaction(67, 40.00, LocalDate.of(2025, 7, 20), "Grocery Shopping", false, "Groceries"));
    transactions.add(Transaction(68, 22.00, LocalDate.of(2025, 7, 22), "Museum Visit", false, "Entertainment"));
    transactions.add(Transaction(69, 100.00, LocalDate.of(2025, 7, 25), "Online Course Fee", false, "Other"));
    transactions.add(Transaction(70, 500.00, LocalDate.of(2025, 7, 28), "Tax Refund", true, "Other"));
    transactions.add(Transaction(71, 150.00, LocalDate.of(2025, 8, 1), "Phone Upgrade", false, "Shops"));
    transactions.add(Transaction(72, 45.00, LocalDate.of(2025, 8, 3), "Groceries", false, "Groceries"));
    transactions.add(Transaction(73, 1200.00, LocalDate.of(2025, 8, 5), "August Rent", false, "Rent"));
    transactions.add(Transaction(74, 15.00, LocalDate.of(2025, 8, 7), "Lunch with Classmates", false, "Restaurants"));
    transactions.add(Transaction(75, 95.00, LocalDate.of(2025, 8, 10), "New Sneakers", false, "Clothing"));
    transactions.add(Transaction(76, 250.00, LocalDate.of(2025, 8, 12), "Freelance Design Work", true, "Other"));
    transactions.add(Transaction(77, 28.00, LocalDate.of(2025, 8, 15), "Gas Bill", false, "Utilities"));
    transactions.add(Transaction(78, 70.00, LocalDate.of(2025, 8, 18), "Grocery Shopping", false, "Groceries"));
    transactions.add(Transaction(79, 20.00, LocalDate.of(2025, 8, 20), "Concert Ticket", false, "Entertainment"));
    transactions.add(Transaction(80, 1280.00, LocalDate.of(2025, 8, 22), "Part-time Job Payment", true, "Other"));
    transactions.add(Transaction(81, 55.00, LocalDate.of(2025, 8, 25), "New Jeans", false, "Clothing"));
    transactions.add(Transaction(82, 200.00, LocalDate.of(2025, 8, 28), "Tutoring Side Gig", true, "Other"));
    transactions.add(Transaction(83, 1200.00, LocalDate.of(2025, 9, 5), "September Rent", false, "Rent"));
    transactions.add(Transaction(84, 40.00, LocalDate.of(2025, 9, 7), "Movie Night", false, "Entertainment"));
    transactions.add(Transaction(85, 65.00, LocalDate.of(2025, 9, 10), "New Hoodie", false, "Clothing"));
    transactions.add(Transaction(86, 300.00, LocalDate.of(2025, 9, 12), "Graphic Design Work", true, "Other"));
    transactions.add(Transaction(87, 50.00, LocalDate.of(2025, 9, 15), "Electricity Bill", false, "Utilities"));
    transactions.add(Transaction(88, 18.00, LocalDate.of(2025, 9, 18), "Spotify Subscription", false, "Entertainment"));
    transactions.add(Transaction(89, 75.00, LocalDate.of(2025, 9, 20), "Grocery Shopping", false, "Groceries"));
    transactions.add(Transaction(90, 890.00, LocalDate.of(2025, 9, 22), "Freelance Writing Gig", true, "Other"));
    transactions.add(Transaction(91, 600.00, LocalDate.of(2025, 10, 5), "October Rent", false, "Rent"));
    transactions.add(Transaction(92, 35.00, LocalDate.of(2025, 10, 7), "Coffee & Snacks", false, "Restaurants"));
    transactions.add(Transaction(93, 80.00, LocalDate.of(2025, 10, 10), "Sweater & Scarf", false, "Clothing"));
    transactions.add(Transaction(94, 1250.00, LocalDate.of(2025, 11, 12), "Part-time Job Payment", true, "Other"));
    transactions.add(Transaction(95, 40.00, LocalDate.of(2025, 11, 15), "Phone Bill", false, "Utilities"));
    transactions.add(Transaction(96, 60.00, LocalDate.of(2025, 11, 18), "Dinner with Family", false, "Restaurants"));
    transactions.add(Transaction(97, 100.00, LocalDate.of(2025, 12, 20), "Grocery Shopping", false, "Groceries"));
    transactions.add(Transaction(98, 400.00, LocalDate.of(2025, 12, 25), "Scholarship Stipend", true, "Other"));
    transactions.add(Transaction(99, 1000.00, LocalDate.of(2025, 12, 30), "End-of-Year Bonus", true, "Other"));












    transactionsMap = transactionManager.getOrderedTransactions()
    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Budgeting App",
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
                TabRow(selectedTabIndex = selectedTabIndex) {
                    tabItems.forEachIndexed { index, title ->
                        Tab(
                            text = { Text(title) },
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index }
                        )
                    }
                }
                //if in transaction tab add a search bar
                if (selectedTabIndex == 0) {

                    OutlinedTextField(
                        maxLines = 1,
                        value = searchText,
                        onValueChange = {
                            searchText = it
                            if (it.isBlank()) {
                                transactionsMap = transactionManager.getOrderedTransactions()
                            } else {
                                transactionsMap = transactionManager.searchTransactions(it)
                            }
                        },

                        label = { Text("Search Transactions") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        trailingIcon = {
                            if (searchText.isNotBlank()) {
                                IconButton(onClick = {
                                    searchText = ""
                                    transactionsMap = transactionManager.getOrderedTransactions()
                                }) {
                                    Icon(Icons.Filled.Clear, "Clear Search")
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() } // Hides the keyboard when Enter is pressed
                        )

                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(onClick = { showAddDialog = true }) {
                            Icon(Icons.Filled.Add, "Add Transaction")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add Transaction")
                        }
                    }

                }
            }
        },
        content = { innerPadding ->


            val map = transactionManager.getReverseOrderedTransactions()



            when (selectedTabIndex) {
                //UI for transactions
                0 ->
                    TransactionList(
                    transactions = transactionsMap,
                    modifier = Modifier.padding(innerPadding),
                    onEditTransaction = { transaction ->
                        transactionToEdit = transaction
                        showAddDialog = true
                    }
                )
                //UI for Dashboard
                1 -> DashboardScreen(modifier = Modifier.padding(innerPadding),transactionManager.getBalance(), map)
                //UI for Categories
                2 -> CategoriesScreen(modifier = Modifier.padding(innerPadding),transactionManager.transactions)
            }
            if (showAddDialog) {
                AddTransactionDialog(
                    onDismiss = {
                        showAddDialog = false
                        transactionToEdit = null
                                },
                    onAddTransaction = { amount, date, name, type, category ->
                        if (!isAddingTransaction) {
                            isAddingTransaction = true
                            coroutineScope.launch {
                                if (transactionToEdit != null) {
                                    transactionManager.updateTransaction(
                                        transactionToEdit!!.id,
                                        amount,
                                        date,
                                        name,
                                        type,
                                        category
                                    )
                                } else {
                                    transactionManager.addTransaction(amount, date, name, type,category)
                                }
                                transactions = transactionManager.transactions
                                transactionsMap = transactionManager.getOrderedTransactions()
                                showAddDialog = false
                                transactionToEdit = null
                                isAddingTransaction = false
                            }
                        }
                    },
                    transactionToEdit = transactionToEdit,
                    onDeleteTransaction = { transaction ->
                        transactionManager.deleteTransaction(transaction.id)
                        transactions = transactionManager.transactions
                        transactionsMap = transactionManager.getOrderedTransactions()
                        showAddDialog = false
                        transactionToEdit = null
                    }
                )
            }
        }
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(modifier: Modifier, list:  List<Transaction>)
{

    val categoryMap = mutableMapOf<String, Float>()
        for(transaction in list)
        {

            if(!transaction.isIncome)
                categoryMap[transaction.category] =  (categoryMap.getOrDefault(transaction.category, 0f) + transaction.amount.toFloat())

        }
    //legend for categories screen
    val categoryColors = listOf(
        "Rent" to Color(0xFFC62828),         // Deep Red
        "Utilities" to Color(0xFF1565C0),     // Strong Blue
        "Entertainment" to Color(0xFF7B1FA2), // Rich Purple
        "Groceries" to Color(0xFF2E7D32),     // Fresh Green
        "Restaurants" to Color(0xFFFFA000),   // Warm Orange
        "Shops" to Color(0xFFD81B60),         // Bright Pink
        "Clothing" to Color(0xFF00897B),      // Teal
        "Other" to Color(0xFF757575)          // Neutral Gray
    )

    val categoryList = mutableListOf<PieChartData.Slice>()
    for((category, amount) in categoryMap)
    {
        categoryList.add(PieChartData.Slice(category,String.format("%.2f", amount).toFloat(), categoryColors.find { it.first == category }!!.second))
    }
    //donut chart data
    val donutChartData = PieChartData(
        slices = categoryList,
        plotType = PlotType.Donut
    )
    val donutChartConfig = PieChartConfig(
        strokeWidth = 120f,
        activeSliceAlpha = .9f,
        isAnimationEnable = true,
        labelVisible = true,
        labelColor = Color.Black,
        sliceLabelTextColor = Color.Black,
        backgroundColor = MaterialTheme.colorScheme.background,
        chartPadding = 40,
        labelTypeface = Typeface.defaultFromStyle(Typeface.NORMAL),
        isSumVisible = true,
        labelType = PieChartConfig.LabelType.VALUE,

    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        categoryColors.chunked(2).forEach { rowItems ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                rowItems.forEach { (category, color) ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(color, shape = CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = category, fontSize = 16.sp)
                    }
                }
            }

        }
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "Expense by Category",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,

        )
        DonutPieChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            donutChartData,
            donutChartConfig
        )
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(modifier: Modifier, balance: Double,map :Map<LocalDate, List<Transaction>>) {
    val BudgetLine = remember { BudgetLine() }
    val weeklyData = mutableMapOf<Int, Pair<Float, Float >>()
    val monthlyData = mutableMapOf<Int, Pair<Float, Float>>()
    var selectedIndex by remember { mutableIntStateOf(0) }
    var isWeekly by remember { mutableStateOf(true) };
    val options = listOf("Weekly", "Monthly")
    //sets all the weekly and monthly data to zero
    for(i in 0..3)
    {
        weeklyData[i] = Pair(0f, 0f)
    }
    for(i in 1..12)
    {
        monthlyData[i] = Pair(0f, 0f)
    }
    //weekly view
    if(isWeekly)
    {
        for((date, transactions) in map)
        {
            //only allows transactions that are in the same month to show
            if(date.monthValue != LocalDate.now().monthValue)
                continue
            val weekIndex = ((date.dayOfMonth - 1) / 7).coerceIn(0, 3)
            //adds the expenses and income to the weekly data
            val expenses = transactions.filter { !it.isIncome }.sumOf { it.amount }.toFloat()
            val income = transactions.filter { it.isIncome }.sumOf { it.amount }.toFloat()
            weeklyData[weekIndex] = Pair(weeklyData[weekIndex]!!.first + expenses, weeklyData[weekIndex]!!.second + income)
        }
    }
    //monthly view
    else
    {
        for((date, transactions) in map)
        {
            val monthIndex = date.monthValue.coerceIn(1, 12)
            //adds the expenses and income to the monthly data
            val expenses = transactions.filter { !it.isIncome }.sumOf { it.amount }.toFloat()
            val income = transactions.filter { it.isIncome }.sumOf { it.amount }.toFloat()
            monthlyData[monthIndex] = Pair(monthlyData[monthIndex]!!.first + expenses, monthlyData[monthIndex]!!.second + income)
        }
    }
    val lineChartData : GroupBarChartData
    if(isWeekly)
         lineChartData = BudgetLine.buildGraph(weeklyData)
    else
         lineChartData = BudgetLine.buildGraph(monthlyData,false)
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Total Balance",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "$${String.format("%.2f", balance)}",
            fontSize = 48.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 40.dp),
            color = if (balance >= 0) Color(0xFF388E3C) else Color(0xFFC62828) // Green if positive, red if negative

        )
        Text(
            text = "Income & Expenses",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 30.dp)
        )
        SingleChoiceSegmentedButtonRow {
            options.forEachIndexed { index, label ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = options.size
                    ),
                    onClick = { selectedIndex = index
                        if(selectedIndex == 0) {isWeekly = true}
                        else {isWeekly = false}},
                    selected = index == selectedIndex,
                    label = { Text(label) }
                )
            }
        }

            GroupBarChart(modifier = Modifier
                .height(350.dp)
                .fillMaxWidth()
                , groupBarChartData = lineChartData)



    }




}


@Composable
fun TransactionList(transactions: Map<LocalDate, List<Transaction>>, modifier: Modifier = Modifier, onEditTransaction: (Transaction) -> Unit) {

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        for((date, transactions) in transactions) {
            item {
                dateHeader(date = date)
            }
            for(transaction in transactions) {
                item {
                    TransactionItem(transaction = transaction,onEditTransaction = onEditTransaction)
                }
            }

        }
    }
}
@Composable
fun dateHeader(date: LocalDate)
{
    val formattedDate = date.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
    Text(
        text = formattedDate,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )

}

@Composable
fun TransactionItem(transaction: Transaction,  onEditTransaction: (Transaction) -> Unit) {
    val backgroundColor = if (transaction.isIncome) Color(0xFFE8F5E9) else Color(0xFFFCE4EC) // Light green for income, light red for expense
    val textColor = if (transaction.isIncome) Color(0xFF388E3C) else Color(0xFFC62828) // Dark green for income, dark red for expense
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEditTransaction(transaction) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), //width of list item
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {


                Text(text = transaction.name, fontWeight = FontWeight.Bold, color = textColor)

            }
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                if(!transaction.isIncome)
                    Text(text = transaction.category, color = textColor, textAlign = TextAlign.Start)
            }
            Text(text = "${if (transaction.isIncome) "+ $" else "- $"}${String.format("%.2f", transaction.amount)}", fontWeight = FontWeight.Bold, color = textColor)
        }
    }
}





@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddTransactionDialog(
    onDismiss: () -> Unit,
    onAddTransaction: (Double, LocalDate, String, Boolean, String) -> Unit,
    transactionToEdit: Transaction? = null,
    onDeleteTransaction: ((Transaction) -> Unit)? = null
) {
    var amount by remember { mutableStateOf(transactionToEdit?.amount?.toString() ?: "") }
    var date by remember { mutableStateOf(transactionToEdit?.date ?: LocalDate.now()) }
    var formattedDate = date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
    var category by remember { mutableStateOf(transactionToEdit?.category ?: "") }
    var isIncome by remember { mutableStateOf(transactionToEdit?.isIncome ?: false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var transactionName by remember { mutableStateOf(transactionToEdit?.name ?: "") }
    val transactionManager = remember { TransactionManager() }
    var categoryNames by remember { mutableStateOf(transactionManager.categories) }
    var selectedCategoryIndex by remember { mutableStateOf(categoryNames.indexOf(category).takeIf { it >= 0 } ?: 0) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        date = Instant.ofEpochMilli(datePickerState.selectedDateMillis ?: 0).atZone(
                            ZoneOffset.UTC).toLocalDate()
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,

        text = {

            Row(modifier = Modifier.fillMaxWidth(),verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {

                var selectedIndex by remember { mutableIntStateOf(0) }
                if(isIncome)
                    selectedIndex = 1
                val options = listOf("Expense", "Income")
                SingleChoiceSegmentedButtonRow {
                    options.forEachIndexed { index, label ->
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = options.size
                            ),
                            onClick = { selectedIndex = index
                                if(selectedIndex == 0) {isIncome = false}
                                      else {isIncome = true}},
                            selected = index == selectedIndex,
                            label = { Text(label) }
                        )
                    }
                }
            }

            Column {
                Spacer(modifier = Modifier.height(50.dp))

                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {showDatePicker = true}
                ) {
                    Text(formattedDate)
                }

                OutlinedTextField(
                    value = transactionName,
                    singleLine = true,
                    onValueChange = { transactionName = it },
                    label = { Text("Transaction Name") },
                    modifier = Modifier.fillMaxWidth(),

                    )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = amount,
                    singleLine = true,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )




            }


            Column {
                if(!isIncome)
                {
                    Spacer(modifier = Modifier.height(250.dp))
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Spacer(modifier = Modifier.height(50.dp))

                        categoryNames.forEachIndexed {index, label ->
                            InputChip(
                                selected = selectedCategoryIndex == index,
                                modifier = Modifier.padding(horizontal = 4.dp),
                                onClick = { selectedCategoryIndex = index},
                                label = { Text(label) }
                            )
                        }
                }

                }
            }
        },

        confirmButton = @androidx.compose.runtime.Composable {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center)
            {
                if(transactionToEdit != null)
                {
                    Button(onClick = {
                        if (onDeleteTransaction != null) {
                            onDeleteTransaction(transactionToEdit)
                        }
                    }, colors = ButtonDefaults.buttonColors(Color(0xFFC62828))) {

                        Text("Delete")
                    }
                }
                if(transactionToEdit != null)
                    Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = {
                    val parsedAmount = amount.toDoubleOrNull()
                    if (parsedAmount != null && transactionName.isNotBlank() && selectedCategoryIndex != -1) {
                        onAddTransaction(parsedAmount, date, transactionName, isIncome, categoryNames[selectedCategoryIndex])
                    }
                }) {
                    if(transactionToEdit == null)
                        Text("Add Transaction")
                    else
                        Text("Edit")
                }
            }

        }


    )

}