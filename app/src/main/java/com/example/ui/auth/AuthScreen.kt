package com.example.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ProfileType
import com.example.ui.theme.UberGreenPrimary
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    onLoginWithEmail: (role: ProfileType, email: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    // Default to page 0 (Customer)
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })

    // Customer Form State (Email & Password)
    var customerEmail by remember { mutableStateOf("lerato.moloi@gmail.com") }
    var customerPassword by remember { mutableStateOf("password123") }
    var showCustomerPassword by remember { mutableStateOf(false) }

    // Car Wash Form State (Email & Password)
    var partnerEmail by remember { mutableStateOf("sparkle.wash@washly.co.za") }
    var partnerPassword by remember { mutableStateOf("partner123") }
    var showPartnerPassword by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("auth_screen")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Section: Big Bold Brand "Washly" with Cartoon Graphic Hero
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Big "Washly" Title (Clean text only, no icon on left or right)
                Text(
                    text = "Washly",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Black,
                    fontSize = 40.sp,
                    color = Color(0xFF111827),
                    letterSpacing = (-1).sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "On-demand mobile car washing • We come to you",
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280),
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Professional Minimalist 2D Vector Graphic
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .height(150.dp)
                        .clip(RoundedCornerShape(20.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.washly_vector_art),
                        contentDescription = "Washly Vector Art",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // Middle Section: HorizontalPager with Customer / Business Login Forms
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("auth_horizontal_pager")
            ) { page ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))

                        if (page == 0) {
                            // CUSTOMER LOGIN VIEW
                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("customer_login_card")
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            color = Color(0xFFE6F9F0),
                                            shape = CircleShape,
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.DirectionsCar,
                                                    contentDescription = null,
                                                    tint = UberGreenPrimary,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Column {
                                            Text(
                                                text = "Vehicle Owner Sign In",
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color(0xFF111827)
                                            )
                                            Text(
                                                text = "Sign in to book a mobile detailing",
                                                fontSize = 13.sp,
                                                color = Color(0xFF6B7280)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(18.dp))

                                    // Email Field
                                    OutlinedTextField(
                                        value = customerEmail,
                                        onValueChange = { customerEmail = it },
                                        label = { Text("Email Address", fontSize = 14.sp) },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Email,
                                                contentDescription = null,
                                                tint = Color(0xFF6B7280)
                                            )
                                        },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedContainerColor = Color.White,
                                            unfocusedContainerColor = Color.White,
                                            focusedBorderColor = UberGreenPrimary,
                                            unfocusedBorderColor = Color(0xFFD1D5DB),
                                            focusedLabelColor = UberGreenPrimary
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("customer_email_input")
                                    )

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Password Field
                                    OutlinedTextField(
                                        value = customerPassword,
                                        onValueChange = { customerPassword = it },
                                        label = { Text("Password", fontSize = 14.sp) },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = null,
                                                tint = Color(0xFF6B7280)
                                            )
                                        },
                                        trailingIcon = {
                                            IconButton(onClick = { showCustomerPassword = !showCustomerPassword }) {
                                                Icon(
                                                    imageVector = if (showCustomerPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                    contentDescription = "Toggle password",
                                                    tint = Color(0xFF6B7280)
                                                )
                                            }
                                        },
                                        visualTransformation = if (showCustomerPassword) VisualTransformation.None else PasswordVisualTransformation(),
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedContainerColor = Color.White,
                                            unfocusedContainerColor = Color.White,
                                            focusedBorderColor = UberGreenPrimary,
                                            unfocusedBorderColor = Color(0xFFD1D5DB),
                                            focusedLabelColor = UberGreenPrimary
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("customer_password_input")
                                    )

                                    Spacer(modifier = Modifier.height(20.dp))

                                    // Sign In Button
                                    Button(
                                        onClick = {
                                            onLoginWithEmail(ProfileType.CUSTOMER, customerEmail)
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = UberGreenPrimary),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(54.dp)
                                            .testTag("customer_sign_in_button")
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = "Sign In as Customer",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 17.sp,
                                                color = Color.White
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            // CAR WASH BUSINESS LOGIN VIEW
                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("partner_login_card")
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            color = Color(0xFFE6F9F0),
                                            shape = CircleShape,
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.Business,
                                                    contentDescription = null,
                                                    tint = UberGreenPrimary,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Column {
                                            Text(
                                                text = "Car Wash Business Sign In",
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color(0xFF111827)
                                            )
                                            Text(
                                                text = "Manage incoming bookings & fleet",
                                                fontSize = 13.sp,
                                                color = Color(0xFF6B7280)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(18.dp))

                                    // Business Email Field
                                    OutlinedTextField(
                                        value = partnerEmail,
                                        onValueChange = { partnerEmail = it },
                                        label = { Text("Business Email Address", fontSize = 14.sp) },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Email,
                                                contentDescription = null,
                                                tint = Color(0xFF6B7280)
                                            )
                                        },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedContainerColor = Color.White,
                                            unfocusedContainerColor = Color.White,
                                            focusedBorderColor = UberGreenPrimary,
                                            unfocusedBorderColor = Color(0xFFD1D5DB),
                                            focusedLabelColor = UberGreenPrimary
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("partner_email_input")
                                    )

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Password Field
                                    OutlinedTextField(
                                        value = partnerPassword,
                                        onValueChange = { partnerPassword = it },
                                        label = { Text("Password", fontSize = 14.sp) },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = null,
                                                tint = Color(0xFF6B7280)
                                            )
                                        },
                                        trailingIcon = {
                                            IconButton(onClick = { showPartnerPassword = !showPartnerPassword }) {
                                                Icon(
                                                    imageVector = if (showPartnerPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                    contentDescription = "Toggle password",
                                                    tint = Color(0xFF6B7280)
                                                )
                                            }
                                        },
                                        visualTransformation = if (showPartnerPassword) VisualTransformation.None else PasswordVisualTransformation(),
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedContainerColor = Color.White,
                                            unfocusedContainerColor = Color.White,
                                            focusedBorderColor = UberGreenPrimary,
                                            unfocusedBorderColor = Color(0xFFD1D5DB),
                                            focusedLabelColor = UberGreenPrimary
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("partner_password_input")
                                    )

                                    Spacer(modifier = Modifier.height(20.dp))

                                    // Sign In Button
                                    Button(
                                        onClick = {
                                            onLoginWithEmail(ProfileType.CAR_WASH_OWNER, partnerEmail)
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = UberGreenPrimary),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(54.dp)
                                            .testTag("partner_sign_in_button")
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = "Sign In as Car Wash Pro",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 17.sp,
                                                color = Color.White
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            // Bottom Section: Segmented Profile Toggle at the bottom (as requested)
            Surface(
                color = Color.White,
                tonalElevation = 4.dp,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bottom_auth_switcher")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (pagerState.currentPage == 0)
                            "Are you a mobile detailing business?"
                        else
                            "Need your vehicle washed?",
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280),
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Bottom Toggle Pill
                    Surface(
                        color = Color(0xFFF3F4F6),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val isCustomer = pagerState.currentPage == 0
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (isCustomer) UberGreenPrimary else Color.Transparent)
                                    .clickable {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(0)
                                        }
                                    }
                                    .padding(vertical = 10.dp)
                                    .testTag("bottom_toggle_customer"),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.DirectionsCar,
                                        contentDescription = null,
                                        tint = if (isCustomer) Color.White else Color(0xFF6B7280),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Customer Login",
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCustomer) Color.White else Color(0xFF4B5563),
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            val isPartner = pagerState.currentPage == 1
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (isPartner) UberGreenPrimary else Color.Transparent)
                                    .clickable {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(1)
                                        }
                                    }
                                    .padding(vertical = 10.dp)
                                    .testTag("bottom_toggle_business"),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Business,
                                        contentDescription = null,
                                        tint = if (isPartner) Color.White else Color(0xFF6B7280),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Business Login",
                                        fontWeight = FontWeight.Bold,
                                        color = if (isPartner) Color.White else Color(0xFF4B5563),
                                        fontSize = 14.sp
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
