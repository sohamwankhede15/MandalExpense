package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MandalSettings
import com.example.ui.theme.OrangeBackground
import com.example.ui.theme.OrangeBorder
import com.example.ui.theme.OrangeContainer
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.OrangePrimaryDark
import com.example.ui.theme.TextOrange
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WhiteCard

data class DrawerItemData(
    val title: String,
    val subtext: String = "",
    val icon: ImageVector,
    val route: String,
    val badge: String = ""
)

@Composable
fun AppNavigationDrawerContent(
    currentRoute: String,
    settings: MandalSettings,
    onNavigate: (String) -> Unit,
    onCloseDrawer: () -> Unit,
    onLockClicked: () -> Unit,
    onAiAssistantClicked: () -> Unit,
    onReconciliationClicked: () -> Unit,
    onBackupClicked: () -> Unit,
    onMembersClicked: () -> Unit,
    onLogoutClicked: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier
            .width(310.dp)
            .fillMaxHeight(),
        drawerContainerColor = WhiteCard,
        drawerTonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        ) {
            // Header Banner with Logo and Mandal Details
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(OrangeContainer, WhiteCard)
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AkgmmLogo(
                            size = 52.dp,
                            showSubtext = false
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = OrangePrimaryDark
                                ) {
                                    Text(
                                        text = "AKGMM",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "२०२६",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OrangePrimaryDark
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "अखिल गणेशनगर",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "मित्र मंडळ, पुणे - २८",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = OrangePrimaryDark
                            )
                        }
                        IconButton(
                            onClick = onCloseDrawer,
                            modifier = Modifier.testTag("drawer_close_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "बंद करा",
                                tint = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "नोंदणी क्र: ${settings.registrationNumber}",
                        fontSize = 10.5.sp,
                        color = TextSecondary
                    )
                }
            }

            HorizontalDivider(color = OrangeBorder)

            // Section 1: मुख्य विभाग (Main Section)
            DrawerSectionHeader(title = "मुख्य विभाग")

            DrawerItemRow(
                title = "डॅशबोर्ड",
                icon = Icons.Default.Assessment,
                isSelected = currentRoute == "dashboard",
                onClick = {
                    onNavigate("dashboard")
                    onCloseDrawer()
                }
            )

            DrawerItemRow(
                title = "वर्गणी / पावती",
                subtext = "घरमालक, भाडेकरू, इतर",
                icon = Icons.Default.Receipt,
                isSelected = currentRoute == "vargani",
                onClick = {
                    onNavigate("vargani")
                    onCloseDrawer()
                }
            )

            DrawerItemRow(
                title = "मंडळ खर्च",
                subtext = "आगाऊ, अंतिम व मोफत खर्च",
                icon = Icons.Default.MoneyOff,
                isSelected = currentRoute == "expenses",
                onClick = {
                    onNavigate("expenses")
                    onCloseDrawer()
                }
            )

            DrawerItemRow(
                title = "महाप्रसाद व्यवस्थापन",
                subtext = "खर्च व प्रायोजक",
                icon = Icons.Default.Restaurant,
                isSelected = false,
                onClick = {
                    onNavigate("expenses")
                    onCloseDrawer()
                }
            )

            DrawerItemRow(
                title = "कार्यक्रमाचे वेळापत्रक",
                subtext = "उत्सव नियोजन व आरती यजमान",
                icon = Icons.Default.CalendarMonth,
                isSelected = currentRoute == "calendar",
                badge = "नवीन",
                onClick = {
                    onNavigate("calendar")
                    onCloseDrawer()
                }
            )

            DrawerItemRow(
                title = "दिवसाचा हिशोब",
                subtext = "मागील व चालू दिवसांचे क्लोजिंग",
                icon = Icons.Default.EventNote,
                isSelected = currentRoute == "daily_closing",
                badge = "दैनिक",
                onClick = {
                    onNavigate("daily_closing")
                    onCloseDrawer()
                }
            )

            DrawerItemRow(
                title = "शिल्लक वर्गणी",
                subtext = "फॉलो-अप व प्रलंबित यादी",
                icon = Icons.Default.HourglassBottom,
                isSelected = currentRoute == "pending",
                onClick = {
                    onNavigate("pending")
                    onCloseDrawer()
                }
            )

            DrawerItemRow(
                title = "अहवाल व ताळेबंद",
                subtext = "PDF, Excel व जमा-खर्च",
                icon = Icons.Default.Assessment,
                isSelected = currentRoute == "reports",
                onClick = {
                    onNavigate("reports")
                    onCloseDrawer()
                }
            )

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = OrangeBorder)

            // Section 2: व्यवस्थापन व ताळमेळ (Management)
            DrawerSectionHeader(title = "व्यवस्थापन व ताळमेळ")

            DrawerItemRow(
                title = "१५ कार्यकारिणी सदस्य",
                subtext = "अध्यक्ष, खजिनदार व पदाधिकारी",
                icon = Icons.Default.Groups,
                isSelected = false,
                onClick = {
                    onMembersClicked()
                    onCloseDrawer()
                }
            )

            DrawerItemRow(
                title = "रोख / UPI ताळमेळ",
                subtext = "कॅश इन हॅन्ड टॅली",
                icon = Icons.Default.Paid,
                isSelected = false,
                onClick = {
                    onReconciliationClicked()
                    onCloseDrawer()
                }
            )

            DrawerItemRow(
                title = "ऑडिट इतिहास",
                subtext = "नोंदींचे बदल व लॉग्स",
                icon = Icons.Default.History,
                isSelected = currentRoute == "audit_history",
                onClick = {
                    onNavigate("audit_history")
                    onCloseDrawer()
                }
            )

            DrawerItemRow(
                title = "डेटा बॅकअप व रिस्टोअर",
                subtext = "JSON बॅकअप सुरक्षित करा",
                icon = Icons.Default.Backup,
                isSelected = false,
                onClick = {
                    onBackupClicked()
                    onCloseDrawer()
                }
            )

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = OrangeBorder)

            // Section 3: साधने व सेटिंग्ज
            DrawerSectionHeader(title = "साधने व सेटिंग्ज")

            DrawerItemRow(
                title = "AI सहाय्यक",
                subtext = "स्मार्ट शोध व हिशोब मदत",
                icon = Icons.Default.AutoAwesome,
                isSelected = false,
                onClick = {
                    onAiAssistantClicked()
                    onCloseDrawer()
                }
            )

            DrawerItemRow(
                title = "मंडळ सेटिंग्ज",
                subtext = "नाव, पावती क्रमांक व वर्ष",
                icon = Icons.Default.Settings,
                isSelected = currentRoute == "settings",
                onClick = {
                    onNavigate("settings")
                    onCloseDrawer()
                }
            )

            DrawerItemRow(
                title = "अ‍ॅप लॉक / सुरक्षा",
                subtext = "पिन कोड सुरक्षा",
                icon = Icons.Default.Lock,
                isSelected = false,
                onClick = {
                    onLockClicked()
                    onCloseDrawer()
                }
            )

            DrawerItemRow(
                title = "लॉगआउट",
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                isSelected = false,
                isDanger = true,
                onClick = {
                    onLogoutClicked()
                    onCloseDrawer()
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DrawerSectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 11.5.sp,
        fontWeight = FontWeight.Bold,
        color = OrangePrimaryDark,
        modifier = Modifier.padding(start = 20.dp, top = 12.dp, bottom = 4.dp)
    )
}

@Composable
private fun DrawerItemRow(
    title: String,
    subtext: String = "",
    icon: ImageVector,
    isSelected: Boolean,
    badge: String = "",
    isDanger: Boolean = false,
    onClick: () -> Unit
) {
    val tint = when {
        isDanger -> Color(0xFFDC2626)
        isSelected -> OrangePrimaryDark
        else -> TextPrimary
    }

    val iconTint = when {
        isDanger -> Color(0xFFDC2626)
        isSelected -> OrangePrimaryDark
        else -> OrangePrimary
    }

    NavigationDrawerItem(
        label = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = tint
                    )
                    if (subtext.isNotBlank()) {
                        Text(
                            text = subtext,
                            fontSize = 10.5.sp,
                            color = TextSecondary
                        )
                    }
                }
                if (badge.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = OrangePrimaryDark
                    ) {
                        Text(
                            text = badge,
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
            }
        },
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
        },
        selected = isSelected,
        onClick = onClick,
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = OrangeContainer,
            unselectedContainerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
    )
}
