package data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id:Long = 0,
    val title:String,
    val invoiceDueDate:String,
    val description:String? = null
)
