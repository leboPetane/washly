package com.example.ui.customer

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalCarWash
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.UberGreenPrimary

data class BrowseCategory(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val bgColor: Color,
    val accentColor: Color
)

@Composable
fun BrowseScreen(
    onSelectCategory: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf(
        BrowseCategory(
            title = "Full Body Washes",
            description = "Scratch-safe hand wash & dry",
            icon = Icons.Default.LocalCarWash,
            bgColor = Color(0xFFF0FDF4),
            accentColor = UberGreenPrimary
        ),
        BrowseCategory(
            title = "Snow Foam Cannon",
            description = "Thick pH-neutral pre-soak bath",
            icon = Icons.Default.WaterDrop,
            bgColor = Color(0xFFEFF6FF),
            accentColor = Color(0xFF2563EB)
        ),
        BrowseCategory(
            title = "Interior Vacuum",
            description = "Seats, carpets, boot & dash",
            icon = Icons.Outlined.CleaningServices,
            bgColor = Color(0xFFFEF3C7),
            accentColor = Color(0xFFD97706)
        ),
        BrowseCategory(
            title = "Tires & Rims Polish",
            description = "Brake dust removal & dressing",
            icon = Icons.Default.Build,
            bgColor = Color(0xFFF3E8FF),
            accentColor = Color(0xFF9333EA)
        ),
        BrowseCategory(
            title = "Ceramic Spray & Wax",
            description = "Hydrophobic 90-day mirror gloss",
            icon = Icons.Default.AutoAwesome,
            bgColor = Color(0xFFFFF1F2),
            accentColor = Color(0xFFE11D48)
        ),
        BrowseCategory(
            title = "All Mobile Detailers",
            description = "Browse all Cape Town wash pros",
            icon = Icons.Default.DirectionsCar,
            bgColor = Color(0xFFF3F4F6),
            accentColor = Color(0xFF111827)
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
            .testTag("browse_screen")
    ) {
        Text(
            text = "Explore Wash Services",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF111827)
        )
        Text(
            text = "Pick a wash category to find verified mobile pros",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF6B7280)
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(categories) { cat ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cat.bgColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onSelectCategory(cat.title) }
                        .testTag("browse_card_${cat.title}")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = cat.icon,
                                contentDescription = null,
                                tint = cat.accentColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Text(
                                text = cat.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF111827)
                            )
                            Text(
                                text = cat.description,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF6B7280),
                                fontSize = 10.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
