package com.aiv2ray.client.ui.permissions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aiv2ray.client.ui.theme.Copper
import com.aiv2ray.client.ui.theme.Iron
import com.aiv2ray.client.ui.theme.KeyCapButton
import com.aiv2ray.client.ui.theme.Steel
import com.aiv2ray.client.ui.theme.SteelTray

/**
 * In-app prominent disclosure required by Google Play VpnService policy.
 * https://support.google.com/googleplay/android-developer/answer/12564964
 *
 * Blocks the rest of the UI until the user accepts. This consent satisfies:
 *  (a) prominent disclosure within the app
 *  (b) affirmative user action before the VPN tunnel starts
 *  (c) clear description of which data the app does NOT collect
 */
@Composable
fun ConsentScreen(onAccept: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    0f to Steel.Base,
                    1f to Steel.Shadow
                )
            )
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Ai V2Ray Client",
                style = MaterialTheme.typography.titleLarge,
                color = Copper.Front
            )
            Text(
                text = "BEFORE YOU USE THIS APP, READ THIS",
                color = Iron.EngraveLight,
                fontSize = 12.sp
            )

            SteelTray(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "What this app does",
                        color = Copper.Highlight,
                        fontSize = 13.sp
                    )
                    Text(
                        text = """This app routes your device's network traffic through a tunnel you configure. You provide the server address and credentials yourself (vmess://, vless://, trojan://, ss:// URIs). The app does not provide, recommend, broker, host, or log any servers.""",
                        color = Iron.EngraveLight,
                        fontSize = 14.sp
                    )
                }
            }

            SteelTray(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Data this app does NOT collect",
                        color = Copper.Highlight,
                        fontSize = 13.sp
                    )
                    Text(
                        text = """•  No analytics
•  No crash reporting sent off-device
•  No advertising identifiers
•  No browsing history
•  No installed-app inventory
•  No contacts, no location, no device ID
•  No telemetry of any kind""",
                        color = Iron.EngraveLight,
                        fontSize = 14.sp
                    )
                }
            }

            SteelTray(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Data the app DOES handle",
                        color = Copper.Highlight,
                        fontSize = 13.sp
                    )
                    Text(
                        text = """•  VPN tunnel data, on-device only
•  Server URIs you paste or scan
•  Tunnel up/down state (kept locally)""",
                        color = Iron.EngraveLight,
                        fontSize = 14.sp
                    )
                }
            }

            SteelTray(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Important",
                        color = Iron.StatusLed,
                        fontSize = 13.sp
                    )
                    Text(
                        text = """Operating a VPN may be subject to laws in your jurisdiction. You are solely responsible for the content that flows through the tunnel. This app blocks ad traffic only if YOU configure it to do so — it contains no built-in ad blocking.""",
                        color = Iron.EngraveLight,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                KeyCapButton(active = true, onClick = onAccept) {
                    Text(
                        text = "I understand, continue",
                        color = Copper.Front,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}
