package org.d3if3035.keuangan

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ExpenseDao {
    @Insert
    fun insertExpense(expense: ExpenseEntity)


    @Query("SELECT * FROM expenses ORDER BY date DESC LIMIT :limit")
    suspend fun getLatestExpenses(limit: Int): List<ExpenseEntity>

    @Query("SELECT * FROM expenses ORDER BY date ASC")
    fun getAllExpensesSortedByDateAscending(): List<ExpenseEntity>

    @Query("SELECT * FROM expenses")
    fun getAllExpenses(): List<ExpenseEntity>

    @Query("SELECT SUM(amount) FROM expenses")
    fun getTotalExpense(): Double

}
