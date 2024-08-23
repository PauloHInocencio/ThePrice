package br.com.noartcode.theprice.ui.mapper

import br.com.noartcode.theprice.data.local.localdatasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.domain.model.Payment
import br.com.noartcode.theprice.domain.usecases.ICurrencyFormatter
import br.com.noartcode.theprice.domain.usecases.IGetDateFormat
import br.com.noartcode.theprice.domain.usecases.IGetDaysUntil
import br.com.noartcode.theprice.domain.usecases.IGetTodayDate
import br.com.noartcode.theprice.ui.presentation.home.model.PaymentUi
import kotlin.math.abs


interface Mapper<F,T> {
    suspend fun mapFrom(from:F):T
}

class PaymentDomainToUiMapper (
    private val dataSource: BillLocalDataSource,
    private val formatter: ICurrencyFormatter,
    private val getTodayDate: IGetTodayDate,
    private val dateFormat: IGetDateFormat,
    private val getDaysUntil: IGetDaysUntil,
) : Mapper<Payment, PaymentUi?> {

    override suspend fun mapFrom(from: Payment): PaymentUi? {
        val bill = dataSource.getBill(from.billId) ?: return null
        return if (from.paidAt == null) {
            val days = getDaysUntil(startDate = getTodayDate(), endDate = from.dueDate)
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
                price = formatter.format(from.payedValue ?: bill.price)
            )
        } else {
            PaymentUi(
                id = from.id,
                billName = bill.name,
                status = PaymentUi.Status.PAYED,
                statusDescription = "Paid in ${dateFormat(from.paidAt)}",
                price = formatter.format(from.payedValue ?: bill.price)
            )
        }


    }
}