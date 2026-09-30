package com.example.ui.components

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.BottomNavTab
import com.example.ui.theme.UberGreenPrimary

@Composable
fun UberBottomBar(
    currentTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit,
    activeOrdersCount: Int,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .navigationBarsPadding()
            .testTag("uber_bottom_nav_bar"),
        containerColor = Color.White,
        tonalElevation = 6.dp
    ) {
        // 1. Home
        NavigationBarItem(
            selected = currentTab == BottomNavTab.HOME,
            onClick = { onTabSelected(BottomNavTab.HOME) },
            icon = {
                Icon(
                    imageVector = if (currentTab == BottomNavTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Home",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = "Home",
                    fontSize = 11.sp,
                    fontWeight = if (currentTab == BottomNavTab.HOME) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = UberGreenPrimary,
                selectedTextColor = UberGreenPrimary,
                indicatorColor = Color(0xFFE6F9F0),
                unselectedIconColor = Color(0xFF6B7280),
                unselectedTextColor = Color(0xFF6B7280)
            ),
            modifier = Modifier.testTag("nav_tab_home")
        )

        // 2. Browse
        NavigationBarItem(
            selected = currentTab == BottomNavTab.BROWSE,
            onClick = { onTabSelected(BottomNavTab.BROWSE) },
            icon = {
                Icon(
                    imageVector = if (currentTab == BottomNavTab.BROWSE) Icons.Filled.Search else Icons.Outlined.Search,
                    contentDescription = "Browse",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = "Browse",
                    fontSize = 11.sp,
                    fontWeight = if (currentTab == BottomNavTab.BROWSE) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = UberGreenPrimary,
                selectedTextColor = UberGreenPrimary,
                indicatorColor = Color(0xFFE6F9F0),
                unselectedIconColor = Color(0xFF6B7280),
                unselectedTextColor = Color(0xFF6B7280)
            ),
            modifier = Modifier.testTag("nav_tab_browse")
        )

        // 3. Activity / Orders
        NavigationBarItem(
            selected = currentTab == BottomNavTab.ORDERS,
            onClick = { onTabSelected(BottomNavTab.ORDERS) },
            icon = {
                BadgedBox(
                    badge = {
                        if (activeOrdersCount > 0) {
                            Badge(
                                containerColor = UberGreenPrimary,
                                contentColor = Color.White
                            ) {
                                Text("$activeOrdersCount")
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (currentTab == BottomNavTab.ORDERS) Icons.Filled.ReceiptLong else Icons.Outlined.ReceiptLong,
                        contentDescription = "Orders",
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            label = {
                Text(
                    text = "Activity",
                    fontSize = 11.sp,
                    fontWeight = if (currentTab == BottomNavTab.ORDERS) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = UberGreenPrimary,
                selectedTextColor = UberGreenPrimary,
                indicatorColor = Color(0xFFE6F9F0),
                unselectedIconColor = Color(0xFF6B7280),
                unselectedTextColor = Color(0xFF6B7280)
            ),
            modifier = Modifier.testTag("nav_tab_orders")
        )

        // 4. Account
        NavigationBarItem(
            selected = currentTab == BottomNavTab.ACCOUNT,
            onClick = { onTabSelected(BottomNavTab.ACCOUNT) },
            icon = {
                Icon(
                    imageVector = if (currentTab == BottomNavTab.ACCOUNT) Icons.Filled.Person else Icons.Outlined.Person,
                    contentDescription = "Account",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = "Account",
                    fontSize = 11.sp,
                    fontWeight = if (currentTab == BottomNavTab.ACCOUNT) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = UberGreenPrimary,
                selectedTextColor = UberGreenPrimary,
                indicatorColor = Color(0xFFE6F9F0),
                unselectedIconColor = Color(0xFF6B7280),
                unselectedTextColor = Color(0xFF6B7280)
            ),
            modifier = Modifier.testTag("nav_tab_account")
        )
    }
}
