package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MandalSettings
import com.example.data.model.VarganiTransaction
import com.example.ui.theme.OrangeBackground
import com.example.ui.theme.OrangeBorder
import com.example.ui.theme.OrangeContainer
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.OrangePrimaryDark
import com.example.ui.theme.RedExpense
import com.example.ui.theme.TextOrange
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WhiteCard
import com.example.util.DateUtils
import com.example.util.IndianCurrencyFormatter
import com.example.util.NumberToWordsConverter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PavtiDetailSheet(
    vargani: VarganiTransaction,
    settings: MandalSettings,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onShareWhatsApp: (VarganiTransaction) -> Unit,
    onExportPdf: (VarganiTransaction) -> Unit,
    onDelete: (VarganiTransaction) -> Unit,
    onEdit: (VarganiTransaction) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFFFCFDFD),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .padding(bottom = 28.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Sheet Header with Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AKGMM डिजिटल ई-पावती",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = OrangePrimaryDark
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "बंद करा")
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // THE DIGITAL ORNAMENTAL PAVTI CARD
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(OrangePrimaryDark, OrangePrimary, OrangeBorder)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = WhiteCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // AKGMM Logo Header
                    AkgmmLogo(
                        size = 64.dp,
                        showSubtext = false
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = OrangePrimaryDark
                    ) {
                        Text(
                            text = "AKGMM",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Pavti Title & Mandal Name
                    Text(
                        text = "वर्गणी पावती",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFBF360C),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "अखिल गणेशनगर मित्र मंडळ",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "गणेशनगर हडपसर, पुणे - ४११०६०",
                        fontSize = 11.5.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "महोत्सव वर्ष: ${settings.festivalYear} | नोंदणी क्र.: ${settings.registrationNumber}",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OrangePrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(thickness = 1.dp, color = OrangeBorder)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Pavti Number & Date
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "पावती क्र.: ${vargani.pavtiNumber}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = OrangePrimaryDark
                        )
                        Text(
                            text = "दिनांक: ${DateUtils.formatDateTime(vargani.timestamp)}",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Contributor Info Box
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(OrangeContainer.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .border(1.dp, OrangeBorder, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        ReceiptDetailRow(
                            label = "व्यक्तीचा प्रकार",
                            value = when {
                                vargani.isOwner -> "🏠 घरमालक (Owner)"
                                vargani.isTenant -> "🏢 भाडेकरू (Tenant)"
                                else -> "⭐ इतर (Other - ${vargani.displayPersonType})"
                            },
                            isBold = true
                        )

                        ReceiptDetailRow(
                            label = when {
                                vargani.isOwner -> "घरमालकाचे नाव"
                                vargani.isTenant -> "भाडेकरूचे नाव"
                                else -> "नाव"
                            },
                            value = vargani.contributorName,
                            isBold = true
                        )

                        if (vargani.isTenant && vargani.ownerName.isNotBlank()) {
                            ReceiptDetailRow(
                                label = "संबंधित घरमालक",
                                value = vargani.ownerName,
                                isBold = true
                            )
                        }

                        if (vargani.isOther && vargani.customCategoryName.isNotBlank()) {
                            ReceiptDetailRow(
                                label = "प्रवर्ग / पद",
                                value = vargani.customCategoryName,
                                isBold = true
                            )
                        }

                        if (vargani.mobileNumber.isNotBlank()) {
                            ReceiptDetailRow(label = "मोबाईल क्रमांक", value = vargani.mobileNumber)
                        }
                        if (vargani.address.isNotBlank()) {
                            ReceiptDetailRow(label = "पत्ता / विभाग", value = vargani.address)
                        }
                        ReceiptDetailRow(label = "पेमेंट पद्धत", value = vargani.paymentMode)
                        if (vargani.notes.isNotBlank()) {
                            ReceiptDetailRow(label = "टीप / संदर्भ", value = vargani.notes)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Large Amount Highlight
                    Surface(
                        color = OrangeContainer,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, OrangePrimary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "स्वीकारलेली वर्गणी रक्कम",
                                fontSize = 11.5.sp,
                                color = OrangePrimaryDark,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = IndianCurrencyFormatter.formatRupees(vargani.amount),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = OrangePrimaryDark
                            )
                            val words = if (vargani.amountInWords.isNotBlank()) {
                                vargani.amountInWords
                            } else {
                                NumberToWordsConverter.convertToMarathi(vargani.amount)
                            }
                            Text(
                                text = "($words)",
                                fontSize = 11.5.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Signatures & Collector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (vargani.collectedBy.isNotBlank()) vargani.collectedBy else "प्रतिनिधी",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "स्वीकारणारा / पावती देणारा",
                                fontSize = 9.5.sp,
                                color = TextSecondary
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = settings.authorizedSignatory,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "अधिकृत स्वाक्षरी",
                                fontSize = 9.5.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "🚩 ॥ गणपती बाप्पा मोर्या! पुढच्या वर्षी लवकर या! ॥ 🌺",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = OrangePrimaryDark,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons Row (WhatsApp, PDF)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // WhatsApp Button
                Button(
                    onClick = { onShareWhatsApp(vargani) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("share_whatsapp_receipt_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "WhatsApp",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "WhatsApp", fontWeight = FontWeight.Bold)
                }

                // PDF Download Button
                Button(
                    onClick = { onExportPdf(vargani) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("export_pdf_receipt_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimaryDark),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = "PDF",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "PDF पावती", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Secondary Buttons (बदल करा, हटवा)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { onEdit(vargani) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "बदल करा", modifier = Modifier.size(16.dp), tint = TextSecondary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "बदल करा", color = TextPrimary)
                }

                OutlinedButton(
                    onClick = { onDelete(vargani) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = RedExpense),
                    border = BorderStroke(1.dp, RedExpense.copy(alpha = 0.5f))
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "हटवा", modifier = Modifier.size(16.dp), tint = RedExpense)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "हटवा", color = RedExpense)
                }
            }
        }
    }
}

@Composable
fun ReceiptDetailRow(label: String, value: String, isBold: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label:",
            fontSize = 11.5.sp,
            color = TextSecondary,
            modifier = Modifier.width(115.dp)
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            color = TextPrimary,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}
