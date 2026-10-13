package com.aiv2ray.client.ui.permissions

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
            .safeDrawingPadding()
            .padding(horizontal = 20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                Brush.verticalGradient(listOf(Steel.Mid, Steel.Base)),
                                RoundedCornerShape(12.dp)
                            )
                            .border(1.dp, Steel.High, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "A",
                            color = Copper.Front,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    Column {
                        Text(
                            text = "Ai V2Ray Client",
                            color = Copper.Highlight,
                            style = MaterialTheme.typography.labelLarge
                        )
                        Text(
                            text = "PRIVATE NETWORK ROUTING",
                            color = Iron.EngraveLight,
                            fontSize = 10.sp,
                            letterSpacing = 1.2.sp
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "WELCOME · FIRST-RUN DISCLOSURE",
                        color = Copper.Front,
                        fontSize = 11.sp,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = "Your network.\nYour rules.",
                        color = Iron.EngraveLight,
                        fontSize = 30.sp,
                        lineHeight = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Before you continue, here is what this VPN does and how it handles your data.",
                        color = Iron.EngraveLight,
                        fontSize = 14.sp
                    )
                }

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
            }

            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color.Transparent, Steel.High, Color.Transparent)
                        )
                    )
            )
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                KeyCapButton(
                    modifier = Modifier.fillMaxWidth(),
                    active = true,
                    onClick = onAccept
                ) {
                    Text(
                        text = "I understand, continue",
                        color = Copper.Front,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
            Text(
                text = "Your choice is saved on this device.",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 12.dp),
                color = Iron.EngraveLight,
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
