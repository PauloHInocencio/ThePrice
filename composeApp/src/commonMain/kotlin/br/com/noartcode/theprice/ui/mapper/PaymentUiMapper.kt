package br.com.noartcode.theprice.ui.mapper

import br.com.noartcode.theprice.data.local.datasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.model.Payment
import br.com.noartcode.theprice.domain.model.isValid
import br.com.noartcode.theprice.domain.model.toEpochMilliseconds
import br.com.noartcode.theprice.domain.model.toLocalDate
import br.com.noartcode.theprice.domain.usecases.ICurrencyFormatter
import br.com.noartcode.theprice.domain.usecases.IGetDateFormat
import br.com.noartcode.theprice.domain.usecases.IGetDaysUntil
import br.com.noartcode.theprice.domain.usecases.IGetTodayDate
import br.com.noartcode.theprice.ui.presentation.home.PaymentUi
import kotlinx.datetime.Instant
import kotlin.math.abs


interface UiMapper<F,T> {
    suspend fun mapFrom(from:F):T
}

class PaymentDomainToUiMapper (
    private val dataSource: BillLocalDataSource,
    private val formatter: ICurrencyFormatter,
    private val getTodayDate: IGetTodayDate,
    private val dateFormat: IGetDateFormat,
    private val getDaysUntil: IGetDaysUntil,
) : UiMapper<Payment, PaymentUi?> {

    override suspend fun mapFrom(from: Payment): PaymentUi? {
        val bill = dataSource.getBill(from.billId) ?: return null
        return if (!from.isPayed) {

            // TODO: Find a better way to calculate a valid end day
            val start = getTodayDate()
            var end:DayMonthAndYear = from.dueDate.copy(day = bill.billingStartDate.day)
            while(end.isValid().not()) {
                end = end.copy(day = bill.billingStartDate.day - 1)
            }

            val days = getDaysUntil(startDate = start, endDate = end)

            val (description, status) = when {
                days > 0 -> { "Expires in $days days" to PaymentUi.Status.PENDING }
                days < 0 -> {  "${abs(days)} days overdue" to PaymentUi.Status.OVERDUE }
                else -> { "Expire today!" to PaymentUi.Status.PENDING }
            }
            PaymentUi(
                id = from.id,
                billName = bill.name,
                status = status,
                statusDescription = description,
                price = formatter.format(from.price)
            )
        } else {
            PaymentUi(
                id = from.id,
                billName = bill.name,
                status = PaymentUi.Status.PAYED,
                statusDescription = "Paid in ${dateFormat(from.dueDate)}",
                price = formatter.format(from.price)
            )
        }
    }
}