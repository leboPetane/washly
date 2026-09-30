package com.example.data.model

data class CarWashServiceDetail(
    val carWashServiceId: Long,
    val carWashId: Long,
    val serviceId: Long,
    val name: String,
    val description: String,
    val category: String,
    val iconName: String,
    val price: Double,
    val active: Boolean
)

data class CarWashWithServices(
    val carWash: CarWashEntity,
    val services: List<CarWashServiceDetail>
)

data class BookingDetail(
    val booking: BookingEntity,
    val carWash: CarWashEntity,
    val customer: CustomerEntity,
    val items: List<BookingItemEntity>,
    val fee: CarWashFeeEntity? = null
)

data class PartnerEarningsSummary(
    val totalBookingsCount: Int,
    val completedBookingsCount: Int,
    val pendingBookingsCount: Int,
    val acceptedBookingsCount: Int,
    val totalGrossCash: Double,
    val totalWashlyFeeOwed: Double,
    val netPartnerEarnings: Double
)

data class CreateBookingRequest(
    val customerId: Long,
    val carWashId: Long,
    val selectedCarWashServiceIds: List<Long>,
    val vehicleDetails: String,
    val serviceAddress: String,
    val customerNotes: String = ""
)
