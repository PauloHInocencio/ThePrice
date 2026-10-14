package br.com.noartcode.theprice.data.local.workers

class OverduePaymentReminderWorker : IOverduePaymentReminderWorker {
    override fun sweepDailyDue(restart: Boolean) {
        // Not supported on desktop yet
    }
}
