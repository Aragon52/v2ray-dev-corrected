package com.aiv2ray.client.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aiv2ray.client.ui.theme.Copper
import com.aiv2ray.client.ui.theme.Iron
import com.aiv2ray.client.ui.theme.Steel
import com.aiv2ray.client.ui.theme.SteelTray

@Composable
fun SettingsPane() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SteelTray(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SectionTitle("DEVICE")
                RowKV("KILL SWITCH", "ENABLED")
                RowKV("DNS", "1.1.1.1, 1.0.0.1")
                RowKV("VPN ADDRESS", "10.66.0.2/32")
                RowKV("MTU", "1500")
            }
        }

        SteelTray(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SectionTitle("APPEARANCE")
                RowKV("THEME", "BRUSHED STEEL")
                RowKV("ACCENT", "BURNISHED COPPER")
                RowKV("TYPEFACE", "ROBOTO MONO")
            }
        }

        SteelTray(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SectionTitle("PRIVACY")
                RowKV("ANALYTICS", "NONE")
                RowKV("AD SDK", "NONE")
                RowKV("BACKUP", "DISABLED")
            }
        }

        Text(
            text = "Ai V2Ray Client · v2.4.1",
            color = Iron.EngraveLight,
            fontSize = 11.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        color = Copper.Highlight,
        fontSize = 11.sp,
        letterSpacing = 2.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun RowKV(key: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.horizontalGradient(listOf(Steel.Base, Steel.Base)), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(key, color = Iron.EngraveLight, fontSize = 12.sp, letterSpacing = 1.5.sp)
        Spacer(Modifier.weight(1f))
        Text(
            value,
            color = Copper.Highlight,
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp
        )
    }
}
