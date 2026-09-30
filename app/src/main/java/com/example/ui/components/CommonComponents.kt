package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocalCarWash
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BookingStatus
import com.example.data.model.CarWashStatus
import com.example.ui.theme.StatusAcceptedBlue
import com.example.ui.theme.StatusCompletedGreen
import com.example.ui.theme.StatusOfflineContainer
import com.example.ui.theme.StatusOfflineGrey
import com.example.ui.theme.StatusOnlineContainer
import com.example.ui.theme.StatusOnlineGreen
import com.example.ui.theme.StatusPendingOrange
import java.util.Locale

@Composable
fun OnlineStatusBadge(
    status: CarWashStatus,
    modifier: Modifier = Modifier
) {
    val isOnline = status == CarWashStatus.ONLINE
    val bgColor by animateColorAsState(
        targetValue = if (isOnline) StatusOnlineContainer else StatusOfflineContainer,
        label = "bg"
    )
    val textColor = if (isOnline) StatusOnlineGreen else StatusOfflineGrey

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.testTag("status_badge_${status.name.lowercase(Locale.ROOT)}")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(textColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isOnline) "ONLINE" else "OFFLINE",
                style = MaterialTheme.typography.labelSmall,
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun BookingStatusBadge(
    status: BookingStatus,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, text, icon) = when (status) {
        BookingStatus.PENDING -> Quad(
            Color(0xFFFEF3C7),
            StatusPendingOrange,
            "Requested (Pending)",
            Icons.Default.HourglassTop
        )
        BookingStatus.ACCEPTED -> Quad(
            Color(0xFFDBEAFE),
            StatusAcceptedBlue,
            "Accepted (In Progress)",
            Icons.Outlined.Timer
        )
        BookingStatus.COMPLETED -> Quad(
            Color(0xFFD1FAE5),
            StatusCompletedGreen,
            "Completed",
            Icons.Default.CheckCircle
        )
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.testTag("booking_status_${status.name.lowercase(Locale.ROOT)}")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = textColor,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun RatingBadge(
    rating: Double,
    reviewCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Rating",
            tint = Color(0xFFF59E0B),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = String.format(Locale.US, "%.1f", rating),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = "($reviewCount)",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun FormattedPrice(
    price: Double,
    modifier: Modifier = Modifier,
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.titleMedium,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Text(
        text = "R${String.format(Locale.US, "%.2f", price)}",
        style = style,
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = modifier.testTag("price_tag")
    )
}

fun getServiceIcon(iconName: String): ImageVector {
    return when (iconName) {
        "car_wash" -> Icons.Default.LocalCarWash
        "cleaning_services" -> Icons.Outlined.CleaningServices
        "bubble_chart" -> Icons.Default.WaterDrop
        "precision_manufacturing" -> Icons.Default.Build
        "auto_awesome" -> Icons.Default.AutoAwesome
        else -> Icons.Default.DirectionsCar
    }
}
