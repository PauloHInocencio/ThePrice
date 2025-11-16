package br.com.noartcode.theprice.data.local.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.model.isValid
import br.com.noartcode.theprice.domain.repository.BillsRepository
import br.com.noartcode.theprice.domain.repository.PaymentsRepository
import br.com.noartcode.theprice.domain.usecases.datetime.IGetDaysUntil
import br.com.noartcode.theprice.domain.usecases.datetime.IGetTodayDate
import br.com.noartcode.theprice.platform.notification.INotifier
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import kotlin.math.abs

const val OVERDUE_PAYMENT_REMINDER_WORKER_TAG = "daily-due-sweep"

class OverduePaymentReminderWorker(
    private val manager: WorkManager
) : IOverduePaymentReminderWorker {
     override fun sweepDailyDue() {
         val workRequest = PeriodicWorkRequestBuilder<AndroidOverduePaymentReminderWorker>(6, TimeUnit.HOURS)
             .addTag(OVERDUE_PAYMENT_REMINDER_WORKER_TAG)
             .build()

         manager.enqueueUniquePeriodicWork(
             OVERDUE_PAYMENT_REMINDER_WORKER_TAG,
             ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
             workRequest
         )
    }
}

class AndroidOverduePaymentReminderWorker(
    appContext:Context,
    params: WorkerParameters,
    private val billsRepository: BillsRepository,
    private val paymentsRepository: PaymentsRepository,
    private val notifier: INotifier,
    private val getTodayDate: IGetTodayDate,
    private val getDaysUntil: IGetDaysUntil,
    private val ioDispatcher: CoroutineDispatcher,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        val today = getTodayDate()

        // schedule remender to overdue bills or bills that will overdue in 2 days.
        val pendingPayments = paymentsRepository.getMonthPayments(month = today.month, year = today.year)
            .first()
            .filter { !it.isPayed && maxOf(it.dueDate.day - 2, 1) <= today.day  }


        for (p in pendingPayments) {
            val bill = billsRepository.get(p.billId) ?: continue

            // TODO: Find a better way to calculate a valid end day
            var end: DayMonthAndYear = p.dueDate.copy(day = bill.billingStartDate.day)
            while (end.isValid().not()){
                end = end.copy(day = bill.billingStartDate.day - 1)
            }

            val days = getDaysUntil(startDate = today, endDate = end)

            val (title, description) = when {
                days > 0 -> { "${bill.name} is about to expire" to "${bill.name} expires in $days days" }
                days < 0 -> {  "${bill.name} is overdue" to "${bill.name} is ${abs(days)} days overdue" }
                else -> { "Time to pay ${bill.name}" to "${bill.name} expire today!" }
            }

            notifier.showOverduePaymentNotification(
                title = title,
                description = description,
                paymentId = p.id
            )
        }

        return@withContext Result.success()
    }
}