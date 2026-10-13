package com.aiv2ray.client.ui.info

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aiv2ray.client.ui.theme.Copper
import com.aiv2ray.client.ui.theme.Iron
import com.aiv2ray.client.ui.theme.Steel
import com.aiv2ray.client.ui.theme.SteelTray

@Composable
fun InfoPane() {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SteelTray(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "DEVICE INFORMATION", color = Copper.Highlight,
                    fontSize = 11.sp, fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp)
                RowKV("APP VERSION", "2.4.1")
                RowKV("PROTOCOL", "VLESS / VMESS / TROJAN / SHADOWSOCKS")
                RowKV("LOCAL IP", "192.168.x.x")
                RowKV("SERVER", "—")
                RowKV("SUBSCRIPTION", "—")
            }
        }
        SteelTray(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "POLICY", color = Copper.Highlight,
                    fontSize = 11.sp, fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp)
                RowKV("DATA COLLECTED", "NONE")
                RowKV("AD SDK", "NONE")
                RowKV("TRACKING", "NONE")
                RowKV("BACKUP", "DISABLED")
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "© 2026 AiV2Ray · SECURE NETWORK",
            color = Iron.EngraveLight, fontSize = 11.sp,
            modifier = Modifier.padding(start = 4.dp))
    }
}

@Composable
private fun RowKV(k: String, v: String) {
    Row(modifier = Modifier
            .fillMaxWidth()
            .background(Brush.horizontalGradient(listOf(Steel.Base, Steel.Base)),
                RoundedCornerShape(6.dp))
            .border(1.dp, Steel.High, RoundedCornerShape(6.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Text(k, color = Iron.EngraveLight, fontSize = 11.sp,
            letterSpacing = 1.5.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.weight(1f))
        Text(v, color = Copper.Highlight,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
            fontSize = 12.sp)
    }
}
