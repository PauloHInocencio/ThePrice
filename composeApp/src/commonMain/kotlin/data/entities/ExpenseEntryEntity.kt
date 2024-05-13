package data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "expensesEntries",
    foreignKeys = [
        ForeignKey(
            entity = ExpenseEntity::class,
            parentColumns = ["id"],
            childColumns = ["expenseId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class ExpenseEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id:Long = 0,
    val expenseId:Long,
    val status:String,
    val modifiedAt:String? = null

)
