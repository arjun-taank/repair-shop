package com.repairshop.tracker.model

import java.io.Serializable

data class Item(
    val id: Long = 0,
    val name: String,
    val category: String,
    val brand: String = "",
    val model: String = "",
    val description: String = "",
    val totalCount: Int = 0,
    val inCount: Int = 0,
    val outCount: Int = 0
) : Serializable {
    val isInStock: Boolean get() = inCount > 0
}

data class Customer(
    val id: Long = 0,
    val name: String,
    val phone: String,
    val address: String = "",
    val totalCharge: Double = 0.0,
    val paidAdvance: Double = 0.0
) : Serializable {
    val remainingBalance: Double get() = (totalCharge - paidAdvance).coerceAtLeast(0.0)
}

data class TransactionEntry(
    val id: Long = 0,
    val type: String, // "IN" or "OUT"
    val itemId: Long,
    val itemName: String,
    val itemCategory: String,
    val itemModel: String = "",
    val customerId: Long,
    val customerName: String,
    val customerPhone: String = "",
    val dateIn: String,
    val timeIn: String = "10:00 AM",
    val dateOut: String? = null,
    val repairCharge: Double = 0.0,
    val advancePaid: Double = 0.0,
    val status: String = "In Repair", // "In Repair", "Completed", "Delivered"
    val remarks: String = ""
) : Serializable

data class PaymentRecord(
    val id: Long = 0,
    val customerId: Long,
    val date: String,
    val title: String,
    val amount: Double,
    val isPending: Boolean = false
) : Serializable
