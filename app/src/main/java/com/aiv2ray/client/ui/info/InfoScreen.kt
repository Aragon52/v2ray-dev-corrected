package com.aiv2ray.client.ui.info

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aiv2ray.client.ui.theme.Copper
import com.aiv2ray.client.ui.theme.Iron
import com.aiv2ray.client.ui.theme.ScrewHead
import com.aiv2ray.client.ui.theme.ScrewedSteelPlate
import com.aiv2ray.client.ui.theme.Steel

@Composable
fun InfoPane() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ScrewedSteelPlate(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "DEVICE INFORMATION",
                    color = Color(0xFFD6DBE2),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 4.dp)
                )
                InfoSteelRow("App Version", "2.4.1")
                InfoSteelRow("Protocol", "VLESS / VMESS / TROJAN / SHADOWSOCKS")
                InfoSteelRow("Local IP", "192.168.x.x")
                InfoSteelRow("Server", "—")
                InfoSteelRow("Subscription", "—")
            }
        }
        ScrewedSteelPlate(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "PRIVACY & POLICY",
                    color = Copper.Highlight,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                InfoSteelRow("Data collected", "NONE")
                InfoSteelRow("Ad SDK", "NONE")
                InfoSteelRow("Tracking", "NONE")
                InfoSteelRow("Backup", "DISABLED")
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "© 2026 AiV2Ray · SECURE NETWORK",
            color = Iron.EngraveLight,
            fontSize = 11.sp,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

@Composable
private fun InfoSteelRow(label: String, value: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF4C525A), Color(0xFF30343A), Color(0xFF25282D))
                ),
                RoundedCornerShape(8.dp)
            )
            .border(1.dp, Color(0xFF656D77), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 10.dp)
    ) {
        ScrewHead(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = (-4).dp),
            size = 8.dp
        )
        ScrewHead(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = 4.dp),
            size = 8.dp
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = Color(0xFFE8ECF0),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(0.85f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = value,
                color = Copper.Highlight,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1.15f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
