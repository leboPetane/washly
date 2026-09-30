package com.example.data.repository

import com.example.data.dao.WashlyDao
import com.example.data.model.BookingDetail
import com.example.data.model.BookingEntity
import com.example.data.model.BookingItemEntity
import com.example.data.model.BookingStatus
import com.example.data.model.CarWashEntity
import com.example.data.model.CarWashFeeEntity
import com.example.data.model.CarWashPaymentEntity
import com.example.data.model.CarWashServiceDetail
import com.example.data.model.CarWashStatus
import com.example.data.model.CarWashWithServices
import com.example.data.model.CreateBookingRequest
import com.example.data.model.CustomerEntity
import com.example.data.model.PartnerEarningsSummary
import com.example.data.model.ProfileEntity
import com.example.data.model.ServiceListEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlin.math.round

class WashlyRepository(private val dao: WashlyDao) {

    companion object {
        // Spec Section 22: Centralized Washly transaction fee rate (4%)
        const val WASHLY_TRANSACTION_FEE_RATE = 0.04
    }

    // --- Profiles & Users ---
    val allProfiles: Flow<List<ProfileEntity>> = dao.getAllProfiles()
    val allMasterServices: Flow<List<ServiceListEntity>> = dao.getAllMasterServices()

    suspend fun getCustomerById(customerId: Long): CustomerEntity? = withContext(Dispatchers.IO) {
        dao.getCustomerById(customerId)
    }

    suspend fun getCustomerByProfileId(profileId: Long): CustomerEntity? = withContext(Dispatchers.IO) {
        dao.getCustomerByProfileId(profileId)
    }

    suspend fun getDefaultCustomer(): CustomerEntity? = withContext(Dispatchers.IO) {
        dao.getDefaultCustomer()
    }

    suspend fun getCarWashByProfileId(profileId: Long): CarWashEntity? = withContext(Dispatchers.IO) {
        dao.getCarWashByProfileId(profileId)
    }

    // --- Car Washes ---
    val allCarWashes: Flow<List<CarWashEntity>> = dao.getAllCarWashes()

    fun observeCarWash(carWashId: Long): Flow<CarWashEntity?> =
        dao.observeCarWashById(carWashId)

    fun getCarWashWithServices(carWashId: Long): Flow<CarWashWithServices?> {
        return combine(
            dao.observeCarWashById(carWashId),
            dao.getCarWashServices(carWashId)
        ) { carWash, services ->
            if (carWash == null) null
            else CarWashWithServices(carWash = carWash, services = services)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun setCarWashStatus(carWashId: Long, status: CarWashStatus): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val carWash = dao.getCarWashById(carWashId)
                ?: return@withContext Result.failure(WashlyException.NotFoundException("Car wash $carWashId not found."))
            dao.updateCarWashStatus(carWashId, status, System.currentTimeMillis())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateServicePrice(carWashServiceId: Long, newPrice: Double): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (newPrice < 0) {
                return@withContext Result.failure(IllegalArgumentException("Price must be positive"))
            }
            dao.updateServicePrice(carWashServiceId, newPrice, System.currentTimeMillis())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleServiceActive(carWashServiceId: Long, active: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            dao.toggleServiceActive(carWashServiceId, active, System.currentTimeMillis())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Bookings Workflow ---

    /**
     * Creates a new booking atomically according to Spec Sections 18 & 19:
     * 1. Verify customer exists
     * 2. Verify car wash exists
     * 3. Verify car wash is ONLINE (Section 12: Offline businesses cannot receive new bookings)
     * 4. Verify selected services belong to that car wash and are active
     * 5. Calculate total server-side
     * 6. Persist booking and items atomically
     */
    suspend fun createBooking(request: CreateBookingRequest): Result<Long> = withContext(Dispatchers.IO) {
        try {
            val customer = dao.getCustomerById(request.customerId)
                ?: return@withContext Result.failure(WashlyException.NotFoundException("Customer profile not found"))

            val carWash = dao.getCarWashById(request.carWashId)
                ?: return@withContext Result.failure(WashlyException.NotFoundException("Car wash not found"))

            if (carWash.status != CarWashStatus.ONLINE) {
                return@withContext Result.failure(
                    WashlyException.CarWashOfflineException(
                        "${carWash.businessName} is currently offline and not accepting new bookings."
                    )
                )
            }

            if (request.selectedCarWashServiceIds.isEmpty()) {
                return@withContext Result.failure(IllegalArgumentException("Please select at least one service"))
            }

            val services = dao.getCarWashServicesByIds(request.carWashId, request.selectedCarWashServiceIds)
            if (services.size != request.selectedCarWashServiceIds.distinct().size) {
                return@withContext Result.failure(
                    WashlyException.ServiceUnavailableException("Some selected services do not belong to this car wash")
                )
            }

            val inactiveServices = services.filter { !it.active }
            if (inactiveServices.isNotEmpty()) {
                return@withContext Result.failure(
                    WashlyException.ServiceUnavailableException("Service '${inactiveServices.first().name}' is currently unavailable")
                )
            }

            // Calculate total price server-side (do not trust client)
            val calculatedTotal = services.sumOf { it.price }
            val now = System.currentTimeMillis()

            val bookingEntity = BookingEntity(
                bookingId = 0,
                customerId = request.customerId,
                carWashId = request.carWashId,
                totalPrice = calculatedTotal,
                status = BookingStatus.PENDING,
                bookingDate = now,
                vehicleDetails = request.vehicleDetails.ifBlank { customer.defaultVehicle },
                serviceAddress = request.serviceAddress.ifBlank { customer.address },
                customerNotes = request.customerNotes,
                createdAt = now,
                updatedAt = now
            )

            val bookingId = dao.createBookingTransaction(bookingEntity) { generatedId ->
                services.map { service ->
                    BookingItemEntity(
                        bookingItemId = 0,
                        bookingId = generatedId,
                        carWashServiceId = service.carWashServiceId,
                        serviceName = service.name,
                        price = service.price, // Historical price snapshot (Section 16)
                        date = now
                    )
                }
            }

            Result.success(bookingId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Car-wash owner accepts a PENDING booking.
     * Enforces state machine: PENDING -> ACCEPTED
     */
    suspend fun acceptBooking(bookingId: Long, carWashId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val booking = dao.getBookingById(bookingId)
                ?: return@withContext Result.failure(WashlyException.NotFoundException("Booking $bookingId not found"))

            if (booking.carWashId != carWashId) {
                return@withContext Result.failure(
                    WashlyException.UnauthorizedException("Booking does not belong to your car wash")
                )
            }

            if (booking.status != BookingStatus.PENDING) {
                return@withContext Result.failure(
                    WashlyException.InvalidStatusTransitionException("Only PENDING bookings can be accepted. Current: ${booking.status}")
                )
            }

            dao.updateBookingStatus(bookingId, BookingStatus.ACCEPTED, System.currentTimeMillis())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Car-wash owner completes an ACCEPTED booking.
     * Enforces state machine: ACCEPTED -> COMPLETED
     * Calculates 4% Washly transaction fee and records it atomically.
     */
    suspend fun completeBooking(bookingId: Long, carWashId: Long): Result<Double> = withContext(Dispatchers.IO) {
        try {
            val booking = dao.getBookingById(bookingId)
                ?: return@withContext Result.failure(WashlyException.NotFoundException("Booking $bookingId not found"))

            if (booking.carWashId != carWashId) {
                return@withContext Result.failure(
                    WashlyException.UnauthorizedException("Booking does not belong to your car wash")
                )
            }

            if (booking.status != BookingStatus.ACCEPTED) {
                return@withContext Result.failure(
                    WashlyException.InvalidStatusTransitionException("Only ACCEPTED bookings can be marked as COMPLETED. Current: ${booking.status}")
                )
            }

            val now = System.currentTimeMillis()
            // 4% fee rounded to 2 decimal places
            val rawFee = booking.totalPrice * WASHLY_TRANSACTION_FEE_RATE
            val feeAmount = round(rawFee * 100.0) / 100.0

            dao.completeBookingTransaction(bookingId, carWashId, feeAmount, now)

            // Record cash payment
            dao.insertPayment(
                CarWashPaymentEntity(
                    paymentId = 0,
                    carWashId = carWashId,
                    bookingId = bookingId,
                    totalPaid = booking.totalPrice,
                    dateOfPayment = now
                )
            )

            Result.success(feeAmount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun observeBookingDetails(bookingId: Long): Flow<BookingDetail?> {
        return combine(
            dao.observeBookingById(bookingId),
            dao.getBookingItems(bookingId)
        ) { booking, items ->
            if (booking == null) return@combine null
            val carWash = dao.getCarWashById(booking.carWashId) ?: return@combine null
            val customer = dao.getCustomerById(booking.customerId) ?: return@combine null
            val fee = dao.getFeeByBookingId(bookingId)
            BookingDetail(
                booking = booking,
                carWash = carWash,
                customer = customer,
                items = items,
                fee = fee
            )
        }.flowOn(Dispatchers.IO)
    }

    fun getCustomerBookings(customerId: Long): Flow<List<BookingEntity>> =
        dao.getCustomerBookings(customerId)

    fun getCarWashBookings(carWashId: Long): Flow<List<BookingEntity>> =
        dao.getCarWashBookings(carWashId)

    suspend fun getBookingItems(bookingId: Long): List<BookingItemEntity> = withContext(Dispatchers.IO) {
        dao.getBookingItemsSync(bookingId)
    }

    fun getCarWashFees(carWashId: Long): Flow<List<CarWashFeeEntity>> =
        dao.getCarWashFees(carWashId)

    fun getPartnerEarningsSummary(carWashId: Long): Flow<PartnerEarningsSummary> {
        return combine(
            dao.getCarWashBookings(carWashId),
            dao.getCarWashFees(carWashId)
        ) { bookings, fees ->
            val completed = bookings.filter { it.status == BookingStatus.COMPLETED }
            val pending = bookings.filter { it.status == BookingStatus.PENDING }
            val accepted = bookings.filter { it.status == BookingStatus.ACCEPTED }

            val totalGross = completed.sumOf { it.totalPrice }
            val totalFees = fees.filter { it.outstanding }.sumOf { it.amount }
            val netEarnings = totalGross - totalFees

            PartnerEarningsSummary(
                totalBookingsCount = bookings.size,
                completedBookingsCount = completed.size,
                pendingBookingsCount = pending.size,
                acceptedBookingsCount = accepted.size,
                totalGrossCash = totalGross,
                totalWashlyFeeOwed = totalFees,
                netPartnerEarnings = netEarnings
            )
        }.flowOn(Dispatchers.IO)
    }
}
