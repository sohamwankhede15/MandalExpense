package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.example.ui.theme.OrangeBackground
import com.example.ui.theme.OrangeBorder
import com.example.ui.theme.OrangeContainer
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.OrangePrimaryDark
import com.example.ui.theme.TextOrange
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WhiteCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    currentTitle: String,
    festivalYear: String,
    isAppLocked: Boolean,
    onMenuClicked: (() -> Unit)? = null,
    onLockClicked: () -> Unit,
    onAiAssistantClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
    onLogoutClicked: (() -> Unit)? = null
) {
    TopAppBar(
        navigationIcon = {
            if (onMenuClicked != null) {
                IconButton(
                    onClick = onMenuClicked,
                    modifier = Modifier.testTag("hamburger_menu_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "मेनू (Menu)",
                        tint = OrangePrimaryDark,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AkgmmLogo(
                    size = 38.dp,
                    showSubtext = false
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = OrangePrimaryDark
                        ) {
                            Text(
                                text = "AKGMM",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = currentTitle,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Text(
                        text = "अखिल गणेशनगर मित्र मंडळ • $festivalYear",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = OrangePrimaryDark
                    )
                }
            }
        },
        actions = {
            // AI Assistant Icon
            IconButton(
                onClick = onAiAssistantClicked,
                modifier = Modifier.testTag("ai_assistant_btn")
            ) {
                Surface(
                    shape = CircleShape,
                    color = OrangeContainer,
                    modifier = Modifier.size(34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI सहाय्यक",
                            tint = OrangePrimaryDark,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Lock / Unlock Icon
            IconButton(
                onClick = onLockClicked,
                modifier = Modifier.testTag("lock_toggle_btn")
            ) {
                Icon(
                    imageVector = if (isAppLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                    contentDescription = "सुरक्षा लॉक",
                    tint = if (isAppLocked) OrangePrimaryDark else TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Settings Icon
            IconButton(
                onClick = onSettingsClicked,
                modifier = Modifier.testTag("settings_nav_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "सेटिंग्ज",
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Logout Icon
            if (onLogoutClicked != null) {
                IconButton(
                    onClick = onLogoutClicked,
                    modifier = Modifier.testTag("logout_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "लॉगआउट (Logout)",
                        tint = Color(0xFFDC2626),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = WhiteCard,
            titleContentColor = TextPrimary
        )
    )
}

data class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun AppBottomNavigationBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem("डॅशबोर्ड", Icons.Default.Assessment, "dashboard"),
        BottomNavItem("वर्गणी", Icons.Default.Receipt, "vargani"),
        BottomNavItem("मंडळ खर्च", Icons.Default.MoneyOff, "expenses"),
        BottomNavItem("शिल्लक", Icons.Default.HourglassBottom, "pending"),
        BottomNavItem("अहवाल", Icons.Default.Assessment, "reports")
    )

    NavigationBar(
        containerColor = WhiteCard,
        tonalElevation = 6.dp
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = OrangePrimaryDark,
                    selectedTextColor = OrangePrimaryDark,
                    indicatorColor = OrangeContainer,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary
                )
            )
        }
    }
}
