package data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id:Int = 0,
    val title:String,
    val description:String? = null,
    val value:Float,
    val type:String,
    val status:String,
    val invoiceDueDate:Long,
)
