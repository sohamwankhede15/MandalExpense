package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.CrimsonAccent
import com.example.ui.theme.MarigoldSecondary
import com.example.ui.theme.SaffronDark
import com.example.ui.theme.SaffronPrimary

@Composable
fun AkgmmLogo(
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    showSubtext: Boolean = true,
    isDarkBackground: Boolean = false
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo Emblem Image maintaining exact original aspect ratio and artwork
        Box(
            modifier = Modifier.size(size),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_akgmm_logo),
                contentDescription = "AKGMM Official Logo",
                modifier = Modifier.fillMaxSize()
            )
        }

        if (showSubtext) {
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = if (isDarkBackground) Color(0xFFFFD54F) else CrimsonAccent,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "AKGMM",
                    color = if (isDarkBackground) Color(0xFF5C0000) else Color.White,
                    fontSize = (size.value * 0.16f).coerceIn(10f, 15f).sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                )
            }

            Text(
                text = "अखिल गणेशनगर मित्र मंडळ",
                color = if (isDarkBackground) Color.White else SaffronDark,
                fontSize = (size.value * 0.15f).coerceIn(9f, 13f).sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "गणेशनगर हडपसर, पुणे - ४११०६०",
                color = if (isDarkBackground) Color(0xFFFFE082) else Color(0xFF795548),
                fontSize = (size.value * 0.13f).coerceIn(8f, 11f).sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun AkgmmHeaderBadge(
    modifier: Modifier = Modifier,
    title: String = "AKGMM",
    subtitle: String = "अखिल गणेशनगर मित्र मंडळ"
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AkgmmLogo(
            size = 38.dp,
            showSubtext = false
        )

        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                    shape = RoundedCornerShape(3.dp),
                    color = Color(0xFFFFD54F)
                ) {
                    Text(
                        text = "हडपसर, पुणे",
                        color = Color(0xFF5C0000),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }

            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
