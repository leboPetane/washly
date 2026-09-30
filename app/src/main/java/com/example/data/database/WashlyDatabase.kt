package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.WashlyDao
import com.example.data.model.BookingEntity
import com.example.data.model.BookingItemEntity
import com.example.data.model.BookingStatus
import com.example.data.model.CarWashEntity
import com.example.data.model.CarWashFeeEntity
import com.example.data.model.CarWashPaymentEntity
import com.example.data.model.CarWashServiceEntity
import com.example.data.model.CarWashStatus
import com.example.data.model.CustomerEntity
import com.example.data.model.ProfileEntity
import com.example.data.model.ProfileType
import com.example.data.model.ServiceListEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ProfileEntity::class,
        CustomerEntity::class,
        CarWashEntity::class,
        ServiceListEntity::class,
        CarWashServiceEntity::class,
        BookingEntity::class,
        BookingItemEntity::class,
        CarWashPaymentEntity::class,
        CarWashFeeEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class WashlyDatabase : RoomDatabase() {

    abstract fun washlyDao(): WashlyDao

    companion object {
        @Volatile
        private var INSTANCE: WashlyDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): WashlyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WashlyDatabase::class.java,
                    "washly_marketplace.db"
                )
                    .addCallback(WashlyDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class WashlyDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database.washlyDao())
                }
            }
        }

        private suspend fun populateInitialData(dao: WashlyDao) {
            val now = System.currentTimeMillis()

            // 1. Profiles
            val profiles = listOf(
                ProfileEntity(
                    profileId = 1,
                    userId = "usr_cust_001",
                    profileType = ProfileType.CUSTOMER,
                    fullName = "Lerato Moloi",
                    phone = "+27 82 456 7890",
                    createdAt = now - 86400000L * 10
                ),
                ProfileEntity(
                    profileId = 2,
                    userId = "usr_wash_001",
                    profileType = ProfileType.CAR_WASH_OWNER,
                    fullName = "Kagiso Dlamini",
                    phone = "+27 71 889 1234",
                    createdAt = now - 86400000L * 20
                ),
                ProfileEntity(
                    profileId = 3,
                    userId = "usr_wash_002",
                    profileType = ProfileType.CAR_WASH_OWNER,
                    fullName = "Thabo Ndlovu",
                    phone = "+27 83 234 5678",
                    createdAt = now - 86400000L * 15
                ),
                ProfileEntity(
                    profileId = 4,
                    userId = "usr_wash_003",
                    profileType = ProfileType.CAR_WASH_OWNER,
                    fullName = "Sipho Sithole",
                    phone = "+27 76 998 4433",
                    createdAt = now - 86400000L * 12
                ),
                ProfileEntity(
                    profileId = 5,
                    userId = "usr_cust_002",
                    profileType = ProfileType.CUSTOMER,
                    fullName = "Sarah Jenkins",
                    phone = "+27 84 112 3344",
                    createdAt = now - 86400000L * 5
                )
            )
            dao.insertProfiles(profiles)

            // 2. Customers
            val customers = listOf(
                CustomerEntity(
                    customerId = 1,
                    profileId = 1,
                    address = "42 Protea Road, Claremont, Cape Town",
                    defaultVehicle = "VW Polo TSI • White • CA 829-123"
                ),
                CustomerEntity(
                    customerId = 2,
                    profileId = 5,
                    address = "18 Ocean View Drive, Camps Bay",
                    defaultVehicle = "Toyota RAV4 • Silver • CA 442-901"
                )
            )
            dao.insertCustomers(customers)

            // 3. Car Washes
            val carWashes = listOf(
                CarWashEntity(
                    carWashId = 1,
                    profileId = 2,
                    businessName = "SparkleMobile Pro Detailers",
                    description = "Cape Town's premier mobile eco-detailing team. We come directly to your driveway or office with onboard water & generator!",
                    phone = "+27 71 889 1234",
                    address = "Southern Suburbs & City Bowl",
                    latitude = "-33.9249",
                    longitude = "18.4241",
                    status = CarWashStatus.ONLINE,
                    rating = 4.9,
                    reviewCount = 86,
                    serviceRadiusKm = 20,
                    createdAt = now - 86400000L * 20,
                    updatedAt = now
                ),
                CarWashEntity(
                    carWashId = 2,
                    profileId = 3,
                    businessName = "AquaGlide Eco Mobile Wash",
                    description = "Specialized waterless & high-foaming mobile detailing. Ultra-gloss finish, scratch-free microfiber technique, 100% biodegradable.",
                    phone = "+27 83 234 5678",
                    address = "Atlantic Seaboard & Waterfront",
                    latitude = "-33.9050",
                    longitude = "18.4100",
                    status = CarWashStatus.ONLINE,
                    rating = 4.8,
                    reviewCount = 52,
                    serviceRadiusKm = 15,
                    createdAt = now - 86400000L * 15,
                    updatedAt = now
                ),
                CarWashEntity(
                    carWashId = 3,
                    profileId = 4,
                    businessName = "Apex Foam & Shine Mobile",
                    description = "Heavy duty snow foam cannon, deep extraction upholstery vacuuming, iron decontamination & paint glaze sealant.",
                    phone = "+27 76 998 4433",
                    address = "Northern Suburbs & Century City",
                    latitude = "-33.8900",
                    longitude = "18.5100",
                    status = CarWashStatus.OFFLINE,
                    rating = 4.7,
                    reviewCount = 41,
                    serviceRadiusKm = 25,
                    createdAt = now - 86400000L * 12,
                    updatedAt = now
                )
            )
            dao.insertCarWashes(carWashes)

            // 4. Master Service Catalog
            val services = listOf(
                ServiceListEntity(
                    serviceId = 1,
                    name = "Full Body Wash",
                    description = "Pre-rinse, two-bucket scratch-safe hand wash, wheel face wash, and microfiber dry.",
                    category = "Exterior",
                    iconName = "car_wash"
                ),
                ServiceListEntity(
                    serviceId = 2,
                    name = "Hoover & Vacuum",
                    description = "Complete interior carpets, seats, mats, boot vacuuming, and dashboard dust removal.",
                    category = "Interior",
                    iconName = "cleaning_services"
                ),
                ServiceListEntity(
                    serviceId = 3,
                    name = "Tire Wash",
                    description = "Basic alloy and tire wall pressure wash to rinse surface dirt and grime.",
                    category = "Wheels",
                    iconName = "tire_repair"
                ),
                ServiceListEntity(
                    serviceId = 4,
                    name = "Tire + Rim Wash & Polish",
                    description = "Deep wheel degreasing, brake dust iron remover, and wet-look protective tire dressing.",
                    category = "Wheels",
                    iconName = "tire_repair"
                ),
                ServiceListEntity(
                    serviceId = 5,
                    name = "Snow Foam Cannon Bath",
                    description = "Thick pH-neutral active foam pre-soak that gently loosens road film and grit.",
                    category = "Exterior",
                    iconName = "bubble_chart"
                ),
                ServiceListEntity(
                    serviceId = 6,
                    name = "Engine Bay Detailing",
                    description = "Careful low-pressure steam cleaning of engine bay components and satin dressing.",
                    category = "Specialist",
                    iconName = "precision_manufacturing"
                ),
                ServiceListEntity(
                    serviceId = 7,
                    name = "Ceramic Spray Shine & Wax",
                    description = "Hydrophobic synthetic ceramic sealant providing 90 days of water beading and mirror shine.",
                    category = "Exterior",
                    iconName = "auto_awesome"
                )
            )
            dao.insertMasterServices(services)

            // 5. Car Wash Service pricing
            val carWashServices = listOf(
                // SparkleMobile (id = 1)
                CarWashServiceEntity(carWashServiceId = 1, carWashId = 1, serviceId = 1, price = 150.0, active = true),
                CarWashServiceEntity(carWashServiceId = 2, carWashId = 1, serviceId = 2, price = 70.0, active = true),
                CarWashServiceEntity(carWashServiceId = 3, carWashId = 1, serviceId = 3, price = 50.0, active = true),
                CarWashServiceEntity(carWashServiceId = 4, carWashId = 1, serviceId = 4, price = 80.0, active = true),
                CarWashServiceEntity(carWashServiceId = 5, carWashId = 1, serviceId = 5, price = 60.0, active = true),
                CarWashServiceEntity(carWashServiceId = 6, carWashId = 1, serviceId = 6, price = 180.0, active = true),
                CarWashServiceEntity(carWashServiceId = 7, carWashId = 1, serviceId = 7, price = 120.0, active = true),

                // AquaGlide (id = 2)
                CarWashServiceEntity(carWashServiceId = 8, carWashId = 2, serviceId = 1, price = 140.0, active = true),
                CarWashServiceEntity(carWashServiceId = 9, carWashId = 2, serviceId = 2, price = 65.0, active = true),
                CarWashServiceEntity(carWashServiceId = 10, carWashId = 2, serviceId = 4, price = 75.0, active = true),
                CarWashServiceEntity(carWashServiceId = 11, carWashId = 2, serviceId = 7, price = 110.0, active = true),

                // Apex Foam & Shine (id = 3)
                CarWashServiceEntity(carWashServiceId = 12, carWashId = 3, serviceId = 1, price = 160.0, active = true),
                CarWashServiceEntity(carWashServiceId = 13, carWashId = 3, serviceId = 2, price = 80.0, active = true),
                CarWashServiceEntity(carWashServiceId = 14, carWashId = 3, serviceId = 4, price = 90.0, active = true),
                CarWashServiceEntity(carWashServiceId = 15, carWashId = 3, serviceId = 5, price = 70.0, active = true)
            )
            dao.insertCarWashServices(carWashServices)

            // 6. Sample Initial Completed Booking with fee
            val completedBooking = BookingEntity(
                bookingId = 1001,
                customerId = 1,
                carWashId = 1,
                totalPrice = 280.0,
                status = BookingStatus.COMPLETED,
                bookingDate = now - 3600000L * 4,
                vehicleDetails = "VW Polo TSI • White • CA 829-123",
                serviceAddress = "42 Protea Road, Claremont",
                customerNotes = "Parked in visitor bay near security gate.",
                createdAt = now - 3600000L * 6,
                updatedAt = now - 3600000L * 4
            )
            dao.insertBooking(completedBooking)

            val bookingItems = listOf(
                BookingItemEntity(bookingItemId = 1, bookingId = 1001, carWashServiceId = 1, serviceName = "Full Body Wash", price = 150.0, date = now - 3600000L * 6),
                BookingItemEntity(bookingItemId = 2, bookingId = 1001, carWashServiceId = 2, serviceName = "Hoover & Vacuum", price = 70.0, date = now - 3600000L * 6),
                BookingItemEntity(bookingItemId = 3, bookingId = 1001, carWashServiceId = 5, serviceName = "Snow Foam Cannon Bath", price = 60.0, date = now - 3600000L * 6)
            )
            dao.insertBookingItems(bookingItems)

            // Washly 4% transaction fee for booking 1001 (R280 * 0.04 = R11.20)
            dao.insertFee(
                CarWashFeeEntity(
                    feeId = 1,
                    carWashId = 1,
                    bookingId = 1001,
                    amount = 11.20,
                    outstanding = true,
                    createdAt = now - 3600000L * 4
                )
            )
            // Cash payment recorded
            dao.insertPayment(
                CarWashPaymentEntity(
                    paymentId = 1,
                    carWashId = 1,
                    bookingId = 1001,
                    totalPaid = 280.0,
                    dateOfPayment = now - 3600000L * 4
                )
            )
        }
    }
}
