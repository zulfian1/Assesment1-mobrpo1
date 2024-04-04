package org.d3if3035.keuangan

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.d3if3035.keuangan.ui.theme.KeuanganTheme
import java.util.*

class ListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KeuanganTheme {
                ExpenseList()
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseList() {
    var sortAscending by remember { mutableStateOf(false) }
    var showTotal by remember { mutableStateOf(false) }
    var totalExpense by remember { mutableDoubleStateOf(0.0) }

    val context = LocalContext.current
    val expenseDao = ExpenseDatabase.getInstance(context).expenseDao()

    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Daftar Pengeluaran") },
                navigationIcon = {
                    IconButton(onClick = {
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                    }) {
                        Icon(painterResource(id = R.drawable.baseline_arrow_back_24), contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            if (showTotal) {
                BottomAppBar(
                    content = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Total Pengeluaran Bulan Ini: $totalExpense",
                                modifier = Modifier.padding(16.dp)
                            )
                            IconButton(
                                onClick = { showTotal = false },
                                modifier = Modifier.padding(end = 16.dp)
                            ) {
                                Icon(
                                    painterResource(id = R.drawable.ic_arrow_down),
                                    contentDescription = "Collapse"
                                )
                            }
                        }
                    }
                )
            }
        }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Urutkan:")
                Checkbox(checked = sortAscending, onCheckedChange = { sortAscending = it })
            }

            Spacer(modifier = Modifier.height(16.dp))

            var expenses by remember { mutableStateOf<List<ExpenseEntity>>(emptyList()) }

            LaunchedEffect(Unit) {
                val loadedExpenses = withContext(Dispatchers.IO) {
                    if (sortAscending) {
                        expenseDao.getAllExpensesSortedByDateAscending()
                    } else {
                        expenseDao.getAllExpenses()
                    }
                }
                expenses = loadedExpenses
            }

            LazyColumn {
                items(expenses) { expense ->
                    ExpenseListItem(expense)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = showTotal, onCheckedChange = { showTotal = it })
                Text("Tampilkan Jumlah Pengeluaran dalam Satu Bulan")
            }

            if (showTotal) {
                LaunchedEffect(Unit) {
                    val total = withContext(Dispatchers.IO) {
                        expenseDao.getTotalExpense()
                    }
                    totalExpense = total
                }
            }

            if (showTotal) {
                Text("Total Pengeluaran: $totalExpense")
            }

        }
    }
}






@Composable
fun ExpenseListItem(expense: ExpenseEntity) {
    Card(modifier = Modifier.padding(vertical = 8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Deskripsi: ${expense.description}")
            Text(text = "Pengeluaran: ${expense.amount}")
            Text(text = "Tanggal: ${expense.date}")
        }
    }
}
