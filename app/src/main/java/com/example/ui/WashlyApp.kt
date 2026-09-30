package com.example.ui

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CustomerEntity
import com.example.data.model.ProfileType
import com.example.ui.auth.AuthScreen
import com.example.ui.components.UberBottomBar
import com.example.ui.components.UberTopBar
import com.example.ui.components.WashlyTopBar
import com.example.ui.customer.AccountScreen
import com.example.ui.customer.ActiveBookingTrackerScreen
import com.example.ui.customer.BookingCheckoutScreen
import com.example.ui.customer.BrowseScreen
import com.example.ui.customer.CarWashDetailScreen
import com.example.ui.customer.CustomerBookingsScreen
import com.example.ui.customer.CustomerHomeScreen
import com.example.ui.partner.PartnerDashboardScreen
import kotlinx.coroutines.flow.collectLatest

@Composable
fun WashlyApp(
    viewModel: WashlyViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Authentication & Profile State
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val currentProfile by viewModel.currentProfile.collectAsStateWithLifecycle()
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
    val activeBottomTab by viewModel.activeBottomTab.collectAsStateWithLifecycle()

    // Customer Navigation & Booking State
    val carWashes by viewModel.allCarWashes.collectAsStateWithLifecycle()
    val carWashesMap by viewModel.carWashesMap.collectAsStateWithLifecycle()
    val customer by viewModel.currentCustomer.collectAsStateWithLifecycle()
    val selectedCarWashWithServices by viewModel.selectedCarWashWithServices.collectAsStateWithLifecycle()
    val checkoutServiceIds by viewModel.checkoutServiceIds.collectAsStateWithLifecycle()
    val isSubmittingCheckout by viewModel.isSubmittingCheckout.collectAsStateWithLifecycle()
    val checkoutErrorMessage by viewModel.checkoutErrorMessage.collectAsStateWithLifecycle()
    val customerBookings by viewModel.customerBookings.collectAsStateWithLifecycle()
    val activeBookingsCount by viewModel.activeCustomerBookingsCount.collectAsStateWithLifecycle()
    val trackingBookingId by viewModel.trackingBookingId.collectAsStateWithLifecycle()
    val activeBookingDetail by viewModel.activeBookingDetail.collectAsStateWithLifecycle()

    // Partner State
    val partnerCarWash by viewModel.partnerCarWash.collectAsStateWithLifecycle()
    val partnerBookings by viewModel.partnerBookings.collectAsStateWithLifecycle()
    val partnerServices by viewModel.partnerServices.collectAsStateWithLifecycle()
    val partnerFees by viewModel.partnerFees.collectAsStateWithLifecycle()
    val partnerSummary by viewModel.partnerSummary.collectAsStateWithLifecycle()
    val partnerBookingItemsMap by viewModel.partnerBookingItemsMap.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // 1. Auth Flow: If not logged in, display full-screen Uber Eats style Login / Sign Up
    // Defaults to Customer login; swipe right (or tap bottom toggle) to log in as Car Wash business
    if (!isLoggedIn) {
        AuthScreen(
            onLoginWithEmail = { role, email ->
                viewModel.loginWithEmail(role, email)
            }
        )
        return
    }

    // 2. Logged-in Experience: Strictly separate Customer and Car Wash roles (No in-app switching)
    when (currentRole) {
        ProfileType.CUSTOMER -> {
            when {
                // Tracking a specific booking
                trackingBookingId != null -> {
                    BackHandler {
                        viewModel.closeTracking()
                    }
                    val detail = activeBookingDetail
                    if (detail != null) {
                        ActiveBookingTrackerScreen(
                            detail = detail,
                            onBack = { viewModel.closeTracking() },
                            onCallProvider = { phone ->
                                try {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                                    context.startActivity(intent)
                                } catch (_: Exception) {}
                            }
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize().background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF06C167))
                        }
                    }
                }

                // Checkout & Confirmation Screen
                checkoutServiceIds.isNotEmpty() && selectedCarWashWithServices != null -> {
                    BackHandler {
                        viewModel.backFromCheckout()
                    }
                    val carWash = selectedCarWashWithServices!!.carWash
                    val selectedServices = selectedCarWashWithServices!!.services.filter {
                        it.carWashServiceId in checkoutServiceIds
                    }
                    BookingCheckoutScreen(
                        carWash = carWash,
                        selectedServices = selectedServices,
                        customer = customer ?: CustomerEntity(
                            customerId = 1,
                            profileId = 1,
                            address = "42 Protea Road, Claremont, Cape Town",
                            defaultVehicle = "VW Polo TSI • White • CA 829-123"
                        ),
                        onBack = { viewModel.backFromCheckout() },
                        onConfirmBooking = { vehicle, address, notes ->
                            viewModel.confirmBooking(vehicle, address, notes)
                        },
                        isSubmitting = isSubmittingCheckout,
                        errorMessage = checkoutErrorMessage
                    )
                }

                // Car Wash Detail & Menu of Services
                selectedCarWashWithServices != null -> {
                    BackHandler {
                        viewModel.closeCarWashDetail()
                    }
                    CarWashDetailScreen(
                        data = selectedCarWashWithServices!!,
                        onBack = { viewModel.closeCarWashDetail() },
                        onProceedToCheckout = { serviceIds ->
                            viewModel.proceedToCheckout(serviceIds)
                        }
                    )
                }

                // Main Customer Marketplace Shell (Uber Eats Layout with 4 Tabs)
                else -> {
                    Scaffold(
                        modifier = modifier.fillMaxSize(),
                        containerColor = Color.White,
                        topBar = {
                            UberTopBar(
                                userName = currentProfile?.fullName ?: "Lerato Moloi"
                            )
                        },
                        bottomBar = {
                            UberBottomBar(
                                currentTab = activeBottomTab,
                                onTabSelected = { tab -> viewModel.setBottomTab(tab) },
                                activeOrdersCount = activeBookingsCount
                            )
                        },
                        snackbarHost = { SnackbarHost(snackbarHostState) }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .background(Color.White)
                        ) {
                            AnimatedContent(
                                targetState = activeBottomTab,
                                transitionSpec = { fadeIn() togetherWith fadeOut() },
                                label = "customer_tabs"
                            ) { tab ->
                                when (tab) {
                                    BottomNavTab.HOME -> {
                                        CustomerHomeScreen(
                                            carWashes = carWashes,
                                            onSelectCarWash = { carWashId ->
                                                viewModel.selectCarWash(carWashId)
                                            },
                                            userAddress = customer?.address ?: "42 Protea Road, Claremont, Cape Town"
                                        )
                                    }

                                    BottomNavTab.BROWSE -> {
                                        BrowseScreen(
                                            onSelectCategory = { _ ->
                                                viewModel.setBottomTab(BottomNavTab.HOME)
                                            }
                                        )
                                    }

                                    BottomNavTab.ORDERS -> {
                                        CustomerBookingsScreen(
                                            bookings = customerBookings,
                                            carWashes = carWashesMap,
                                            onSelectBooking = { bookingId ->
                                                viewModel.trackBooking(bookingId)
                                            },
                                            onBack = {
                                                viewModel.setBottomTab(BottomNavTab.HOME)
                                            }
                                        )
                                    }

                                    BottomNavTab.ACCOUNT -> {
                                        AccountScreen(
                                            currentProfile = currentProfile,
                                            customer = customer,
                                            onLogout = {
                                                viewModel.logout()
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        ProfileType.CAR_WASH_OWNER -> {
            Scaffold(
                modifier = modifier.fillMaxSize(),
                containerColor = Color.White,
                topBar = {
                    WashlyTopBar(
                        businessName = partnerCarWash?.businessName ?: "Partner Portal",
                        onLogout = { viewModel.logout() }
                    )
                },
                snackbarHost = { SnackbarHost(snackbarHostState) }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(Color.White)
                ) {
                    if (partnerCarWash != null) {
                        PartnerDashboardScreen(
                            carWash = partnerCarWash!!,
                            bookings = partnerBookings,
                            services = partnerServices,
                            fees = partnerFees,
                            summary = partnerSummary,
                            bookingItemsMap = partnerBookingItemsMap,
                            onToggleOnlineStatus = { status ->
                                viewModel.togglePartnerOnlineStatus(status)
                            },
                            onAcceptBooking = { bookingId ->
                                viewModel.acceptBooking(bookingId)
                            },
                            onCompleteBooking = { bookingId ->
                                viewModel.completeBooking(bookingId)
                            },
                            onUpdateServicePrice = { serviceId, newPrice ->
                                viewModel.updateServicePrice(serviceId, newPrice)
                            },
                            onToggleServiceActive = { serviceId, active ->
                                viewModel.toggleServiceActive(serviceId, active)
                            }
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF06C167))
                        }
                    }
                }
            }
        }
    }
}
