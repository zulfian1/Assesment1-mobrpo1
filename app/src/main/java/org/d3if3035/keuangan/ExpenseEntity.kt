    package org.d3if3035.keuangan

    import androidx.room.Entity
    import androidx.room.PrimaryKey
    import java.util.Date

    @Entity(tableName = "expenses")
    data class ExpenseEntity(
        @PrimaryKey(autoGenerate = true)
        val id: Long = 0,
        val description: String,
        val amount: Double,
        val date: Date
    )
