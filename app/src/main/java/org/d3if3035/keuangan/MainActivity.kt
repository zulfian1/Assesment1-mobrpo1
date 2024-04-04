package org.d3if3035.keuangan

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.d3if3035.keuangan.ui.theme.KeuanganTheme
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import androidx.compose.runtime.remember as remember1

class MainActivity : ComponentActivity() {
    private lateinit var expenseDao: ExpenseDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = ExpenseDatabase.getInstance(applicationContext)
        expenseDao = database.expenseDao()

        setContent {
            KeuanganTheme {
                MainScreen(expenseDao)
            }
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MainScreen(expenseDao: ExpenseDao) {
    var expense by remember1 { mutableStateOf("") }
    var description by remember1 { mutableStateOf("") }
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Topbar dengan tulisan "Pengeluaran"
        Text(
            text = stringResource(R.string.pengeluaranku),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Input Expense (biaya)
        OutlinedTextField(
            value = expense,
            onValueChange = { newExpense ->
                val formattedExpense = formatAmount(newExpense)
                expense = formattedExpense
            },
            label = { Text(stringResource(R.string.biaya)) },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                }
            ),
            visualTransformation = VisualTransformation.None
        )

        // Input Description (deskripsi)
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {  keyboardController?.hide() }
            )
        )

        // Button "Submit" dan "Lihat Data"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Button(
                onClick = {
                    if (expense.isEmpty() || description.isEmpty()) {
                        Toast.makeText(context, "Mohon Lengkapi Data", Toast.LENGTH_SHORT).show()
                    } else {
                        val currentDate = Date()
                        val expenseValue = expense.replace(".", "").toDoubleOrNull() ?: 0.0
                        val expenseEntity = ExpenseEntity(description = description, amount = expenseValue, date = currentDate)
                        saveExpense(expenseDao, expenseEntity, context)
                        // Clear input fields after submitting data
                        expense = ""
                        description = ""

                    }

                },
                modifier = Modifier
                    .padding(top = 8.dp)

            ) {
                Text("Submit")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        val intent = Intent(context, ListActivity::class.java)
                        context.startActivity(intent)
                    },

                    modifier = Modifier
                        .padding(top = 8.dp)
                ) {
                    Text(stringResource(R.string.lihat_data))
                }
            }

        }




        Text(
            text = stringResource(R.string.pengeluaran_terbaru),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(top = 16.dp)
        )


        LatestInputsList(expenseDao)

        Image(

            painter = painterResource(id = R.drawable.f),
            contentDescription = "gambar yang bukan vector apa namanya",
            modifier = Modifier
                .size(1000.dp)
                .align(alignment = Alignment.CenterHorizontally)
        )

        Button(
            onClick = {(context as? ComponentActivity)?.recreate()},
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 1.dp)
        ) {
            Text(text = "Refresh")
        }
    }
}





@Composable
fun LatestInputsList(expenseDao: ExpenseDao) {


    var latestExpenses by remember1 { mutableStateOf<List<ExpenseEntity>>(emptyList()) }
    LaunchedEffect(key1 = Unit) {
        latestExpenses = expenseDao.getLatestExpenses(5)
    }

    Column {
        latestExpenses.forEach { expenseEntity ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = expenseEntity.description)
                Text(text = "Rp ${expenseEntity.amount}")
            }
        }
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun saveExpense(expenseDao: ExpenseDao, expenseEntity: ExpenseEntity, context: Context) {

    GlobalScope.launch(Dispatchers.IO) {
        expenseDao.insertExpense(expenseEntity)
    }
    Toast.makeText(context, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
}

fun formatAmount(amount: String): String {
    val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault()) as DecimalFormat
    numberFormat.applyPattern("#,###")

    val formattedAmount = amount.replace(".", "")
    val parsedAmount = formattedAmount.toDoubleOrNull() ?: 0.0

    return numberFormat.format(parsedAmount)
}
