package br.com.noartcode.theprice.data.local.mapper

import kotlin.test.Test

class BillWithPaymentsMapperTest {

    @Test
    fun `toSyncEvent creates sync event with correct endpoint`() {
        // GIVEN a BillWithPayments and action "update"
        // WHEN calling toSyncEvent
        // THEN returns SyncEvent with endpoint = "bill"
    }

    @Test
    fun `toSyncEvent creates sync event with provided action`() {
        // GIVEN a BillWithPayments and action "create"
        // WHEN calling toSyncEvent
        // THEN returns SyncEvent with action = "create"
    }

    @Test
    fun `toSyncEvent serializes billWithPayments to JSON payload`() {
        // GIVEN a BillWithPayments with specific bill and payments
        // WHEN calling toSyncEvent
        // THEN payload contains JSON-serialized BillWithPaymentsDto
    }

    @Test
    fun `toSyncEvent uses bill createdAt for event timestamp`() {
        // GIVEN a BillWithPayments with bill.createdAt = 1234567890
        // WHEN calling toSyncEvent
        // THEN SyncEvent.createdAt = 1234567890
    }

    @Test
    fun `toSyncEvent generates unique event ID`() {
        // GIVEN two identical BillWithPayments objects
        // WHEN calling toSyncEvent on both
        // THEN each SyncEvent has a different UUID
    }
}