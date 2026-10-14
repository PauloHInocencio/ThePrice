package br.com.noartcode.theprice.data.local.workers

interface IOverduePaymentReminderWorker {
    /**
     * Schedules the periodic overdue-payment sweep.
     * @param restart true replaces any existing schedule and runs a sweep right away (after login, when
     * local data just changed); false keeps an existing schedule untouched (on app start).
     */
    fun sweepDailyDue(restart: Boolean = true)
}