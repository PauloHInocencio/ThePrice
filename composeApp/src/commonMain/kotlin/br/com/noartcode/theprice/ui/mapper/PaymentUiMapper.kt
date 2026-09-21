package br.com.noartcode.theprice.ui.mapper

import br.com.noartcode.theprice.data.local.datasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.model.Payment
import br.com.noartcode.theprice.domain.model.isValid
import br.com.noartcode.theprice.domain.usecases.ICurrencyFormatter
import br.com.noartcode.theprice.domain.usecases.IGetDateFormat
import br.com.noartcode.theprice.domain.usecases.datetime.IGetDaysUntil
import br.com.noartcode.theprice.domain.usecases.datetime.IGetTodayDate
import br.com.noartcode.theprice.ui.presentation.home.PaymentUi
import kotlin.math.abs
import org.jetbrains.compose.resources.getString
import theprice.composeapp.generated.resources.Res
import theprice.composeapp.generated.resources.days_overdue
import theprice.composeapp.generated.resources.expires_in_days
import theprice.composeapp.generated.resources.expires_today
import theprice.composeapp.generated.resources.paid_in_date


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

            val days = getDaysUntil(startDate = getTodayDate() , endDate = from.dueDate)

            val (description, status) = when {
                days > 0 -> { getString(Res.string.expires_in_days, days) to PaymentUi.Status.PENDING }
                days < 0 -> {  getString(Res.string.days_overdue, abs(days)) to PaymentUi.Status.OVERDUE }
                else -> { getString(Res.string.expires_today) to PaymentUi.Status.PENDING }
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
                statusDescription = getString(Res.string.paid_in_date, dateFormat(from.dueDate)),
                price = formatter.format(from.price)
            )
        }
    }
}