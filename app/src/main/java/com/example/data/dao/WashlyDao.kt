package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.data.model.BookingEntity
import com.example.data.model.BookingItemEntity
import com.example.data.model.BookingStatus
import com.example.data.model.CarWashEntity
import com.example.data.model.CarWashFeeEntity
import com.example.data.model.CarWashPaymentEntity
import com.example.data.model.CarWashServiceDetail
import com.example.data.model.CarWashServiceEntity
import com.example.data.model.CarWashStatus
import com.example.data.model.CustomerEntity
import com.example.data.model.ProfileEntity
import com.example.data.model.ServiceListEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WashlyDao {

    // --- Profiles & Accounts ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfiles(profiles: List<ProfileEntity>)

    @Query("SELECT * FROM profile ORDER BY profile_id ASC")
    fun getAllProfiles(): Flow<List<ProfileEntity>>

    @Query("SELECT * FROM profile WHERE profile_id = :profileId")
    suspend fun getProfileById(profileId: Long): ProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomers(customers: List<CustomerEntity>)

    @Query("SELECT * FROM customer WHERE customer_id = :customerId")
    suspend fun getCustomerById(customerId: Long): CustomerEntity?

    @Query("SELECT * FROM customer WHERE profile_id = :profileId LIMIT 1")
    suspend fun getCustomerByProfileId(profileId: Long): CustomerEntity?

    @Query("SELECT * FROM customer LIMIT 1")
    suspend fun getDefaultCustomer(): CustomerEntity?

    // --- Car Washes ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCarWashes(carWashes: List<CarWashEntity>)

    @Query("SELECT * FROM car_wash ORDER BY status DESC, rating DESC")
    fun getAllCarWashes(): Flow<List<CarWashEntity>>

    @Query("SELECT * FROM car_wash WHERE car_wash_id = :carWashId")
    suspend fun getCarWashById(carWashId: Long): CarWashEntity?

    @Query("SELECT * FROM car_wash WHERE car_wash_id = :carWashId")
    fun observeCarWashById(carWashId: Long): Flow<CarWashEntity?>

    @Query("SELECT * FROM car_wash WHERE profile_id = :profileId LIMIT 1")
    suspend fun getCarWashByProfileId(profileId: Long): CarWashEntity?

    @Query("UPDATE car_wash SET status = :status, updated_at = :updatedAt WHERE car_wash_id = :carWashId")
    suspend fun updateCarWashStatus(carWashId: Long, status: CarWashStatus, updatedAt: Long)

    // --- Services ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMasterServices(services: List<ServiceListEntity>)

    @Query("SELECT * FROM service_list ORDER BY service_id ASC")
    fun getAllMasterServices(): Flow<List<ServiceListEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCarWashServices(services: List<CarWashServiceEntity>)

    @Query("""
        SELECT 
            cws.car_wash_service_id AS carWashServiceId,
            cws.car_wash_id AS carWashId,
            s.service_id AS serviceId,
            s.name AS name,
            s.description AS description,
            s.category AS category,
            s.icon_name AS iconName,
            cws.price AS price,
            cws.active AS active
        FROM car_wash_services cws
        INNER JOIN service_list s ON cws.service_id = s.service_id
        WHERE cws.car_wash_id = :carWashId
        ORDER BY s.service_id ASC
    """)
    fun getCarWashServices(carWashId: Long): Flow<List<CarWashServiceDetail>>

    @Query("""
        SELECT 
            cws.car_wash_service_id AS carWashServiceId,
            cws.car_wash_id AS carWashId,
            s.service_id AS serviceId,
            s.name AS name,
            s.description AS description,
            s.category AS category,
            s.icon_name AS iconName,
            cws.price AS price,
            cws.active AS active
        FROM car_wash_services cws
        INNER JOIN service_list s ON cws.service_id = s.service_id
        WHERE cws.car_wash_id = :carWashId AND cws.car_wash_service_id IN (:serviceIds)
    """)
    suspend fun getCarWashServicesByIds(carWashId: Long, serviceIds: List<Long>): List<CarWashServiceDetail>

    @Query("""
        UPDATE car_wash_services 
        SET price = :newPrice, updated_at = :updatedAt 
        WHERE car_wash_service_id = :carWashServiceId
    """)
    suspend fun updateServicePrice(carWashServiceId: Long, newPrice: Double, updatedAt: Long)

    @Query("""
        UPDATE car_wash_services 
        SET active = :active, updated_at = :updatedAt 
        WHERE car_wash_service_id = :carWashServiceId
    """)
    suspend fun toggleServiceActive(carWashServiceId: Long, active: Boolean, updatedAt: Long)

    // --- Bookings ---
    @Insert
    suspend fun insertBooking(booking: BookingEntity): Long

    @Insert
    suspend fun insertBookingItems(items: List<BookingItemEntity>)

    @Query("SELECT * FROM bookings WHERE booking_id = :bookingId")
    suspend fun getBookingById(bookingId: Long): BookingEntity?

    @Query("SELECT * FROM bookings WHERE booking_id = :bookingId")
    fun observeBookingById(bookingId: Long): Flow<BookingEntity?>

    @Query("SELECT * FROM booking_items WHERE booking_id = :bookingId ORDER BY booking_item_id ASC")
    fun getBookingItems(bookingId: Long): Flow<List<BookingItemEntity>>

    @Query("SELECT * FROM booking_items WHERE booking_id = :bookingId ORDER BY booking_item_id ASC")
    suspend fun getBookingItemsSync(bookingId: Long): List<BookingItemEntity>

    @Query("SELECT * FROM bookings WHERE customer_id = :customerId ORDER BY booking_date DESC")
    fun getCustomerBookings(customerId: Long): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE car_wash_id = :carWashId ORDER BY booking_date DESC")
    fun getCarWashBookings(carWashId: Long): Flow<List<BookingEntity>>

    @Query("UPDATE bookings SET status = :status, updated_at = :updatedAt WHERE booking_id = :bookingId")
    suspend fun updateBookingStatus(bookingId: Long, status: BookingStatus, updatedAt: Long)

    // --- Fees & Payments ---
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFee(fee: CarWashFeeEntity): Long

    @Query("SELECT * FROM car_wash_fees WHERE booking_id = :bookingId LIMIT 1")
    suspend fun getFeeByBookingId(bookingId: Long): CarWashFeeEntity?

    @Query("SELECT * FROM car_wash_fees WHERE car_wash_id = :carWashId ORDER BY created_at DESC")
    fun getCarWashFees(carWashId: Long): Flow<List<CarWashFeeEntity>>

    @Insert
    suspend fun insertPayment(payment: CarWashPaymentEntity): Long

    @Query("SELECT * FROM car_wash_payments WHERE car_wash_id = :carWashId ORDER BY date_of_payment DESC")
    fun getCarWashPayments(carWashId: Long): Flow<List<CarWashPaymentEntity>>

    // --- Transactions ---
    @Transaction
    suspend fun createBookingTransaction(
        booking: BookingEntity,
        itemsFactory: (Long) -> List<BookingItemEntity>
    ): Long {
        val bookingId = insertBooking(booking)
        val items = itemsFactory(bookingId)
        insertBookingItems(items)
        return bookingId
    }

    @Transaction
    suspend fun completeBookingTransaction(
        bookingId: Long,
        carWashId: Long,
        feeAmount: Double,
        now: Long
    ) {
        updateBookingStatus(bookingId, BookingStatus.COMPLETED, now)
        // Idempotent fee creation
        val existingFee = getFeeByBookingId(bookingId)
        if (existingFee == null) {
            insertFee(
                CarWashFeeEntity(
                    carWashId = carWashId,
                    bookingId = bookingId,
                    amount = feeAmount,
                    outstanding = true,
                    createdAt = now
                )
            )
        }
    }
}
