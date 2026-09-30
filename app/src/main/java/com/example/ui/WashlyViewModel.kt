package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.BookingDetail
import com.example.data.model.BookingEntity
import com.example.data.model.BookingItemEntity
import com.example.data.model.BookingStatus
import com.example.data.model.CarWashEntity
import com.example.data.model.CarWashFeeEntity
import com.example.data.model.CarWashServiceDetail
import com.example.data.model.CarWashStatus
import com.example.data.model.CarWashWithServices
import com.example.data.model.CreateBookingRequest
import com.example.data.model.CustomerEntity
import com.example.data.model.PartnerEarningsSummary
import com.example.data.model.ProfileEntity
import com.example.data.model.ProfileType
import com.example.data.model.ServiceListEntity
import com.example.data.repository.WashlyException
import com.example.data.repository.WashlyRepository
import java.util.Locale
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class BottomNavTab {
    HOME,
    BROWSE,
    ORDERS,
    ACCOUNT
}

@OptIn(ExperimentalCoroutinesApi::class)
class WashlyViewModel(
    private val repository: WashlyRepository
) : ViewModel() {

    // Auth & Active Profile
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _currentProfile = MutableStateFlow<ProfileEntity?>(null)
    val currentProfile: StateFlow<ProfileEntity?> = _currentProfile.asStateFlow()

    val allProfiles: StateFlow<List<ProfileEntity>> = repository.allProfiles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMasterServices: StateFlow<List<ServiceListEntity>> = repository.allMasterServices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Bottom Navigation Tab
    private val _activeBottomTab = MutableStateFlow(BottomNavTab.HOME)
    val activeBottomTab: StateFlow<BottomNavTab> = _activeBottomTab.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    // Active persona
    private val _currentRole = MutableStateFlow(ProfileType.CUSTOMER)
    val currentRole: StateFlow<ProfileType> = _currentRole.asStateFlow()

    // Customer & Partner IDs
    private val _activeCustomerId = MutableStateFlow<Long>(1)
    val activeCustomerId: StateFlow<Long> = _activeCustomerId.asStateFlow()

    private val _activeCarWashId = MutableStateFlow<Long>(1)
    val activeCarWashId: StateFlow<Long> = _activeCarWashId.asStateFlow()

    // Navigation State
    private val _selectedCarWashId = MutableStateFlow<Long?>(null)
    val selectedCarWashId: StateFlow<Long?> = _selectedCarWashId.asStateFlow()

    private val _checkoutServiceIds = MutableStateFlow<List<Long>>(emptyList())
    val checkoutServiceIds: StateFlow<List<Long>> = _checkoutServiceIds.asStateFlow()

    private val _trackingBookingId = MutableStateFlow<Long?>(null)
    val trackingBookingId: StateFlow<Long?> = _trackingBookingId.asStateFlow()

    private val _showingCustomerBookings = MutableStateFlow(false)
    val showingCustomerBookings: StateFlow<Boolean> = _showingCustomerBookings.asStateFlow()

    // UI Feedback
    private val _isSubmittingCheckout = MutableStateFlow(false)
    val isSubmittingCheckout: StateFlow<Boolean> = _isSubmittingCheckout.asStateFlow()

    private val _checkoutErrorMessage = MutableStateFlow<String?>(null)
    val checkoutErrorMessage: StateFlow<String?> = _checkoutErrorMessage.asStateFlow()

    private val _uiEvents = MutableSharedFlow<String>()
    val uiEvents: SharedFlow<String> = _uiEvents.asSharedFlow()

    // Customer Data
    private val _customerEntity = MutableStateFlow<CustomerEntity?>(null)
    val currentCustomer: StateFlow<CustomerEntity?> = _customerEntity.asStateFlow()

    // Car Washes
    val allCarWashes: StateFlow<List<CarWashEntity>> = repository.allCarWashes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val carWashesMap: StateFlow<Map<Long, CarWashEntity>> = allCarWashes.combine(flowOf(Unit)) { list, _ ->
        list.associateBy { it.carWashId }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val selectedCarWashWithServices: StateFlow<CarWashWithServices?> = _selectedCarWashId.flatMapLatest { id ->
        if (id == null) flowOf(null)
        else repository.getCarWashWithServices(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Customer Bookings
    val customerBookings: StateFlow<List<BookingEntity>> = _activeCustomerId.flatMapLatest { id ->
        repository.getCustomerBookings(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeCustomerBookingsCount: StateFlow<Int> = customerBookings.combine(flowOf(Unit)) { list, _ ->
        list.count { it.status == BookingStatus.PENDING || it.status == BookingStatus.ACCEPTED }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val activeBookingDetail: StateFlow<BookingDetail?> = _trackingBookingId.flatMapLatest { id ->
        if (id == null) flowOf(null)
        else repository.observeBookingDetails(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Partner Data
    val partnerCarWash: StateFlow<CarWashEntity?> = _activeCarWashId.flatMapLatest { id ->
        repository.observeCarWash(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val partnerBookings: StateFlow<List<BookingEntity>> = _activeCarWashId.flatMapLatest { id ->
        repository.getCarWashBookings(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val partnerServices: StateFlow<List<CarWashServiceDetail>> = _activeCarWashId.flatMapLatest { id ->
        repository.getCarWashWithServices(id)
    }.combine(flowOf(Unit)) { detail, _ ->
        detail?.services ?: emptyList()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val partnerFees: StateFlow<List<CarWashFeeEntity>> = _activeCarWashId.flatMapLatest { id ->
        repository.getCarWashFees(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val partnerSummary: StateFlow<PartnerEarningsSummary> = _activeCarWashId.flatMapLatest { id ->
        repository.getPartnerEarningsSummary(id)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        PartnerEarningsSummary(0, 0, 0, 0, 0.0, 0.0, 0.0)
    )

    // Booking Items Cache for Partner view
    private val _partnerBookingItemsMap = MutableStateFlow<Map<Long, List<BookingItemEntity>>>(emptyMap())
    val partnerBookingItemsMap: StateFlow<Map<Long, List<BookingItemEntity>>> = _partnerBookingItemsMap.asStateFlow()

    init {
        viewModelScope.launch {
            val cust = repository.getDefaultCustomer()
            _customerEntity.value = cust ?: CustomerEntity(
                customerId = 1,
                profileId = 1,
                address = "42 Protea Road, Claremont, Cape Town",
                defaultVehicle = "VW Polo TSI • White • CA 829-123"
            )
        }
        viewModelScope.launch {
            allProfiles.collect { profiles ->
                if (_currentProfile.value == null && profiles.isNotEmpty()) {
                    _currentProfile.value = profiles.first()
                }
            }
        }
        viewModelScope.launch {
            partnerBookings.collect { bookingsList ->
                val map = mutableMapOf<Long, List<BookingItemEntity>>()
                bookingsList.forEach { booking ->
                    map[booking.bookingId] = repository.getBookingItems(booking.bookingId)
                }
                _partnerBookingItemsMap.value = map
            }
        }
    }

    // Auth & Profile Management
    fun loginWithProfile(profile: ProfileEntity) {
        _currentProfile.value = profile
        _currentRole.value = profile.profileType
        _isLoggedIn.value = true
        _selectedCarWashId.value = null
        _checkoutServiceIds.value = emptyList()
        _trackingBookingId.value = null
        _showingCustomerBookings.value = false
        _activeBottomTab.value = BottomNavTab.HOME

        viewModelScope.launch {
            if (profile.profileType == ProfileType.CUSTOMER) {
                val cust = repository.getCustomerByProfileId(profile.profileId)
                if (cust != null) {
                    _activeCustomerId.value = cust.customerId
                    _customerEntity.value = cust
                }
            } else {
                val wash = repository.getCarWashByProfileId(profile.profileId)
                if (wash != null) {
                    _activeCarWashId.value = wash.carWashId
                }
            }
            _uiEvents.emit("Signed in as ${profile.fullName}")
        }
    }

    fun loginWithEmail(role: ProfileType, email: String, password: String = "") {
        val matching = allProfiles.value.firstOrNull { it.profileType == role }
        if (matching != null) {
            loginWithProfile(matching)
        } else {
            _currentRole.value = role
            _isLoggedIn.value = true
        }
    }

    fun loginWithPhone(role: ProfileType, phone: String) {
        val existing = allProfiles.value.firstOrNull { it.phone == phone && it.profileType == role }
        if (existing != null) {
            loginWithProfile(existing)
        } else {
            val fallback = allProfiles.value.firstOrNull { it.profileType == role }
            if (fallback != null) {
                loginWithProfile(fallback)
            } else {
                _currentRole.value = role
                _isLoggedIn.value = true
            }
        }
    }

    fun logout() {
        _isLoggedIn.value = false
        _selectedCarWashId.value = null
        _checkoutServiceIds.value = emptyList()
        _trackingBookingId.value = null
        _showingCustomerBookings.value = false
        _activeBottomTab.value = BottomNavTab.HOME
    }

    // Role switcher
    fun setRole(role: ProfileType) {
        _currentRole.value = role
        // If switching role, find matching profile
        val matchingProfile = allProfiles.value.firstOrNull { it.profileType == role }
        if (matchingProfile != null) {
            _currentProfile.value = matchingProfile
            viewModelScope.launch {
                if (role == ProfileType.CUSTOMER) {
                    val cust = repository.getCustomerByProfileId(matchingProfile.profileId)
                    if (cust != null) {
                        _activeCustomerId.value = cust.customerId
                        _customerEntity.value = cust
                    }
                } else {
                    val wash = repository.getCarWashByProfileId(matchingProfile.profileId)
                    if (wash != null) {
                        _activeCarWashId.value = wash.carWashId
                    }
                }
            }
        }
        _selectedCarWashId.value = null
        _checkoutServiceIds.value = emptyList()
        _trackingBookingId.value = null
        _showingCustomerBookings.value = false
        _activeBottomTab.value = BottomNavTab.HOME
    }

    fun setBottomTab(tab: BottomNavTab) {
        _activeBottomTab.value = tab
        if (tab == BottomNavTab.HOME || tab == BottomNavTab.BROWSE) {
            _showingCustomerBookings.value = false
        } else if (tab == BottomNavTab.ORDERS) {
            _showingCustomerBookings.value = true
        }
    }

    fun selectCategoryFilter(category: String?) {
        _selectedCategory.value = category
    }

    // Customer Navigation Actions
    fun selectCarWash(carWashId: Long) {
        _selectedCarWashId.value = carWashId
        _checkoutServiceIds.value = emptyList()
        _checkoutErrorMessage.value = null
    }

    fun closeCarWashDetail() {
        _selectedCarWashId.value = null
        _checkoutServiceIds.value = emptyList()
    }

    fun proceedToCheckout(serviceIds: List<Long>) {
        _checkoutServiceIds.value = serviceIds
        _checkoutErrorMessage.value = null
    }

    fun backFromCheckout() {
        _checkoutServiceIds.value = emptyList()
        _checkoutErrorMessage.value = null
    }

    fun openCustomerBookings() {
        _showingCustomerBookings.value = true
        _activeBottomTab.value = BottomNavTab.ORDERS
    }

    fun closeCustomerBookings() {
        _showingCustomerBookings.value = false
        _activeBottomTab.value = BottomNavTab.HOME
    }

    fun trackBooking(bookingId: Long) {
        _trackingBookingId.value = bookingId
    }

    fun closeTracking() {
        _trackingBookingId.value = null
    }

    // Booking Creation
    fun confirmBooking(vehicleDetails: String, serviceAddress: String, customerNotes: String) {
        val carWashId = _selectedCarWashId.value ?: return
        val serviceIds = _checkoutServiceIds.value
        val custId = _activeCustomerId.value

        if (serviceIds.isEmpty()) return

        viewModelScope.launch {
            _isSubmittingCheckout.value = true
            _checkoutErrorMessage.value = null

            val result = repository.createBooking(
                CreateBookingRequest(
                    customerId = custId,
                    carWashId = carWashId,
                    selectedCarWashServiceIds = serviceIds,
                    vehicleDetails = vehicleDetails,
                    serviceAddress = serviceAddress,
                    customerNotes = customerNotes
                )
            )

            _isSubmittingCheckout.value = false

            result.onSuccess { newBookingId ->
                _checkoutServiceIds.value = emptyList()
                _selectedCarWashId.value = null
                _trackingBookingId.value = newBookingId
                _activeBottomTab.value = BottomNavTab.ORDERS
                _uiEvents.emit("Order #$newBookingId confirmed! Provider alerted.")
            }.onFailure { error ->
                _checkoutErrorMessage.value = error.message ?: "Failed to place booking"
            }
        }
    }

    // Partner Actions
    fun togglePartnerOnlineStatus(newStatus: CarWashStatus) {
        val carWashId = _activeCarWashId.value
        viewModelScope.launch {
            val result = repository.setCarWashStatus(carWashId, newStatus)
            result.onSuccess {
                val statusText = if (newStatus == CarWashStatus.ONLINE) "ONLINE" else "OFFLINE"
                _uiEvents.emit("Your business is now $statusText")
            }.onFailure {
                _uiEvents.emit("Could not update status: ${it.message}")
            }
        }
    }

    fun acceptBooking(bookingId: Long) {
        val carWashId = _activeCarWashId.value
        viewModelScope.launch {
            val result = repository.acceptBooking(bookingId, carWashId)
            result.onSuccess {
                _uiEvents.emit("Order #$bookingId accepted! On the way to customer.")
            }.onFailure {
                _uiEvents.emit("Error accepting booking: ${it.message}")
            }
        }
    }

    fun completeBooking(bookingId: Long) {
        val carWashId = _activeCarWashId.value
        viewModelScope.launch {
            val result = repository.completeBooking(bookingId, carWashId)
            result.onSuccess { fee ->
                _uiEvents.emit("Order #$bookingId marked completed! Washly 4% fee (R${String.format(Locale.US, "%.2f", fee)}) recorded.")
            }.onFailure {
                _uiEvents.emit("Error completing booking: ${it.message}")
            }
        }
    }

    fun updateServicePrice(serviceId: Long, newPrice: Double) {
        viewModelScope.launch {
            val result = repository.updateServicePrice(serviceId, newPrice)
            result.onSuccess {
                _uiEvents.emit("Price updated to R${String.format(Locale.US, "%.2f", newPrice)}")
            }.onFailure {
                _uiEvents.emit("Failed to update price: ${it.message}")
            }
        }
    }

    fun toggleServiceActive(serviceId: Long, active: Boolean) {
        viewModelScope.launch {
            val result = repository.toggleServiceActive(serviceId, active)
            result.onSuccess {
                _uiEvents.emit(if (active) "Service activated" else "Service paused")
            }.onFailure {
                _uiEvents.emit("Failed to update service: ${it.message}")
            }
        }
    }

    companion object {
        fun provideFactory(repository: WashlyRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return WashlyViewModel(repository) as T
                }
            }
    }
}
