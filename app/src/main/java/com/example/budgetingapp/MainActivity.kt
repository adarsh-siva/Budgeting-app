package com.example.budgetingapp

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.graphics.Typeface
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Close
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
            InteractiveQnA()
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
    transactionManager.addTransaction(600.00, LocalDate.of(2025, 1, 5), "January Rent", false, "Rent");
    transactionManager.addTransaction(45.75, LocalDate.of(2025, 1, 7), "Grocery Shopping", false, "Groceries");
    transactionManager.addTransaction(65.00, LocalDate.of(2025, 1, 10), "Electricity Bill", false, "Utilities");
    transactionManager.addTransaction(25.30, LocalDate.of(2025, 1, 12), "Dinner with Friends", false, "Restaurants");
    transactionManager.addTransaction(150.00, LocalDate.of(2025, 1, 15), "Freelance Work", true, "Other");
    transactionManager.addTransaction(8.99, LocalDate.of(2025, 1, 17), "Netflix Subscription", false, "Entertainment");
    transactionManager.addTransaction(20.50, LocalDate.of(2025, 1, 19), "New T-shirt", false, "Clothing");
    transactionManager.addTransaction(52.20, LocalDate.of(2025, 1, 21), "Gas Bill", false, "Utilities");
    transactionManager.addTransaction(35.00, LocalDate.of(2025, 1, 23), "Movie Night", false, "Entertainment");
    transactionManager.addTransaction(75.00, LocalDate.of(2025, 1, 25), "Tutoring Income", true, "Other");
    transactionManager.addTransaction(12.49, LocalDate.of(2025, 1, 28), "Coffee & Snacks", false, "Restaurants");
    transactionManager.addTransaction(600.00, LocalDate.of(2025, 2, 5), "February Rent", false, "Rent");
    transactionManager.addTransaction(50.85, LocalDate.of(2025, 2, 6), "Grocery Shopping", false, "Groceries");
    transactionManager.addTransaction(40.00, LocalDate.of(2025, 2, 8), "Clothing Sale", false, "Clothing");
    transactionManager.addTransaction(200.00, LocalDate.of(2025, 2, 10), "Part-time Job", true, "Other");
    transactionManager.addTransaction(100.00, LocalDate.of(2025, 2, 10), "Valentine's Dinner", false, "Restaurants");
    transactionManager.addTransaction(75.99, LocalDate.of(2025, 2, 18), "New Headphones", false, "Shops");
    transactionManager.addTransaction(30.45, LocalDate.of(2025, 2, 20), "Phone Bill", false, "Utilities");
    transactionManager.addTransaction(15.99, LocalDate.of(2025, 2, 23), "Spotify Subscription", false, "Entertainment");
    transactionManager.addTransaction(300.00, LocalDate.of(2025, 2, 27), "Scholarship", true, "Other");
    transactionManager.addTransaction(60.00, LocalDate.of(2025, 2, 27), "Grocery Shopping", false, "Groceries");
    transactionManager.addTransaction(600.00, LocalDate.of(2025, 3, 5), "March Rent", false, "Rent");
    transactionManager.addTransaction(85.25, LocalDate.of(2025, 3, 7), "Clothing Shopping", false, "Clothing");
    transactionManager.addTransaction(10.00, LocalDate.of(2025, 3, 10), "Lunch at Campus", false, "Restaurants");
    transactionManager.addTransaction(250.00, LocalDate.of(2025, 3, 12), "Side Hustle Income", true, "Other");
    transactionManager.addTransaction(50.00, LocalDate.of(2025, 3, 15), "Gaming Subscription", false, "Entertainment");
    transactionManager.addTransaction(15.00, LocalDate.of(2025, 3, 18), "Uber Ride", false, "Other");
    transactionManager.addTransaction(55.50, LocalDate.of(2025, 3, 20), "Groceries", false, "Groceries");
    transactionManager.addTransaction(20.00, LocalDate.of(2025, 3, 22), "Coffee Date", false, "Restaurants");
    transactionManager.addTransaction(100.00, LocalDate.of(2025, 3, 25), "Concert Ticket", false, "Entertainment");
    transactionManager.addTransaction(900.00, LocalDate.of(2025, 3, 28), "Tax Refund", true, "Other");
    transactionManager.addTransaction(200.00, LocalDate.of(2025, 4, 2), "Freelance Payment", true, "Other");
    transactionManager.addTransaction(600.00, LocalDate.of(2025, 4, 5), "April Rent", false, "Rent");
    transactionManager.addTransaction(60.75, LocalDate.of(2025, 4, 7), "Grocery Shopping", false, "Groceries");
    transactionManager.addTransaction(35.00, LocalDate.of(2025, 4, 9), "Dinner with Family", false, "Restaurants");
    transactionManager.addTransaction(90.00, LocalDate.of(2025, 4, 12), "New Sneakers", false, "Clothing");
    transactionManager.addTransaction(80.00, LocalDate.of(2025, 4, 14), "Electricity Bill", false, "Utilities");
    transactionManager.addTransaction(10.00, LocalDate.of(2025, 4, 18), "Coffee Break", false, "Restaurants");
    transactionManager.addTransaction(250.00, LocalDate.of(2025, 4, 22), "Scholarship Bonus", true, "Other");
    transactionManager.addTransaction(15.00, LocalDate.of(2025, 4, 22), "Uber Ride", false, "Other");
    transactionManager.addTransaction(500.00, LocalDate.of(2025, 4, 25), "Freelance Project", true, "Other");
    transactionManager.addTransaction(600.00, LocalDate.of(2025, 5, 5), "May Rent", false, "Rent");
    transactionManager.addTransaction(55.30, LocalDate.of(2025, 5, 7), "Grocery Shopping", false, "Groceries");
    transactionManager.addTransaction(20.00, LocalDate.of(2025, 5, 9), "Lunch with Friends", false, "Restaurants");
    transactionManager.addTransaction(40.00, LocalDate.of(2025, 5, 12), "Cinema Ticket", false, "Entertainment");
    transactionManager.addTransaction(150.00, LocalDate.of(2025, 5, 15), "Part-time Job", true, "Other");
    transactionManager.addTransaction(80.00, LocalDate.of(2025, 5, 18), "New Jacket", false, "Clothing");
    transactionManager.addTransaction(35.75, LocalDate.of(2025, 5, 20), "Gas Bill", false, "Utilities");
    transactionManager.addTransaction(25.00, LocalDate.of(2025, 5, 22), "Spotify & Netflix", false, "Entertainment");
    transactionManager.addTransaction(900.00, LocalDate.of(2025, 5, 25), "Tutoring Income", true, "Other");
    transactionManager.addTransaction(75.00, LocalDate.of(2025, 5, 27), "Groceries", false, "Groceries");
    transactionManager.addTransaction(600.00, LocalDate.of(2025, 6, 5), "June Rent", false, "Rent");
    transactionManager.addTransaction(90.50, LocalDate.of(2025, 6, 7), "Summer Clothes Shopping", false, "Clothing");
    transactionManager.addTransaction(12.00, LocalDate.of(2025, 6, 10), "Coffee & Donuts", false, "Restaurants");
    transactionManager.addTransaction(300.00, LocalDate.of(2025, 6, 12), "Freelance Project", true, "Other");
    transactionManager.addTransaction(45.00, LocalDate.of(2025, 6, 15), "Dinner with Parents", false, "Restaurants");
    transactionManager.addTransaction(20.00, LocalDate.of(2025, 6, 18), "Uber Ride", false, "Other");
    transactionManager.addTransaction(110.00, LocalDate.of(2025, 6, 20), "Electricity & Water Bill", false, "Utilities");
    transactionManager.addTransaction(18.50, LocalDate.of(2025, 6, 22), "Hobby Supplies", false, "Shops");
    transactionManager.addTransaction(350.00, LocalDate.of(2025, 6, 25), "Scholarship Payment", true, "Other");
    transactionManager.addTransaction(65.00, LocalDate.of(2025, 6, 27), "Groceries", false, "Groceries");
    transactionManager.addTransaction(1200.00, LocalDate.of(2025, 7, 5), "July Rent", false, "Rent");
    transactionManager.addTransaction(10.50, LocalDate.of(2025, 7, 7), "Lunch at College", false, "Restaurants");
    transactionManager.addTransaction(85.00, LocalDate.of(2025, 7, 10), "Clothing Shopping", false, "Clothing");
    transactionManager.addTransaction(1200.00, LocalDate.of(2025, 7, 12), "Part-time Job Payment", true, "Other");
    transactionManager.addTransaction(30.00, LocalDate.of(2025, 7, 15), "Streaming Services", false, "Entertainment");
    transactionManager.addTransaction(60.00, LocalDate.of(2025, 7, 18), "Dinner with Friends", false, "Restaurants");
    transactionManager.addTransaction(40.00, LocalDate.of(2025, 7, 20), "Grocery Shopping", false, "Groceries");
    transactionManager.addTransaction(22.00, LocalDate.of(2025, 7, 22), "Museum Visit", false, "Entertainment");
    transactionManager.addTransaction(100.00, LocalDate.of(2025, 7, 25), "Online Course Fee", false, "Other");
    transactionManager.addTransaction(500.00, LocalDate.of(2025, 7, 28), "Tax Refund", true, "Other");
    transactionManager.addTransaction(150.00, LocalDate.of(2025, 8, 1), "Phone Upgrade", false, "Shops");
    transactionManager.addTransaction(45.00, LocalDate.of(2025, 8, 3), "Groceries", false, "Groceries");
    transactionManager.addTransaction(1200.00, LocalDate.of(2025, 8, 5), "August Rent", false, "Rent");
    transactionManager.addTransaction(15.00, LocalDate.of(2025, 8, 7), "Lunch with Classmates", false, "Restaurants");
    transactionManager.addTransaction(95.00, LocalDate.of(2025, 8, 10), "New Sneakers", false, "Clothing");
    transactionManager.addTransaction(250.00, LocalDate.of(2025, 8, 12), "Freelance Design Work", true, "Other");
    transactionManager.addTransaction(28.00, LocalDate.of(2025, 8, 15), "Gas Bill", false, "Utilities");
    transactionManager.addTransaction(70.00, LocalDate.of(2025, 8, 18), "Grocery Shopping", false, "Groceries");
    transactionManager.addTransaction(20.00, LocalDate.of(2025, 8, 20), "Concert Ticket", false, "Entertainment");
    transactionManager.addTransaction(1280.00, LocalDate.of(2025, 8, 22), "Part-time Job Payment", true, "Other");
    transactionManager.addTransaction(55.00, LocalDate.of(2025, 8, 25), "New Jeans", false, "Clothing");
    transactionManager.addTransaction(200.00, LocalDate.of(2025, 8, 28), "Tutoring Side Gig", true, "Other");
    transactionManager.addTransaction(1200.00, LocalDate.of(2025, 9, 5), "September Rent", false, "Rent");
    transactionManager.addTransaction(40.00, LocalDate.of(2025, 9, 7), "Movie Night", false, "Entertainment");
    transactionManager.addTransaction(65.00, LocalDate.of(2025, 9, 10), "New Hoodie", false, "Clothing");
    transactionManager.addTransaction(300.00, LocalDate.of(2025, 9, 12), "Graphic Design Work", true, "Other");
    transactionManager.addTransaction(50.00, LocalDate.of(2025, 9, 15), "Electricity Bill", false, "Utilities");
    transactionManager.addTransaction(18.00, LocalDate.of(2025, 9, 18), "Spotify Subscription", false, "Entertainment");
    transactionManager.addTransaction(75.00, LocalDate.of(2025, 9, 20), "Grocery Shopping", false, "Groceries");
    transactionManager.addTransaction(890.00, LocalDate.of(2025, 9, 22), "Freelance Writing Gig", true, "Other");
    transactionManager.addTransaction(600.00, LocalDate.of(2025, 10, 5), "October Rent", false, "Rent");
    transactionManager.addTransaction(35.00, LocalDate.of(2025, 10, 7), "Coffee & Snacks", false, "Restaurants");
    transactionManager.addTransaction(80.00, LocalDate.of(2025, 10, 10), "Sweater & Scarf", false, "Clothing");
    transactionManager.addTransaction(1250.00, LocalDate.of(2025, 11, 12), "Part-time Job Payment", true, "Other");
    transactionManager.addTransaction(40.00, LocalDate.of(2025, 11, 15), "Phone Bill", false, "Utilities");
    transactionManager.addTransaction(60.00, LocalDate.of(2025, 11, 18), "Dinner with Family", false, "Restaurants");
    transactionManager.addTransaction(100.00, LocalDate.of(2025, 12, 20), "Grocery Shopping", false, "Groceries");
    transactionManager.addTransaction(400.00, LocalDate.of(2025, 12, 25), "Scholarship Stipend", true, "Other");
    transactionManager.addTransaction(1000.00, LocalDate.of(2025, 12, 30), "End-of-Year Bonus", true, "Other");












    transactionsMap = transactionManager.getOrderedTransactions()
    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Budget Buddy",
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
@Composable
fun InteractiveQnA() {
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    // Only storing answers
    val answersList = listOf(

        "You can track your monthly income and expenses to get a better idea of your financial health.",
        "The app allows you to categorize your spending, so you can easily see where your money goes.",
        "To get started, simply enter your income and expenses, including the amount, category, and date.",
        "You can view your current balance anytime to know exactly how much money you have available.",
        "The app provides summaries of your income and expenses over different periods like weekly and monthly.",
        "You can update or delete any transaction you've entered, keeping your records accurate.",
        "Use the search feature to find specific transactions based on category, amount, or date.",
        "The expense categorization feature helps you identify areas where you can cut back on spending.",
        "The income section of the app lets you input your salary, freelance work, or any other income source.",
        "If you need to adjust your budget, simply modify the entries under the 'income' or 'expenses' sections.",
        "The app gives you the flexibility to input recurring expenses, like rent or subscriptions.",
        "By tracking your spending patterns, you can make informed decisions about your future financial goals.",
        "To get a clear picture of your finances, generate monthly summaries that show your total income and expenses.",
        "You can view a breakdown of your expenses by category, making it easy to see where you're spending the most.",
        "Set a budget for each category and the app will help you stay on track by alerting you if you're nearing your limit.",
        "You can add notes to each transaction, giving you a more detailed record of your spending.",
        "The app supports multiple currencies, so you can track your finances no matter where you are.",
        "The 'Delete Transaction' feature allows you to remove any accidental entries that were incorrectly added.",
        "By analyzing your spending trends, you can identify opportunities to save more money.",
        "Use the app to track not only expenses but also your savings goals to see how you're progressing.",
        "The 'Add Transaction' button allows you to quickly record any new income or expense.",
        "When you categorize expenses, you can easily generate reports that show how much you're spending in each area.",
        "The app helps students build good financial habits by tracking their daily expenses and income.",
        "You can sort transactions by date to view your expenses in chronological order.",
        "Looking to save? The app helps you set savings goals and track your progress over time.",
        "The app includes a feature to track one-time purchases, such as textbooks or gadgets, under the 'miscellaneous' category.",
        "You can filter transactions by date range, allowing you to generate reports for any period.",
        "By regularly updating your entries, you can ensure your balance always reflects your real-time finances.",
        "Set recurring reminders for monthly expenses like rent, utilities, or subscriptions so you never forget.",
        "The app's user-friendly interface makes it easy for students to manage their finances without feeling overwhelmed.",
        "The 'Transaction History' feature lets you quickly look back at your financial activity.",
        "Track and compare your expenses month by month to see if you're staying within budget.",
        "The app also allows you to set financial goals, such as saving for a vacation or paying off debt."

    )

    Box(
        contentAlignment = Alignment.TopEnd,
        modifier = Modifier.fillMaxSize()
            .offset(x = (-16).dp, y = 8.dp),
    ) {
        Button(onClick = { showMenu = true }) {
            Text("?")
        }
    }

    // Show menu as a separate screen/modal
    if (showMenu) {
        FullScreenMenu(
            onDismiss = { showMenu = false },
            answersList = answersList,
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it }
        )
    }
}

@Composable
fun FullScreenMenu(
    onDismiss: () -> Unit,
    answersList: List<String>,
    searchQuery: TextFieldValue,
    onSearchQueryChange: (TextFieldValue) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFFEFEFEF))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Close Button
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close Menu")
            }

            Text("Search Finance Q&A", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(16.dp))


            // Search Bar
            BasicTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .border(1.dp, Color.Gray),
                textStyle = LocalTextStyle.current.copy(fontSize = 16.sp, color = Color.Black)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Filtered results based on search query
            val results = answersList.filter { answer ->
                answer.contains(searchQuery.text, ignoreCase = true)
            }

            if (results.isEmpty()) {
                Text("No answers found.", fontSize = 14.sp, color = Color.Gray)
            } else {
                // Box answers separately with light borders
                results.forEach { answer ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .border(1.dp, Color.LightGray, shape = MaterialTheme.shapes.medium)
                            .padding(12.dp)
                    ) {
                        Text(text = answer, fontSize = 14.sp, color = Color.Black)
                    }
                }
            }
        }
    }
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