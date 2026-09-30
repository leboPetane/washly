package com.example.ui.partner

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BookingEntity
import com.example.data.model.BookingItemEntity
import com.example.data.model.BookingStatus
import com.example.data.model.CarWashEntity
import com.example.data.model.CarWashFeeEntity
import com.example.data.model.CarWashServiceDetail
import com.example.data.model.CarWashStatus
import com.example.data.model.CustomerEntity
import com.example.data.model.PartnerEarningsSummary
import com.example.ui.components.BookingStatusBadge
import com.example.ui.components.FormattedPrice
import com.example.ui.components.OnlineStatusBadge
import com.example.ui.components.getServiceIcon
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PartnerDashboardScreen(
    carWash: CarWashEntity,
    bookings: List<BookingEntity>,
    services: List<CarWashServiceDetail>,
    fees: List<CarWashFeeEntity>,
    summary: PartnerEarningsSummary,
    bookingItemsMap: Map<Long, List<BookingItemEntity>>,
    onToggleOnlineStatus: (CarWashStatus) -> Unit,
    onAcceptBooking: (Long) -> Unit,
    onCompleteBooking: (Long) -> Unit,
    onUpdateServicePrice: (carWashServiceId: Long, newPrice: Double) -> Unit,
    onToggleServiceActive: (carWashServiceId: Long, active: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPartnerTab by remember { mutableIntStateOf(0) }
    val partnerTabs = listOf("Requests", "Services & Prices", "Washly Fees")

    val isOnline = carWash.status == CarWashStatus.ONLINE

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .testTag("partner_dashboard_screen")
    ) {
        // Partner Hero / Status Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isOnline) Color(0xFFF0FDF4) else Color(0xFFF8FAFC)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = carWash.businessName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${carWash.address} • ${carWash.phone}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Online Toggle Switch
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        OnlineStatusBadge(status = carWash.status)
                        Spacer(modifier = Modifier.height(4.dp))
                        Switch(
                            checked = isOnline,
                            onCheckedChange = { checked ->
                                val newStatus = if (checked) CarWashStatus.ONLINE else CarWashStatus.OFFLINE
                                onToggleOnlineStatus(newStatus)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF10B981)
                            ),
                            modifier = Modifier.testTag("online_offline_switch")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Explanatory notice on offline behavior (Spec Section 12)
                Surface(
                    color = if (isOnline) Color(0xFFDCFCE7) else Color(0xFFE2E8F0),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = if (isOnline) Color(0xFF166534) else Color(0xFF475569),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isOnline) "You are ONLINE and accepting new booking requests."
                            else "You are OFFLINE. New bookings are paused. Ongoing bookings continue normally.",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isOnline) Color(0xFF166534) else Color(0xFF475569)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Financial Metrics Grid (Spec Section 3, 21: 4% Fee & Cash Bookkeeping)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricMiniCard(
                        title = "Gross Cash",
                        amount = summary.totalGrossCash,
                        subtitle = "${summary.completedBookingsCount} done",
                        icon = Icons.Default.LocalAtm,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )

                    MetricMiniCard(
                        title = "Washly 4% Fee",
                        amount = summary.totalWashlyFeeOwed,
                        subtitle = "Owed to platform",
                        icon = Icons.Default.Percent,
                        containerColor = Color(0xFFFEF3C7),
                        contentColor = Color(0xFF92400E),
                        modifier = Modifier.weight(1f)
                    )

                    MetricMiniCard(
                        title = "Net Payout",
                        amount = summary.netPartnerEarnings,
                        subtitle = "Your take-home",
                        icon = Icons.Default.AccountBalanceWallet,
                        containerColor = Color(0xFFECFDF5),
                        contentColor = Color(0xFF065F46),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Sub Tabs
        TabRow(
            selectedTabIndex = selectedPartnerTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            partnerTabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedPartnerTab == index,
                    onClick = { selectedPartnerTab = index },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = title,
                                fontWeight = if (selectedPartnerTab == index) FontWeight.Bold else FontWeight.Medium
                            )
                            if (index == 0 && summary.pendingBookingsCount > 0) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Surface(
                                    color = Color(0xFFF59E0B),
                                    shape = CircleShape
                                ) {
                                    Text(
                                        text = "${summary.pendingBookingsCount}",
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                    },
                    modifier = Modifier.testTag("partner_tab_$index")
                )
            }
        }

        // Tab Content
        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedPartnerTab) {
                0 -> PartnerBookingsTab(
                    bookings = bookings,
                    bookingItemsMap = bookingItemsMap,
                    onAccept = onAcceptBooking,
                    onComplete = onCompleteBooking
                )
                1 -> PartnerServicesTab(
                    services = services,
                    onUpdatePrice = onUpdateServicePrice,
                    onToggleActive = onToggleServiceActive
                )
                2 -> PartnerFeesTab(
                    fees = fees,
                    summary = summary
                )
            }
        }
    }
}

@Composable
fun MetricMiniCard(
    title: String,
    amount: Double,
    subtitle: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = containerColor,
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 1.dp,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            FormattedPrice(
                price = amount,
                style = MaterialTheme.typography.titleSmall,
                color = contentColor
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = contentColor.copy(alpha = 0.8f),
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun PartnerBookingsTab(
    bookings: List<BookingEntity>,
    bookingItemsMap: Map<Long, List<BookingItemEntity>>,
    onAccept: (Long) -> Unit,
    onComplete: (Long) -> Unit
) {
    var statusFilter by remember { mutableStateOf<BookingStatus?>(null) }

    val filtered = if (statusFilter == null) bookings else bookings.filter { it.status == statusFilter }

    val dateFormatter = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())

    Column(modifier = Modifier.fillMaxSize()) {
        // Quick sub-filter chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterTextChip(
                label = "All (${bookings.size})",
                selected = statusFilter == null,
                onClick = { statusFilter = null },
                modifier = Modifier.testTag("filter_partner_all")
            )
            FilterTextChip(
                label = "Pending (${bookings.count { it.status == BookingStatus.PENDING }})",
                selected = statusFilter == BookingStatus.PENDING,
                onClick = { statusFilter = BookingStatus.PENDING },
                modifier = Modifier.testTag("filter_partner_pending")
            )
            FilterTextChip(
                label = "Accepted (${bookings.count { it.status == BookingStatus.ACCEPTED }})",
                selected = statusFilter == BookingStatus.ACCEPTED,
                onClick = { statusFilter = BookingStatus.ACCEPTED },
                modifier = Modifier.testTag("filter_partner_accepted")
            )
            FilterTextChip(
                label = "Done (${bookings.count { it.status == BookingStatus.COMPLETED }})",
                selected = statusFilter == BookingStatus.COMPLETED,
                onClick = { statusFilter = BookingStatus.COMPLETED },
                modifier = Modifier.testTag("filter_partner_completed")
            )
        }

        if (filtered.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No bookings match this filter.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filtered, key = { it.bookingId }) { booking ->
                    val items = bookingItemsMap[booking.bookingId] ?: emptyList()
                    val dateStr = dateFormatter.format(Date(booking.bookingDate))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("partner_booking_card_${booking.bookingId}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Booking #${booking.bookingId}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = dateStr,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                                BookingStatusBadge(status = booking.status)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Vehicle: ${booking.vehicleDetails}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Address: ${booking.serviceAddress}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            if (booking.customerNotes.isNotBlank()) {
                                Text(
                                    text = "Notes: \"${booking.customerNotes}\"",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Items preview
                            if (items.isNotEmpty()) {
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        items.forEach { item ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = "• ${item.serviceName}",
                                                    style = MaterialTheme.typography.labelSmall
                                                )
                                                FormattedPrice(
                                                    price = item.price,
                                                    style = MaterialTheme.typography.labelSmall
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Total Cash to Collect:",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    FormattedPrice(
                                        price = booking.totalPrice,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }

                                // State Machine Transition Actions (Spec Sections 17 & 23)
                                when (booking.status) {
                                    BookingStatus.PENDING -> {
                                        Button(
                                            onClick = { onAccept(booking.bookingId) },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary
                                            ),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.testTag("accept_booking_btn_${booking.bookingId}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Accept Booking")
                                        }
                                    }
                                    BookingStatus.ACCEPTED -> {
                                        Button(
                                            onClick = { onComplete(booking.bookingId) },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFF10B981)
                                            ),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.testTag("complete_booking_btn_${booking.bookingId}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Mark Completed")
                                        }
                                    }
                                    BookingStatus.COMPLETED -> {
                                        val fee = booking.totalPrice * 0.04
                                        Surface(
                                            color = Color(0xFFFEF3C7),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = "Fee: R${String.format(Locale.US, "%.2f", fee)} (4%)",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color(0xFF92400E),
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FilterTextChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        shape = CircleShape,
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun PartnerServicesTab(
    services: List<CarWashServiceDetail>,
    onUpdatePrice: (Long, Double) -> Unit,
    onToggleActive: (Long, Boolean) -> Unit
) {
    var editingService by remember { mutableStateOf<CarWashServiceDetail?>(null) }
    var newPriceText by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "Service Catalog & Custom Pricing",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Set your own prices and activate or pause services you offer.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        items(services, key = { it.carWashServiceId }) { service ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("partner_service_card_${service.carWashServiceId}"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getServiceIcon(service.iconName),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = service.name,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        FormattedPrice(price = service.price, style = MaterialTheme.typography.bodyMedium)
                    }

                    // Edit Price Button
                    IconButton(
                        onClick = {
                            editingService = service
                            newPriceText = service.price.toInt().toString()
                        },
                        modifier = Modifier.testTag("edit_price_btn_${service.carWashServiceId}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Price",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Active Switch
                    Switch(
                        checked = service.active,
                        onCheckedChange = { active ->
                            onToggleActive(service.carWashServiceId, active)
                        },
                        modifier = Modifier.testTag("toggle_service_active_${service.carWashServiceId}")
                    )
                }
            }
        }
    }

    // Price Edit Dialog
    editingService?.let { service ->
        AlertDialog(
            onDismissRequest = { editingService = null },
            title = { Text("Update Price: ${service.name}") },
            text = {
                Column {
                    Text("Enter new price in South African Rand (R):")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPriceText,
                        onValueChange = { newPriceText = it },
                        label = { Text("Price (R)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.testTag("price_input_dialog")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsed = newPriceText.toDoubleOrNull()
                        if (parsed != null && parsed >= 0) {
                            onUpdatePrice(service.carWashServiceId, parsed)
                            editingService = null
                        }
                    },
                    modifier = Modifier.testTag("save_price_btn")
                ) {
                    Text("Save Price")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingService = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun PartnerFeesTab(
    fees: List<CarWashFeeEntity>,
    summary: PartnerEarningsSummary
) {
    val dateFormatter = SimpleDateFormat("dd MMM yyyy • HH:mm", Locale.getDefault())

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Washly Marketplace 4% Fee Policy",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF92400E)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Washly charges a 4% transaction fee per completed booking. Fees are automatically recorded upon job completion for internal accounting.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFB45309),
                        lineHeight = 18.sp
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = Color(0xFFFDE68A)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total Outstanding Platform Fees:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF92400E)
                        )
                        FormattedPrice(
                            price = summary.totalWashlyFeeOwed,
                            color = Color(0xFF92400E),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "Fee Records (${fees.size})",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }

        if (fees.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No fee records yet. Fees appear when bookings are completed.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(fees, key = { it.feeId }) { fee ->
                val dateStr = dateFormatter.format(Date(fee.createdAt))

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Booking #${fee.bookingId} (4% Fee)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = dateStr,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            FormattedPrice(price = fee.amount, color = Color(0xFFB45309))
                            Surface(
                                color = if (fee.outstanding) Color(0xFFFEE2E2) else Color(0xFFDCFCE7),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = if (fee.outstanding) "Outstanding" else "Settled",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (fee.outstanding) Color(0xFF991B1B) else Color(0xFF166534),
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
