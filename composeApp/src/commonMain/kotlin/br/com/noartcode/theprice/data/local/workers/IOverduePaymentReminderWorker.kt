package br.com.noartcode.theprice.data.local.workers

interface IOverduePaymentReminderWorker {
    fun sweepDailyDue()
}