package com.example.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "profile",
    indices = [Index(value = ["user_id"], unique = true)]
)
data class ProfileEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "profile_id")
    val profileId: Long = 0,
    @ColumnInfo(name = "user_id")
    val userId: String,
    @ColumnInfo(name = "profile_type")
    val profileType: ProfileType,
    @ColumnInfo(name = "full_name")
    val fullName: String,
    @ColumnInfo(name = "phone")
    val phone: String,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "customer",
    foreignKeys = [
        ForeignKey(
            entity = ProfileEntity::class,
            parentColumns = ["profile_id"],
            childColumns = ["profile_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["profile_id"])]
)
data class CustomerEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "customer_id")
    val customerId: Long = 0,
    @ColumnInfo(name = "profile_id")
    val profileId: Long,
    @ColumnInfo(name = "address")
    val address: String,
    @ColumnInfo(name = "default_vehicle")
    val defaultVehicle: String
)

@Entity(
    tableName = "car_wash",
    foreignKeys = [
        ForeignKey(
            entity = ProfileEntity::class,
            parentColumns = ["profile_id"],
            childColumns = ["profile_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["profile_id"])]
)
data class CarWashEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "car_wash_id")
    val carWashId: Long = 0,
    @ColumnInfo(name = "profile_id")
    val profileId: Long,
    @ColumnInfo(name = "business_name")
    val businessName: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "phone")
    val phone: String,
    @ColumnInfo(name = "address")
    val address: String,
    @ColumnInfo(name = "latitude")
    val latitude: String,
    @ColumnInfo(name = "longitude")
    val longitude: String,
    @ColumnInfo(name = "status")
    val status: CarWashStatus = CarWashStatus.OFFLINE,
    @ColumnInfo(name = "rating")
    val rating: Double = 4.8,
    @ColumnInfo(name = "review_count")
    val reviewCount: Int = 24,
    @ColumnInfo(name = "service_radius_km")
    val serviceRadiusKm: Int = 15,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "service_list")
data class ServiceListEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "service_id")
    val serviceId: Long = 0,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "category")
    val category: String,
    @ColumnInfo(name = "icon_name")
    val iconName: String
)

@Entity(
    tableName = "car_wash_services",
    foreignKeys = [
        ForeignKey(
            entity = CarWashEntity::class,
            parentColumns = ["car_wash_id"],
            childColumns = ["car_wash_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ServiceListEntity::class,
            parentColumns = ["service_id"],
            childColumns = ["service_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["car_wash_id"]),
        Index(value = ["service_id"]),
        Index(value = ["car_wash_id", "service_id"], unique = true)
    ]
)
data class CarWashServiceEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "car_wash_service_id")
    val carWashServiceId: Long = 0,
    @ColumnInfo(name = "car_wash_id")
    val carWashId: Long,
    @ColumnInfo(name = "service_id")
    val serviceId: Long,
    @ColumnInfo(name = "price")
    val price: Double,
    @ColumnInfo(name = "active")
    val active: Boolean = true,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "bookings",
    foreignKeys = [
        ForeignKey(
            entity = CustomerEntity::class,
            parentColumns = ["customer_id"],
            childColumns = ["customer_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CarWashEntity::class,
            parentColumns = ["car_wash_id"],
            childColumns = ["car_wash_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["customer_id"]),
        Index(value = ["car_wash_id"]),
        Index(value = ["status"])
    ]
)
data class BookingEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "booking_id")
    val bookingId: Long = 0,
    @ColumnInfo(name = "customer_id")
    val customerId: Long,
    @ColumnInfo(name = "car_wash_id")
    val carWashId: Long,
    @ColumnInfo(name = "total_price")
    val totalPrice: Double,
    @ColumnInfo(name = "status")
    val status: BookingStatus = BookingStatus.PENDING,
    @ColumnInfo(name = "booking_date")
    val bookingDate: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "vehicle_details")
    val vehicleDetails: String,
    @ColumnInfo(name = "service_address")
    val serviceAddress: String,
    @ColumnInfo(name = "customer_notes")
    val customerNotes: String = "",
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "booking_items",
    foreignKeys = [
        ForeignKey(
            entity = BookingEntity::class,
            parentColumns = ["booking_id"],
            childColumns = ["booking_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CarWashServiceEntity::class,
            parentColumns = ["car_wash_service_id"],
            childColumns = ["car_wash_service_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["booking_id"]),
        Index(value = ["car_wash_service_id"])
    ]
)
data class BookingItemEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "booking_item_id")
    val bookingItemId: Long = 0,
    @ColumnInfo(name = "booking_id")
    val bookingId: Long,
    @ColumnInfo(name = "car_wash_service_id")
    val carWashServiceId: Long,
    @ColumnInfo(name = "service_name")
    val serviceName: String,
    @ColumnInfo(name = "price")
    val price: Double,
    @ColumnInfo(name = "date")
    val date: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "car_wash_payments",
    foreignKeys = [
        ForeignKey(
            entity = CarWashEntity::class,
            parentColumns = ["car_wash_id"],
            childColumns = ["car_wash_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["car_wash_id"])]
)
data class CarWashPaymentEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "payment_id")
    val paymentId: Long = 0,
    @ColumnInfo(name = "car_wash_id")
    val carWashId: Long,
    @ColumnInfo(name = "booking_id")
    val bookingId: Long,
    @ColumnInfo(name = "total_paid")
    val totalPaid: Double,
    @ColumnInfo(name = "date_of_payment")
    val dateOfPayment: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "car_wash_fees",
    foreignKeys = [
        ForeignKey(
            entity = CarWashEntity::class,
            parentColumns = ["car_wash_id"],
            childColumns = ["car_wash_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = BookingEntity::class,
            parentColumns = ["booking_id"],
            childColumns = ["booking_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["car_wash_id"]),
        Index(value = ["booking_id"], unique = true)
    ]
)
data class CarWashFeeEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "fee_id")
    val feeId: Long = 0,
    @ColumnInfo(name = "car_wash_id")
    val carWashId: Long,
    @ColumnInfo(name = "booking_id")
    val bookingId: Long,
    @ColumnInfo(name = "amount")
    val amount: Double,
    @ColumnInfo(name = "outstanding")
    val outstanding: Boolean = true,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
